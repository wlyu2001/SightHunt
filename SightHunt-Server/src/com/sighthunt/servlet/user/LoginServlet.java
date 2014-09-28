package com.sighthunt.servlet.user;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.sighthunt.data.Metadata;
import com.sighthunt.network.model.User;
import com.sighthunt.util.DBHelper;
import com.sighthunt.util.EncryptUtils;
import com.sighthunt.util.JsonResponseWriter;
import com.sighthunt.util.TextUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");


        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity result = DBHelper.getUserByUsername(username, datastore);
        if (result != null) {
            if (TextUtils.isEmpty(password) || password.equals(result.getProperty(Metadata.User.PASSWORD))) {
                User user = new User();
                user.username = username;
                // token should be fetched from datastore
                user.token = EncryptUtils.getInstance().getToken(username);
                JsonResponseWriter.write(resp, user);
            }
        } else {
            // new facebook user
            if (TextUtils.isEmpty(password)) {
                User user = new User();
                user.username = username;
                user.token = EncryptUtils.getInstance().generateAndStoreToken(username);
                Entity userEntity = DBHelper.createNewUserEntity(user);
                datastore.put(userEntity);
                JsonResponseWriter.write(resp, user);
            }
        }
    }
}
