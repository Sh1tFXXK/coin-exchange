package com.wc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wc.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wc.dto.UserDto;
import com.wc.model.*;

import java.util.List;
import java.util.Map;

public interface UserService extends IService<User>{


    Page<User> findByPage(Page<User> page, String mobile, Long userId, String userName, String realName, Integer status, Integer reviewsStatus);

    Page<User> findDirectInvitePage(Page<User> page, Long userId);

    void updateUserAuthStatus(Long id, Byte authStatus, Long authCode, String remark);

    boolean identifyVerify(Long Id, UserAuthForm userAuthForm);

    void authUser(Long aLong, List<String> list);

    boolean updatePhone(Long aLong, UpdatePhoneParam updatePhoneParam);

    boolean checkNewPhone(String mobile, String countryCode);

    boolean updateUserPayPwd(Long userId, UpdateLoginParam updateLoginParam);

    boolean unsetPayPassword(Long userId, UnsetPayPasswordParam unsetPayPasswordParam);

    boolean unsetLoginPwd(UnSetPasswordParam unSetPasswordParam);

    List<User> getUserInvites(Long userId);

    boolean register(RegisterParam registerParam);

    Map<Long, UserDto> getBasicUsers(List<Long> ids, String userName, String mobile);
}
