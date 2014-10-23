package com.sighthunt.servlet.sight;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.sighthunt.data.Metadata;
import com.sighthunt.network.model.Sight;
import com.sighthunt.util.HttpServletRequestHelper;
import com.sighthunt.util.JsonResponseWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

public class ReportSightServlet extends HttpServlet {
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		if (!HttpServletRequestHelper.isRequestTokenValid(req)) {
			resp.sendError(401);
			return;
		}
		long uuid = Long.parseLong(req.getParameter(Metadata.ReportedSight.SIGHT_UUID));
		String reporter = req.getParameter(Metadata.ReportedSight.REPORTER);
		int reason = Integer.parseInt(req.getParameter(Metadata.ReportedSight.REASON));

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Entity entity = new Entity(Metadata.ReportedSight.ENTITY_NAME);

		entity.setProperty(Metadata.ReportedSight.REPORT_TIME, new Date().getTime());
		entity.setProperty(Metadata.ReportedSight.SIGHT_UUID, uuid);
		entity.setProperty(Metadata.ReportedSight.REASON, reason);
		entity.setProperty(Metadata.ReportedSight.REPORTER, reporter);
		datastore.put(entity);

		JsonResponseWriter.write(resp, 1);
	}
}
