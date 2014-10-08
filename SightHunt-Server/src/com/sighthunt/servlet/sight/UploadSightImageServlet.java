package com.sighthunt.servlet.sight;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.sighthunt.data.Metadata;
import com.sighthunt.network.model.Sight;
import com.sighthunt.util.DBHelper;
import com.sighthunt.util.JsonResponseWriter;
import com.sighthunt.util.ScoreKeeper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class UploadSightImageServlet extends HttpServlet {
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
		BlobKey imageKey = blobs.get("image").get(0);
		BlobKey thumbKey = blobs.get("thumb").get(0);

		if (imageKey != null && thumbKey != null) {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

			String sightKey = req.getParameter("sight_key");

			Entity sightEntity = DBHelper.getSightByKey(Long.parseLong(sightKey), datastore);
			sightEntity.setProperty(Metadata.Sight.IMAGE_KEY, imageKey.getKeyString());
			sightEntity.setProperty(Metadata.Sight.THUMB_KEY, thumbKey.getKeyString());
			sightEntity.setProperty(Metadata.Sight.LAST_MODIFIED, new Date());
			datastore.put(sightEntity);

			String creator = (String) sightEntity.getProperty(Metadata.Sight.CREATOR);

			ScoreKeeper.userSpendToCreateSight(creator, datastore, null);

			Sight returnSight = new Sight();
			returnSight.image_key = imageKey.getKeyString();
			returnSight.thumb_key = thumbKey.getKeyString();

			JsonResponseWriter.write(resp, returnSight);
		}
		// if upload failed...


	}


}
