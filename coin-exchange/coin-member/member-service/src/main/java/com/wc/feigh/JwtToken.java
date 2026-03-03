package com.wc.feigh;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class JwtToken {

    @JsonProperty("access_token")
    private String accessToken;

    /**
 * token
 类型
 */
    @JsonProperty("token_type")
    private String tokenType;
/**
 * refresh_token
 */
    @JsonProperty("refresh_token")
    private String refreshToken;
/**
 *
 过期时间
 */
    @JsonProperty("expires_in")
    private Long expiresIn;

    /**
     * token
     的范围
     */
    private String scope;
/**
 *
 颁发的凭证
 */
    private String jti;
}
