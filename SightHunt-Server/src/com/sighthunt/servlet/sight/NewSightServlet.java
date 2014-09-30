package com.sighthunt.servlet.sight;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.gson.Gson;
import com.sighthunt.network.model.Sight;
import com.sighthunt.util.DBHelper;
import com.sighthunt.util.HttpServletRequestHelper;
import com.sighthunt.util.JsonResponseWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class NewSightServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!HttpServletRequestHelper.isRequestTokenValid(req)) {
            resp.sendError(401);
            return;
        }

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Sight sight = new Gson().fromJson(req.getReader(), Sight.class);

        Entity sightEntity = DBHelper.createNewSightEntity(sight);
        Key key = datastore.put(sightEntity);

        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        String blobUploadUrl = blobstoreService.createUploadUrl("/sight/upload_image");

        Sight returnSight = new Sight();
        returnSight.key = String.valueOf(key.getId());
        // use image_key for upload url for convenience
        returnSight.image_key = blobUploadUrl;
        JsonResponseWriter.write(resp, returnSight);

    }


}
