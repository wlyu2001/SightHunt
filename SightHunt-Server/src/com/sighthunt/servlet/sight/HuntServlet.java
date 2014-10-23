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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
		Date now = new Date();
		huntEntity.setProperty(Metadata.Hunt.TIME, now);
		datastore.put(huntEntity);


		MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
		// no matter what let's get the point for hunter
		ScoreKeeper.userEarnFromHuntSight(user, datastore, cache);

		Object[] objs = ScoreKeeper.updateSightScoreAfterHuntAndReturnCreator(uuid, vote, datastore, cache);
		// if objs is null. this means the sight is already delete by creator. we return key 0 to notify client
		// to remove it from cache. and skip giving point to creator..
		List<Long> results = new ArrayList<Long>();
		if (objs != null) {
			String creator = (String) objs[0];
			long key = (Long) objs[1];
			ScoreKeeper.userEarnFromCreatedSight(creator, vote, datastore, cache);

			results.add(key);
		} else {
			results.add(0l);
		}

		results.add(now.getTime());
		JsonResponseWriter.write(resp, results);

	}
}
