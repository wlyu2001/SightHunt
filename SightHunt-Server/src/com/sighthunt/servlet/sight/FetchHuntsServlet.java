package com.sighthunt.servlet.sight;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sighthunt.data.Metadata;
import com.sighthunt.network.model.Sight;
import com.sighthunt.network.model.SightFetchType;
import com.sighthunt.util.DBHelper;
import com.sighthunt.util.HttpServletRequestHelper;
import com.sighthunt.util.JsonResponseWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FetchHuntsServlet extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		if (!HttpServletRequestHelper.isRequestTokenValid(req)) {
			resp.sendError(401);
			return;
		}

		String user = req.getParameter("user");
		long lastUpdate = Long.parseLong(req.getParameter("last_update"));

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Query.Filter userFilter = new Query.FilterPredicate(Metadata.Hunt.USER, Query.FilterOperator.EQUAL, user);
		Query.Filter dateFilter = new Query.FilterPredicate(Metadata.Hunt.TIME, Query.FilterOperator.GREATER_THAN, new Date(lastUpdate));
		Query.Filter combinedFilter = Query.CompositeFilterOperator.and(userFilter, dateFilter);

		Query q = new Query(Metadata.Hunt.ENTITY_NAME).setFilter(combinedFilter);

		PreparedQuery pq = datastore.prepare(q);
		List<Long> uuids = new ArrayList<Long>();

		for (Entity entity : pq.asIterable()) {
			uuids.add((Long)entity.getProperty(Metadata.Hunt.SIGHT_UUID));

			long time = ((Date)entity.getProperty(Metadata.Hunt.TIME)).getTime();
			if (time > lastUpdate)
				lastUpdate = time;
		}
		uuids.add(lastUpdate);

		JsonResponseWriter.write(resp, uuids);
	}
}
