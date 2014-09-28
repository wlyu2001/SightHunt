package com.sighthunt.util;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.sighthunt.data.Metadata;
import com.sighthunt.network.model.User;

public class DBHelper {
    public static Entity getUserByUsername(String username, DatastoreService datastore) {

        Query.Filter propertyFilter =
                new Query.FilterPredicate("username",
                        Query.FilterOperator.EQUAL,
                        username);
        Query q = new Query("User").setFilter(propertyFilter);
        try {
            Entity result = datastore.prepare(q).asSingleEntity();
            return result;
        } catch(PreparedQuery.TooManyResultsException ex) {
            return null;
        }
    }

    public static Entity createNewUserEntity(User user) {
        Entity userEntity = new Entity(Metadata.User.ENTITY_NAME);
        userEntity.setProperty(Metadata.User.USERNAME, user.username);
        userEntity.setProperty(Metadata.User.PASSWORD, user.password);
        userEntity.setProperty(Metadata.User.EMAIL, user.email);
        userEntity.setProperty(Metadata.User.NICK, user.screen_name);
        userEntity.setProperty(Metadata.User.HUNTS, 0);
        userEntity.setProperty(Metadata.User.VOTES, 0);
        userEntity.setProperty(Metadata.User.SIGHTS, 0);
        userEntity.setProperty(Metadata.User.POINTS, Constants.BEGINNER_POINTS);

        userEntity.setProperty(Metadata.User.TOKEN, user.token);

        return userEntity;

    }
}
