package com.sighthunt.servlet.user;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.gson.Gson;
import com.sighthunt.network.model.User;
import com.sighthunt.util.DBHelper;
import com.sighthunt.util.EncryptUtils;
import com.sighthunt.util.JsonResponseWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SignUpServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        User user = new Gson().fromJson(req.getReader(), User.class);

        Entity result = DBHelper.getUserByUsername(user.username, datastore);

        if (result == null) {

            user.token = EncryptUtils.getInstance().generateAndStoreToken(user.username);
            Entity userEntity = DBHelper.createNewUserEntity(user);
            datastore.put(userEntity);

            User returnUser = new User();
            returnUser.username = user.username;
            returnUser.token = user.token;
            JsonResponseWriter.write(resp, returnUser);
        }
    }
}
