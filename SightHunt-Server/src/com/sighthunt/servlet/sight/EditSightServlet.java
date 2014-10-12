package com.sighthunt.servlet.sight;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gson.Gson;
import com.sighthunt.data.Metadata;
import com.sighthunt.network.model.Sight;
import com.sighthunt.util.HttpServletRequestHelper;
import com.sighthunt.util.JsonResponseWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

public class EditSightServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		if (!HttpServletRequestHelper.isRequestTokenValid(req)) {
			resp.sendError(401);
			return;
		}

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		MemcacheService cache = MemcacheServiceFactory.getMemcacheService();

		Sight sight = new Gson().fromJson(req.getReader(), Sight.class);

		Query query = new Query(Metadata.Sight.ENTITY_NAME);
		Query.Filter filter = new Query.FilterPredicate(Metadata.Sight.UUID, Query.FilterOperator.EQUAL, sight.uuid);
		query.setFilter(filter);
		PreparedQuery preparedQuery = datastore.prepare(query);
		Entity entity = preparedQuery.asSingleEntity();
		Key key = entity.getKey();
		Entity entity1 = new Entity(Metadata.Sight.ENTITY_NAME);
		entity1.setPropertiesFrom(entity);


		Date now = new Date();

		entity1.setProperty(Metadata.Sight.TITLE, sight.title);
		entity1.setProperty(Metadata.Sight.DESCRIPTION, sight.description);
		entity1.setProperty(Metadata.Sight.LAST_MODIFIED, now);

		Key key1 = datastore.put(entity1);
		datastore.delete(key);

		Sight sight1 = (Sight) cache.get(key.getId());

		if (sight1 != null) {
			sight1.title = sight.title;
			sight1.description = sight.description;
			sight1.last_modified = now.getTime();
			cache.delete(key.getId());
			cache.put(key1.getId(), sight1);
		}


		JsonResponseWriter.write(resp, key1.getId());
    }
}
