package com.sighthunt.servlet.sight;

import com.sighthunt.util.HttpServletRequestHelper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class NewSightServlet extends HttpServlet {
    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!HttpServletRequestHelper.isRequestTokenValid(req)) {
            resp.sendError(401);
            return;
        }


    }
}
