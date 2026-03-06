package com.wc.service.impl;

import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.wc.constant.LoginConstant;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String loginType = requestAttributes.getRequest().getParameter("login_type"); // 区分时后台人员还是我们的用户登录
        if (StringUtils.isEmpty(loginType)) {
            throw new AuthenticationServiceException("登录类型不能为null");
        }
        UserDetails userDetails;
        try {
            String grantType = requestAttributes.getRequest().getParameter("grant_type"); // refresh_token 进行纠正
            if (LoginConstant.REFRESH_TYPE.equals(grantType.toUpperCase())) {
                username = adjustUsername(username, loginType);
            }

            switch (loginType) {
                case LoginConstant.ADMIN_TYPE:
                    userDetails = loadSysUserByUsername(username);
                    break;
                case LoginConstant.MEMBER_TYPE:
                    userDetails = loadMemberUserByUsername(username);
                    break;
                default:
                    throw new AuthenticationServiceException("暂不支持的登录方式:" + loginType);
            }
        } catch (IncorrectResultSizeDataAccessException e) { // 我们的用户不存在
            throw new UsernameNotFoundException("用户名" + username + "不存在");
        }
        return userDetails;
    }

    /**
     * 纠正用户的名称
     *
     * @param username  用户的id
     * @param loginType admin_type  member_type
     * @return
     */
    private String adjustUsername(String username, String loginType) {
        if (LoginConstant.ADMIN_TYPE.equals(loginType)) {
            // 管理员的纠正方式
            return jdbcTemplate.queryForObject(LoginConstant.QUERY_ADMIN_USER_WITH_ID,String.class ,username);
        }
        if (LoginConstant.MEMBER_TYPE.equals(loginType)) {
            // 会员的纠正方式
            return jdbcTemplate.queryForObject(LoginConstant.QUERY_MEMBER_USER_WITH_ID,String.class ,username);
        }
        return username;
    }

    private UserDetails loadMemberUserByUsername(String username) {
        return jdbcTemplate.queryForObject(LoginConstant.QUERY_MEMBER_SQL, new RowMapper<User>() {
            @Override
            public  User mapRow(ResultSet resultSet, int i) throws SQLException {
                if(resultSet.wasNull()) {
                    throw new UsernameNotFoundException("用户名"+username+"不存在");
                }
                long id = resultSet.getLong("id");
                String password = resultSet.getString("password");
                int status = resultSet.getInt("status");
                return new User(
                        String.valueOf(id),
                        password,
                        status==1,
                        true,
                        true,
                        true,
                        Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"))
                );
            }
        },username,username);
    }


    private UserDetails loadSysUserByUsername(String username) {
        return jdbcTemplate.queryForObject(LoginConstant.QUERY_ADMIN_SQL, new RowMapper<UserDetails>() {
            @Override
            public UserDetails mapRow(ResultSet resultSet, int i) throws SQLException {
                if(resultSet.wasNull()){
                    throw new UsernameNotFoundException("用户名"+username+"不存在");
                }
                long id = resultSet.getLong("id");
                String password = resultSet.getString("password");
                int status = resultSet.getInt("status");
                return new User(
                        String.valueOf(id),
                        password,
                        status==1,
                        true,
                        true,
                        true,
                        getSysUserPermission(id)
                );
            }
        },username);
    }

    /**
     * use id query permission
     * @param id
     * @return
     */
    private Collection<? extends GrantedAuthority> getSysUserPermission(long id) {
        String roleCode = jdbcTemplate.queryForObject(LoginConstant.QUERY_ROLE_CODE_SQL, String.class, id);
        List<String> permissions = null;
        if(roleCode.equals(LoginConstant.ADMIN_ROLE_CODE)){
            permissions = jdbcTemplate.queryForList(LoginConstant.QUERY_ALL_PERMISSIONS,String.class);
        }else {
            permissions = jdbcTemplate.queryForList(LoginConstant.QUERY_PERMISSION_SQL, String.class);
        }
        if (CollectionUtils.isEmpty(permissions)) {
            return Collections.emptySet();
        }
        return permissions.stream().distinct().map(perm -> new SimpleGrantedAuthority(perm)).collect(Collectors.toSet());
    }
}
