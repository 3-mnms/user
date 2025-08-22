package com.tekcit.festival.utils;

import jakarta.servlet.http.HttpServletRequest;

public class TokenParseUtil {
    public static String parseToken(HttpServletRequest request){
        String bearer = request.getHeader("Authorization");
        if(bearer != null && bearer.startsWith("Bearer ")){
            return bearer.substring(7);
        }
        return null;
    }
}
