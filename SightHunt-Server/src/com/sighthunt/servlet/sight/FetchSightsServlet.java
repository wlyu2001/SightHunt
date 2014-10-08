package com.sighthunt.servlet.sight;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sighthunt.network.model.Sight;
import com.sighthunt.util.HttpServletRequestHelper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FetchSightsServlet extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		if (!HttpServletRequestHelper.isRequestTokenValid(req)) {
			resp.sendError(401);
			return;
		}

		Type listType = new TypeToken<ArrayList<Long>>() {}.getType();

		List<Long> sightIds = new Gson().fromJson(req.getReader(), listType);
	}
}
