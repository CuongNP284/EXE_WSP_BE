package com.wsp.workshophy.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SecurityUtil {
    public static Jwt getJwtClaims() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken) {
            return ((JwtAuthenticationToken) authentication).getToken();
        }
        return null;
    }

    public static Long getCurrentUserId() {
        Jwt jwt = getJwtClaims();
        return (jwt != null) ? Long.valueOf(jwt.getClaimAsString("userId")) : null;
    }

    public static String getCurrentUserName() {
        Jwt jwt = getJwtClaims();
        return (jwt != null) ? jwt.getClaimAsString("username") : null;
    }

    public static List<String> getCurrentRoles() throws JsonProcessingException {
        Jwt jwt = getJwtClaims();
        if (jwt != null) {
            String roles = jwt.getClaimAsString("scope");
            ObjectMapper objectMapper = new ObjectMapper();
            String[] roleArray = objectMapper.readValue(roles, String[].class);
            return Arrays.stream(roleArray).collect(Collectors.toList());
        }
        return null;
    }
}
