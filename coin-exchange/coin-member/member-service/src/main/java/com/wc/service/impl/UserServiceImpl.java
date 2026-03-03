package com.wc.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wc.config.IdAutoConfiguration;
import com.wc.domain.Sms;
import com.wc.domain.UserAuthAuditRecord;
import com.wc.domain.UserAuthInfo;
import com.wc.dto.UserDto;
import com.wc.geetest.GeetestLib;
import com.wc.mappers.UserDtoMapper;
import com.wc.model.*;
import com.wc.service.SmsService;
import com.wc.service.UserAuthAuditRecordService;
import com.wc.service.UserAuthInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wc.domain.User;
import com.wc.mapper.UserMapper;
import com.wc.service.UserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private GeetestLib geetestLib;

    @Autowired
    private UserAuthAuditRecordService userAuthAuditRecordService;

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private UserAuthInfoService userAuthInfoService;

    @Autowired
    private SmsService smsService;

    @Override
    public Page<User> findByPage(Page<User> page, String mobile, Long userId, String userName, String realName, Integer status, Integer reviewsStatus) {
        return page(page,new LambdaQueryWrapper<User>()
                .like(!StringUtils.isEmpty(userName), User::getUsername, userName)
                .like(!StringUtils.isEmpty(realName), User::getRealName, realName)
                .like(!StringUtils.isEmpty(mobile), User::getMobile, mobile)
                .eq(userId!=null,User::getId,userId)
                .eq(status!=null,User::getStatus,status)
                .eq(reviewsStatus!=null,User::getReviewsStatus,reviewsStatus));
    }

    @Override
    public Page<User> findDirectInvitePage(Page<User> page, Long userId) {
        return page(page,new LambdaQueryWrapper<User>().eq(User::getDirectInviteid,userId));
    }

    @Override
    @Transactional
    public void updateUserAuthStatus(Long id, Byte authStatus, Long authCode, String remark) {

        log.info("开始修改用户审核状态，当前用户{}，用户审核状态{}，图片的唯一code{}",id,authStatus,authCode);
        User byId = getById(id);
        if(byId!=null){
            byId.setReviewsStatus(authStatus.intValue());
            updateById(byId);
        }
        UserAuthAuditRecord userAuthAuditRecord = new UserAuthAuditRecord();
        userAuthAuditRecord.setUserId(id);
        userAuthAuditRecord.setAuthCode(authCode);
        userAuthAuditRecord.setStatus(authStatus);
        String usrStr = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        userAuthAuditRecord.setAuditUserId(Long.valueOf(usrStr));
        userAuthAuditRecord.setAuditUserName("-------------");
        userAuthAuditRecord.setRemark(remark);
    }

    @Override
    public boolean identifyVerify(Long id, UserAuthForm userAuthForm) {
        User user= getById(id);
        Assert.notNull(user,"认证的用户不存在");
        Byte authStatus=user.getAuthStatus();
        if(!authStatus.equals((byte)0)) {
            throw new IllegalArgumentException("该用户已经认证成功了");
        }
        //执行认证
        userAuthForm.check(geetestLib,redisTemplate);//极验
        //实名认证
        boolean check = IdAutoConfiguration.check(userAuthForm.getRealName(), userAuthForm.getIdCard());
        if(!check) {
            throw new IllegalArgumentException("该用户信息错误,请检查");
        }
        //设置用户的认证属性
        user.setAuthtime(new Date());
        user.setAuthStatus((byte)1);
        user.setRealName(userAuthForm.getRealName());
        user.setIdCard(userAuthForm.getIdCard());
        user.setIdCardType(userAuthForm.getIdCardType());
        return updateById(user);
    }

    @Override
    @Transactional
    public void authUser(Long id, List<String> imgs) {
        if(CollectionUtils.isEmpty(imgs)){
            throw new IllegalArgumentException("用户的身份证信息为null");
        }
        User user= getById(id);
        if(user==null) {
            throw new IllegalArgumentException("请输入正确的userId");
        }
        long authCode = snowflake.nextId();//使用时间戳(有重复) -->雪花算法
        List<UserAuthInfo>userAuthInfoList=new ArrayList<>(imgs.size());
        for(int i =0; i < imgs.size(); i++) {//有序排列
            String s = imgs.get(i);
            UserAuthInfo userAuthInfo=new UserAuthInfo();
            userAuthInfo.setImageUrl(imgs.get(i));
            userAuthInfo.setUserId(id);
            userAuthInfo.setSerialno(i +1);//设置序号,1正面2反面3手持
            userAuthInfo.setAuthCode(authCode);//是一组身份信息的标识3个图片为一组
            userAuthInfoList.add(userAuthInfo);
        }
        userAuthInfoService.saveBatch(userAuthInfoList);//批量操作
        user.setReviewsStatus(0); //等待审核
        updateById(user);         //更新用户的状态
    }

    @Override
    public boolean updatePhone(Long userId, UpdatePhoneParam updatePhoneParam) {
        // 1使用userId查询用户
        User user= getById(userId);
        // 2验证旧手机
        String oldMobile=user.getMobile();//旧的手机号--- >验证旧手机的验证码
        String oldMobileCode= stringRedisTemplate.opsForValue().get("SMS:VERIFY_OLD_PHONE:"+oldMobile);
        if(!updatePhoneParam.getOldValidateCode().equals(oldMobileCode)){
            throw new IllegalArgumentException("旧手机的验证码错误") ;
        }
        // 3验证新手机
        String newPhoneCode=
                stringRedisTemplate.opsForValue().get("SMS:CHANGE_PHONE_VERIFY:"+ updatePhoneParam.getNewMobilePhone());
        if(!updatePhoneParam.getValidateCode().equals(newPhoneCode)){
            throw new IllegalArgumentException("新手机的验证码错误") ;
        }
        // 4修改手机号
        user.setMobile(updatePhoneParam.getNewMobilePhone());
        return updateById(user);
    }

    @Override
    public boolean checkNewPhone(String mobile, String countryCode) {
        //1新的手机号,没有旧的用户使用
        int count = count(new LambdaQueryWrapper<User>().eq(User::getMobile, mobile).eq(User::getCountryCode,countryCode));
        if(count>0){//有用户占用这个手机号
            throw new IllegalArgumentException("该手机号已经被占用");
        }
        // 2向新的手机发送短信
        Sms sms=new Sms();
        sms.setMobile(mobile);
        sms.setCountryCode(countryCode);
        sms.setTemplateCode("CHANGE_PHONE_VERIFY");//模板代码-- >校验手机号
        return smsService.sendSms(sms);
    }

    @Override
    public boolean updateUserPayPwd(Long userId, UpdateLoginParam updateLoginParam) {
        User user = getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户的Id错误");
        }
        // 1 校验之前的密码 数据库的密码都是我们加密后的密码.-->
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        boolean matches = bCryptPasswordEncoder.matches(updateLoginParam.getOldpassword(), user.getPassword());
        if (!matches) {
            throw new IllegalArgumentException("用户的原始密码输入错误");
        }
        // 2 校验手机的验证码
        String validateCode = updateLoginParam.getValidateCode();
        String phoneValidateCode = stringRedisTemplate.opsForValue().get("SMS:CHANGE_LOGIN_PWD_VERIFY:" + user.getMobile());//"SMS:CHANGE_LOGIN_PWD_VERIFY:111111"
        if (!validateCode.equals(phoneValidateCode)) {
            throw new IllegalArgumentException("手机验证码错误");
        }
        user.setPassword(bCryptPasswordEncoder.encode(updateLoginParam.getNewpassword())); // 修改为加密后的密码
        return updateById(user);
    }

    @Override
    public boolean unsetPayPassword(Long userId, UnsetPayPasswordParam unsetPayPasswordParam) {
        User user = getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户的Id 错误");
        }
        String validateCode = unsetPayPasswordParam.getValidateCode();
        String phoneValidate = stringRedisTemplate.opsForValue().get("SMS:FORGOT_PAY_PWD_VERIFY:" + user.getMobile());
        if (!validateCode.equals(phoneValidate)) {
            throw new IllegalArgumentException("用户的验证码错误");
        }
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        user.setPaypassword(bCryptPasswordEncoder.encode(unsetPayPasswordParam.getPayPassword()));

        return updateById(user);
    }

    @Override
    public boolean unsetLoginPwd(UnSetPasswordParam unSetPasswordParam) {
        log.info("开始重置密码{}", JSON.toJSONString(unSetPasswordParam, true));
        // 1 极验校验
        unSetPasswordParam.check(geetestLib, redisTemplate);
        // 2 手机号码校验
        String phoneValidateCode = stringRedisTemplate.opsForValue().get("SMS:FORGOT_VERIFY:" + unSetPasswordParam.getMobile());
        if (!unSetPasswordParam.getValidateCode().equals(phoneValidateCode)) {
            throw new IllegalArgumentException("手机验证码错误");
        }
        // 3 数据库用户的校验
        String mobile = unSetPasswordParam.getMobile();
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getMobile, mobile));
        if (user == null) {
            throw new IllegalArgumentException("该用户不存在");
        }
        String encode = new BCryptPasswordEncoder().encode(unSetPasswordParam.getPassword());
        user.setPassword(encode);
        return updateById(user);
    }

    @Override
    public List<User> getUserInvites(Long userId) {
        List<User> list = list(new LambdaQueryWrapper<User>().eq(User::getDirectInviteid, userId));
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        list.forEach(user -> {
            user.setPaypassword("*********");
            user.setPassword("********");
            user.setAccessKeyId("*********");
            user.setAccessKeySecret("*********");
        });
        return list;
    }

    /**
     * 用户的注册
     *
     * @param registerParam 注册的表单参数
     * @return
     */
    @Override
    public boolean register(RegisterParam registerParam) {
        log.info("用户开始注册{}", JSON.toJSONString(registerParam, true));
        String mobile = registerParam.getMobile();
        String email = registerParam.getEmail();
        // 1 简单的校验
        if (StringUtils.isEmpty(email) && StringUtils.isEmpty(mobile)) {
            throw new IllegalArgumentException("手机号或邮箱不能同时为空");
        }
        // 2 查询校验
        int count = count(new LambdaQueryWrapper<User>()
                .eq(!StringUtils.isEmpty(email), User::getEmail, email)
                .eq(!StringUtils.isEmpty(mobile), User::getMobile, mobile)
        );
        if (count > 0) {
            throw new IllegalArgumentException("手机号或邮箱已经被注册");
        }

        registerParam.check(geetestLib, redisTemplate); // 进行极验的校验
        User user = getUser(registerParam); // 构建一个新的用户
        return save(user);
    }

    /**
     * 通过用户的信息查询用户
     *
     * @param ids      用户的批量查询,用在我们给别人远程调用时批量获取用户的数据
     * @param userName 使用用户名查询一系列用户的记录
     * @param mobile   使用用户手机查询一系列用户的记录
     * @return
     */
    @Override
    public Map<Long, UserDto> getBasicUsers(List<Long> ids, String userName, String mobile) {
        if (CollectionUtils.isEmpty(ids) && StringUtils.isEmpty(userName) && StringUtils.isEmpty(mobile)) {
            return Collections.emptyMap();
        }
        List<User> list = list(new LambdaQueryWrapper<User>()
                .in(!CollectionUtils.isEmpty(ids), User::getId, ids)
                .like(!StringUtils.isEmpty(userName), User::getUsername, userName)
                .like(!StringUtils.isEmpty(mobile), User::getMobile, mobile));
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        // 将user->userDto
        List<UserDto> userDtos = UserDtoMapper.INSTANCE.convert2Dto(list);
        Map<Long, UserDto> userDtoIdMappings = userDtos.stream().collect(Collectors.toMap(UserDto::getId, userDto -> userDto));
        return userDtoIdMappings;
    }

    private User getUser(RegisterParam registerParam) {
        User user = new User();
        user.setCountryCode(registerParam.getCountryCode());
        user.setEmail(registerParam.getEmail());
        user.setMobile(registerParam.getMobile());
        String encodePwd = new BCryptPasswordEncoder().encode(registerParam.getPassword());
        user.setPassword(encodePwd);
        user.setPaypassSetting(false);
        user.setStatus((byte) 1);
        user.setType((byte) 1);
        user.setAuthStatus((byte) 0);
        user.setLogins(0);
        user.setInviteCode(RandomUtil.randomString(6)); // 用户的邀请码
        if (!StringUtils.isEmpty(registerParam.getInvitionCode())) {
            User userPre = getOne(new LambdaQueryWrapper<User>().eq(User::getInviteCode, registerParam.getInvitionCode()));
            if (userPre != null) {
                user.setDirectInviteid(String.valueOf(userPre.getId())); // 邀请人的id , 需要查询
                user.setInviteRelation(String.valueOf(userPre.getId())); // 邀请人的id , 需要查询
            }

        }
        return user;
    }


    @Override
    public User getById(Serializable id) {
        User user = super.getById(id);
        if(user==null){
            throw new IllegalArgumentException("请输入正确的用户id");
        }
        Byte seniorAuthStatus = null;
        String seniorAuthDesc = "";
        Integer reviewStatus = user.getReviewsStatus();
        if (reviewStatus == null){
            seniorAuthStatus = 3;
            seniorAuthDesc = "资料未填写";
        }else {
            switch (reviewStatus){
                case 1:
                    seniorAuthStatus =1;
                    seniorAuthDesc ="审核通过";
                    break;
                case 2:
                seniorAuthStatus =2;
                    //查询被拒绝的原因--->审核记录里面的时间排序,
                    List<UserAuthAuditRecord> userAuthAuditRecordList= userAuthAuditRecordService.getUserAuthAuditRecordList(user.getId());
                    if(!CollectionUtils.isEmpty(userAuthAuditRecordList)) {
                        UserAuthAuditRecord userAuthAuditRecord= userAuthAuditRecordList.get(0);
                        seniorAuthDesc =userAuthAuditRecord.getRemark();
                    }
                    break;
                case 0:
                    seniorAuthStatus =0;
                    seniorAuthDesc ="等待审核";
                    break;
            }
        }
        user.setSeniorAuthStatus(seniorAuthStatus);
        user.setSeniorAuthDesc(seniorAuthDesc);
        return user;
    }
}
