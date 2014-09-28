package com.sighthunt.util;

import javax.servlet.http.HttpServletRequest;

public class HttpServletRequestHelper {
    public static boolean isRequestTokenValid(HttpServletRequest req) {
        String token = req.getHeader("Authorization");
        String username = req.getHeader("Username");

        if (token == null || username == null)
            return false;

        return EncryptUtils.getInstance().isTokenValid(username, token);

    }
}
