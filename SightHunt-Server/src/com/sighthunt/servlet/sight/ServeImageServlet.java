package com.sighthunt.servlet.sight;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class ServeImageServlet extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		String imageKey = req.getParameter("image_key");
		BlobKey blobKey = new BlobKey(imageKey);
		resp.setHeader("Cache-control", "public, max-age=3600000");
		blobstoreService.serve(blobKey, resp);
	}
}
