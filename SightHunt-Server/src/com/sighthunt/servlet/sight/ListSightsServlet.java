package com.sighthunt.servlet.sight;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.SortDirection;
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

public class ListSightsServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		if (!HttpServletRequestHelper.isRequestTokenValid(req)) {
			resp.sendError(401);
			return;
		}

		String region = req.getParameter("region");
		String type = req.getParameter("type");
		long lastModified = Long.parseLong(req.getParameter("last_modified"));
		String username = req.getHeader("Username");

		// fetch keys...
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query(Metadata.Sight.ENTITY_NAME).setKeysOnly();

		Filter regionFilter = new Query.FilterPredicate(Metadata.Sight.REGION, Query.FilterOperator.EQUAL, region);
		//Filter creatorFilter = new Query.FilterPredicate(Metadata.Sight.CREATOR, FilterOperator.NOT_EQUAL, username);
		//Filter combinedFilter = CompositeFilterOperator.and(regionFilter, creatorFilter);
		query.setFilter(regionFilter);

		//query.addSort(Metadata.Sight.LAST_MODIFIED, SortDirection.DESCENDING);

		// should have LIMIT = 25?
		if (SightType.NEW.equals(type)) {
			query.addSort(Metadata.Sight.TIME_CREATED, SortDirection.DESCENDING);
		} else if (SightType.MOST_HUNTED.equals(type)) {
			query.addSort(Metadata.Sight.HUNTS, SortDirection.DESCENDING);
		} else if (SightType.MOST_VOTED.equals(type)) {
			query.addSort(Metadata.Sight.VOTES, SortDirection.DESCENDING);
		}

		PreparedQuery preparedQuery = datastore.prepare(query);


		// -------------------------

		MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
		List<Sight> sights = new ArrayList <Sight>();

		for (Entity entity : preparedQuery.asIterable()) {
			// check memcache
			Key key = entity.getKey();

			Sight sight = (Sight) cache.get(key.getId());
			if (sight == null) {
				Filter keyFilter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, key);

				Query q =  new Query(Metadata.Sight.ENTITY_NAME).setFilter(keyFilter);
				PreparedQuery pq = datastore.prepare(q);
				Entity result = pq.asSingleEntity();
				sight = DBHelper.createSightObject(result);
			}
			if (sight.last_modified > lastModified) {
				sights.add(sight);
			}
		}

		JsonResponseWriter.write(resp, sights);
	}
}
