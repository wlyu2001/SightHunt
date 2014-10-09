package com.sighthunt.servlet.sight;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sighthunt.data.Metadata;
import com.sighthunt.network.model.Sight;
import com.sighthunt.util.DBHelper;
import com.sighthunt.util.HttpServletRequestHelper;
import com.sighthunt.util.JsonResponseWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FetchSightsServlet extends HttpServlet {
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		if (!HttpServletRequestHelper.isRequestTokenValid(req)) {
			resp.sendError(401);
			return;
		}

		Type listType = new TypeToken<ArrayList<Long>>() {}.getType();
		List<Long> sightIds = new Gson().fromJson(req.getReader(), listType);

		MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		List<Sight> sights = new ArrayList<Sight>();

		for (long sightId : sightIds) {

			Sight sight = (Sight) cache.get(sightId);
			if (sight == null) {
				Key key = KeyFactory.createKey(Metadata.Sight.ENTITY_NAME, sightId);
				Query.Filter keyFilter = new Query.FilterPredicate(Entity.KEY_RESERVED_PROPERTY, Query.FilterOperator.EQUAL, key);

				Query q = new Query(Metadata.Sight.ENTITY_NAME).setFilter(keyFilter);
				PreparedQuery pq = datastore.prepare(q);
				Entity result = pq.asSingleEntity();
				sight = DBHelper.createSightObject(result);
				cache.put(sightId, sight);
			}
			sights.add(sight);
		}

		JsonResponseWriter.write(resp, sights);
	}
}
