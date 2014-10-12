package com.sighthunt.servlet.sight;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.sighthunt.data.Metadata;
import com.sighthunt.network.model.Hunt;
import com.sighthunt.util.DBHelper;
import com.sighthunt.util.HttpServletRequestHelper;
import com.sighthunt.util.JsonResponseWriter;
import com.sighthunt.util.ScoreKeeper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

public class HuntServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		if (!HttpServletRequestHelper.isRequestTokenValid(req)) {
			resp.sendError(401);
			return;
		}

		String user = req.getParameter("user");
		long uuid = Long.parseLong(req.getParameter("uuid"));
		int vote = Integer.parseInt(req.getParameter("vote"));

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Hunt hunt = new Hunt();
		hunt.username = user;
		hunt.uuid = uuid;
		hunt.vote = vote;

		Entity huntEntity = DBHelper.createHuntEntity(hunt);

		huntEntity.setProperty(Metadata.Hunt.TIME, new Date());
		datastore.put(huntEntity);


		MemcacheService cache = MemcacheServiceFactory.getMemcacheService();

		String creator = ScoreKeeper.updateSightScoreAfterHuntAndReturnCreator(uuid, vote, datastore, cache);

		ScoreKeeper.userEarnFromCreatedSight(creator, vote, datastore, cache);

		ScoreKeeper.userEarnFromHuntSight(user, datastore, cache);

		JsonResponseWriter.write(resp, 1);

	}
}
