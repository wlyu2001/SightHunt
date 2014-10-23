package com.sighthunt.servlet.sight;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
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

public class DeleteSightServlet extends HttpServlet {
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		if (!HttpServletRequestHelper.isRequestTokenValid(req)) {
			resp.sendError(401);
			return;
		}
		long uuid = Long.parseLong(req.getParameter(Metadata.Sight.UUID));

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		MemcacheService cache = MemcacheServiceFactory.getMemcacheService();

		Query query = new Query(Metadata.Sight.ENTITY_NAME);
		Query.Filter filter = new Query.FilterPredicate(Metadata.Sight.UUID, Query.FilterOperator.EQUAL, uuid);
		query.setFilter(filter);
		PreparedQuery preparedQuery = datastore.prepare(query);
		Entity entity = preparedQuery.asSingleEntity();
		Key key = entity.getKey();
		String imageKey = (String)entity.getProperty(Metadata.Sight.IMAGE_KEY);
		String thumbKey = (String)entity.getProperty(Metadata.Sight.THUMB_KEY);

		datastore.delete(key);

		Sight sight1 = (Sight) cache.get(key.getId());

		if (sight1 != null) {
			cache.delete(key.getId());
		}

		Entity entity1 = new Entity(Metadata.DeletedSight.ENTITY_NAME, uuid);
		entity1.setProperty(Metadata.DeletedSight.DELETION_TIME, new Date().getTime());
		datastore.put(entity1);

		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		BlobKey imageBlobKey = new BlobKey(imageKey);
		BlobKey thumbBlobKey = new BlobKey(thumbKey);
		blobstoreService.delete(imageBlobKey, thumbBlobKey);

		JsonResponseWriter.write(resp, 1);
	}
}
