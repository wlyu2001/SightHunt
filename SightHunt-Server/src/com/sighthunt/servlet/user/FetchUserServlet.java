package com.sighthunt.servlet.user;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.sighthunt.network.model.User;
import com.sighthunt.util.DBHelper;
import com.sighthunt.util.HttpServletRequestHelper;
import com.sighthunt.util.JsonResponseWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FetchUserServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		if (!HttpServletRequestHelper.isRequestTokenValid(req)) {
			resp.sendError(401);
			return;
		}

		String username = req.getParameter("username");

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity result = DBHelper.getUserByUsername(username, datastore);
		User user = DBHelper.createUserObject(result);

		JsonResponseWriter.write(resp, user);
    }
}
