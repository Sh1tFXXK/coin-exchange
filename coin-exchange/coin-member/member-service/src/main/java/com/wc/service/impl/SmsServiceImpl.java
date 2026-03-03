package com.wc.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.alicloud.sms.ISmsService;
import com.alibaba.fastjson.JSON;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wc.domain.Config;
import com.wc.domain.Sms;
import com.wc.mapper.SmsMapper;
import com.wc.service.ConfigService;
import com.wc.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SmsServiceImpl extends ServiceImpl<SmsMapper, Sms> implements SmsService {

    @Autowired
    private ISmsService smsService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public boolean sendSms(Sms sms) {
        log.info("发送短信{}", JSON.toJSONString(sms,true));
        SendSmsRequest request= buildSmsRequest(sms);
        try{
            SendSmsResponse sendSmsResponse=smsService.sendSmsRequest(request);
            log.info("发送的结果为{}",JSON.toJSONString(sendSmsResponse,true));
            String code=sendSmsResponse.getCode();
            if("OK".equals(code)) {//发送成功,否则失败
                sms.setStatus(1);
                return save(sms);
            }else{
                return false;
            }
        }catch(ClientException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     *
     构建发送短信的请求对象
     *
     */
    private SendSmsRequest buildSmsRequest(Sms sms) {
        SendSmsRequest sendSmsRequest=new SendSmsRequest();
        sendSmsRequest.setPhoneNumbers(sms.getMobile());//发送给谁
        // Required:SMS-SignName-could be found in sms console
        Config signConfig=configService.getConfigByCode("SIGN");
        sendSmsRequest.setSignName(signConfig.getValue());//设置签名---公司里面不会随便的改变--Config查询签名
        // Required:Template-could be found in sms console
        Config configByCode=configService.getConfigByCode(sms.getTemplateCode());
        if(configByCode==null) {
            throw new IllegalArgumentException("您输入的签名不存在");
        }
        sendSmsRequest.setTemplateCode(configByCode.getValue());//模板的Code--动态改变
        //Required:Theparamofsmstemplate.Forexmaple,if the template is "Hello,your verification code is ${code}". The param should be like following value
        String code= RandomUtil.randomNumbers(6);
        //需要把code保存到redis里面
        // key: SMS:VERIFY_OLD_PHONE:15827293117value: 123456
        redisTemplate.opsForValue().set("SMS:"+sms.getTemplateCode()+":"+sms.getMobile(), code,5, TimeUnit.MINUTES);
        sendSmsRequest.setTemplateParam("{\"code\":\""+code+"\"}");
        String desc=configByCode.getDesc();//sign:您的验证码${code}，该验证码5分钟内有效，请勿泄漏于他人！
        String content=signConfig.getValue() +":"+desc.replaceAll("\\$\\{code{2}}",code);
        sms.setContent(content);//最后短信的内容
        return sendSmsRequest;
    }
}
