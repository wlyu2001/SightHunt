package com.sighthunt.servlet.sight;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.sighthunt.data.Metadata;
import com.sighthunt.network.model.Sight;
import com.sighthunt.network.model.SightType;
import com.sighthunt.util.DBHelper;
import com.sighthunt.util.HttpServletRequestHelper;
import com.sighthunt.util.JsonResponseWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListSightsByUserServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		if (!HttpServletRequestHelper.isRequestTokenValid(req)) {
			resp.sendError(401);
			return;
		}

		String user = req.getParameter("user");
		String type = req.getParameter("type");
		long lastModified = Long.parseLong(req.getParameter("last_modified"));
		int offset = Integer.parseInt(req.getParameter("offset"));
		int limit = Integer.parseInt(req.getParameter("limit"));

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		MemcacheService cache = MemcacheServiceFactory.getMemcacheService();

		List<Sight> sights = new ArrayList<Sight>();

		if (SightType.CREATED_BY.equals(type)) {

			Query query = new Query(Metadata.Sight.ENTITY_NAME).setKeysOnly();

			Query.Filter regionFilter = new Query.FilterPredicate(Metadata.Sight.CREATOR, Query.FilterOperator.EQUAL, user);
			query.setFilter(regionFilter);
			PreparedQuery preparedQuery = datastore.prepare(query);

			for (Entity entity : preparedQuery.asIterable()) {
				// check memcache
				Key key = entity.getKey();

				Sight sight = (Sight) cache.get(key.getId());
				if (sight == null) {
					Query.Filter keyFilter = new Query.FilterPredicate(Entity.KEY_RESERVED_PROPERTY, Query.FilterOperator.EQUAL, key);

					Query q = new Query(Metadata.Sight.ENTITY_NAME).setFilter(keyFilter);
					PreparedQuery pq = datastore.prepare(q);
					Entity result = pq.asSingleEntity();
					sight = DBHelper.createSightObject(result);
					cache.put(key.getId(), sight);
				}
				if (sight.last_modified > lastModified) {
					sights.add(sight);
				}
			}

		} else if (SightType.HUNTED_BY.equals(type)) {
			Query query = new Query(Metadata.Hunt.ENTITY_NAME);

			Query.Filter regionFilter = new Query.FilterPredicate(Metadata.Hunt.USER, Query.FilterOperator.EQUAL, user);
			query.setFilter(regionFilter);
			PreparedQuery preparedQuery = datastore.prepare(query);


			FetchOptions fo = FetchOptions.Builder.withOffset(offset).limit(limit);

			for (Entity entity : preparedQuery.asIterable(fo)) {
				long sightId = (Long)entity.getProperty(Metadata.Hunt.SIGHT);
				Key key = KeyFactory.createKey(Metadata.Sight.ENTITY_NAME, sightId);

				Sight sight = (Sight) cache.get(sightId);
				if (sight == null) {
					Query.Filter keyFilter = new Query.FilterPredicate(Entity.KEY_RESERVED_PROPERTY, Query.FilterOperator.EQUAL, key);

					Query q = new Query(Metadata.Sight.ENTITY_NAME).setFilter(keyFilter);
					PreparedQuery pq = datastore.prepare(q);
					Entity result = pq.asSingleEntity();
					sight = DBHelper.createSightObject(result);
					cache.put(sightId, sight);
				}
				if (sight.last_modified > lastModified) {

					sights.add(sight);
				}
			}

		} else {
			return;
		}




		JsonResponseWriter.write(resp, sights);
	}
}
