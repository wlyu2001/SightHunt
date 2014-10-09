package com.sighthunt.servlet.sight;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
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

public class ListSightsByRegionServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		if (!HttpServletRequestHelper.isRequestTokenValid(req)) {
			resp.sendError(401);
			return;
		}

		String region = req.getParameter("region");
		String type = req.getParameter("type");
		int offset = Integer.parseInt(req.getParameter("offset"));
		int limit = Integer.parseInt(req.getParameter("limit"));

		// fetch keys...
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query(Metadata.Sight.ENTITY_NAME).setKeysOnly();

		Filter regionFilter = new Query.FilterPredicate(Metadata.Sight.REGION, Query.FilterOperator.EQUAL, region);
		query.setFilter(regionFilter);

		if (SightType.NEW.equals(type)) {
			query.addSort(Metadata.Sight.TIME_CREATED, SortDirection.DESCENDING);
		} else if (SightType.MOST_HUNTED.equals(type)) {
			query.addSort(Metadata.Sight.HUNTS, SortDirection.DESCENDING);
		} else if (SightType.MOST_VOTED.equals(type)) {
			query.addSort(Metadata.Sight.VOTES, SortDirection.DESCENDING);
		}

		PreparedQuery preparedQuery = datastore.prepare(query);

		List<Long> sightIds = new ArrayList<Long>();

		FetchOptions fo = FetchOptions.Builder.withOffset(offset).limit(limit);
		for (Entity entity : preparedQuery.asIterable(fo)) {
			sightIds.add(entity.getKey().getId());
		}

		JsonResponseWriter.write(resp, sightIds);
	}
}
