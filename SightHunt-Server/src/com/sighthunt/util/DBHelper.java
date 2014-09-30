package com.sighthunt.util;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.sighthunt.data.Metadata;
import com.sighthunt.network.model.Sight;
import com.sighthunt.network.model.User;

import java.util.Date;

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

    public static Entity createNewSightEntity(Sight sight) {
        Entity sightEntity = new Entity(Metadata.Sight.ENTITY_NAME);
        sightEntity.setProperty(Metadata.Sight.TITLE, sight.title);
        sightEntity.setProperty(Metadata.Sight.DESCRIPTION, sight.description);
        sightEntity.setProperty(Metadata.Sight.CREATOR, sight.creator);
        sightEntity.setProperty(Metadata.Sight.TIME_CREATED, new Date(sight.time_created));
        sightEntity.setProperty(Metadata.Sight.HUNTS, 0);
        sightEntity.setProperty(Metadata.Sight.VOTES, 0);
        sightEntity.setProperty(Metadata.Sight.LON, sight.lon);
        sightEntity.setProperty(Metadata.Sight.LAT, sight.lat);

        return sightEntity;

    }
}
