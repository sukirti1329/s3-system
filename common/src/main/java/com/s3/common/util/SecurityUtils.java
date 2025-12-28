package com.s3.common.util;

import com.s3.common.exception.UnauthorizedAccessException;
import com.s3.common.security.JwtUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    public static String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof JwtUserPrincipal p)) {
            throw new UnauthorizedAccessException("User not authenticated");
        }
        return p.getUserId();
    }
}
