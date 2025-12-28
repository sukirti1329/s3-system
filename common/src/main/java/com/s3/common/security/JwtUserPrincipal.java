package com.s3.common.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtUserPrincipal {

    private String userId;     // UUID from users table
    private String username;   // login name
    private String role;       // USER / ADMIN (for now USER)
}

