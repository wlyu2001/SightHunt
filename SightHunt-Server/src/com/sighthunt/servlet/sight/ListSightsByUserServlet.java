package com.sighthunt.servlet.sight;

import com.google.appengine.api.datastore.*;
import com.sighthunt.data.Metadata;
import com.sighthunt.network.model.SightType;
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
		int offset = Integer.parseInt(req.getParameter("offset"));
		int limit = Integer.parseInt(req.getParameter("limit"));
		FetchOptions fo = FetchOptions.Builder.withOffset(offset).limit(limit);

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		List<Long> sightIds = new ArrayList<Long>();
		if (SightType.CREATED_BY.equals(type)) {

			Query query = new Query(Metadata.Sight.ENTITY_NAME).setKeysOnly();

			Query.Filter regionFilter = new Query.FilterPredicate(Metadata.Sight.CREATOR, Query.FilterOperator.EQUAL, user);
			query.setFilter(regionFilter);
			PreparedQuery preparedQuery = datastore.prepare(query);

			for (Entity entity : preparedQuery.asIterable()) {
				sightIds.add(entity.getKey().getId());
			}
		} else if (SightType.HUNTED_BY.equals(type)) {
			Query query = new Query(Metadata.Hunt.ENTITY_NAME);

			Query.Filter regionFilter = new Query.FilterPredicate(Metadata.Hunt.USER, Query.FilterOperator.EQUAL, user);
			query.setFilter(regionFilter);
			PreparedQuery preparedQuery = datastore.prepare(query);

			for (Entity entity : preparedQuery.asIterable(fo)) {
				sightIds.add((Long) entity.getProperty(Metadata.Hunt.SIGHT));
			}

		} else {
			return;
		}

		JsonResponseWriter.write(resp, sightIds);
	}
}
