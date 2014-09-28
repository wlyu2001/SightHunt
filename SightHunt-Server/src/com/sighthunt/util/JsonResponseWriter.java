package com.sighthunt.util;

import com.google.gson.Gson;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JsonResponseWriter {
    public static void write(HttpServletResponse resp, Object obj) throws IOException {
        String json = new Gson().toJson(obj);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
    };
}
