package com.wc.dto.feign;

import com.wc.config.feign.OAuth2FeignConfig;
import com.wc.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name="member-service",configuration= OAuth2FeignConfig.class,path ="/users")
public interface UserServiceFeign {


    /**
     *
     用于admin-service
     里面远程调用member-service
     * @paramids
     * @return
     */
    @GetMapping("/basic/users")
    Map<Long, UserDto> getBasicUsers(@RequestParam("ids")List<Long> ids, String userName, String mobile);
}
