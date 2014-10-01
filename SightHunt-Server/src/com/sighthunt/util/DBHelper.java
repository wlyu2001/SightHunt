package com.sighthunt.util;

import com.google.appengine.api.datastore.*;
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
		} catch (PreparedQuery.TooManyResultsException ex) {
			return null;
		}
	}

	public static Entity getSightByKey(long key, DatastoreService datastore) {

		Query.Filter keyFilter =
				new Query.FilterPredicate(Entity.KEY_RESERVED_PROPERTY,
						Query.FilterOperator.EQUAL,
						KeyFactory.createKey(Metadata.Sight.ENTITY_NAME, key));
		Query q = new Query(Metadata.Sight.ENTITY_NAME).setFilter(keyFilter);
		try {
			Entity result = datastore.prepare(q).asSingleEntity();
			return result;
		} catch (PreparedQuery.TooManyResultsException ex) {
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

	public static Entity createSightEntity(Sight sight) {
		Entity sightEntity = new Entity(Metadata.Sight.ENTITY_NAME);
		sightEntity.setProperty(Metadata.Sight.TITLE, sight.title);
		sightEntity.setProperty(Metadata.Sight.DESCRIPTION, sight.description);
		sightEntity.setProperty(Metadata.Sight.CREATOR, sight.creator);
		sightEntity.setProperty(Metadata.Sight.REGION, sight.region);
		sightEntity.setProperty(Metadata.Sight.TIME_CREATED, new Date(sight.time_created));
		sightEntity.setProperty(Metadata.Sight.LAST_MODIFIED, new Date(sight.last_modified));
		sightEntity.setProperty(Metadata.Sight.HUNTS, sight.hunts);
		sightEntity.setProperty(Metadata.Sight.VOTES, sight.votes);
		sightEntity.setProperty(Metadata.Sight.LON, sight.lon);
		sightEntity.setProperty(Metadata.Sight.LAT, sight.lat);
		sightEntity.setProperty(Metadata.Sight.IMAGE_KEY, sight.image_key);
		sightEntity.setProperty(Metadata.Sight.THUMB_KEY, sight.thumb_key);

		return sightEntity;
	}

	public static Sight createSightObject(Entity entity) {
		Sight sight = new Sight();
		sight.title = (String) entity.getProperty(Metadata.Sight.TITLE);
		sight.description = (String) entity.getProperty(Metadata.Sight.DESCRIPTION);
		sight.creator = (String) entity.getProperty(Metadata.Sight.CREATOR);
		sight.region = (String) entity.getProperty(Metadata.Sight.REGION);
		sight.time_created = ((Date) entity.getProperty(Metadata.Sight.TIME_CREATED)).getTime();
		sight.last_modified = ((Date) entity.getProperty(Metadata.Sight.LAST_MODIFIED)).getTime();
		sight.hunts = ((Long) entity.getProperty(Metadata.Sight.HUNTS)).intValue();
		sight.votes = ((Long) entity.getProperty(Metadata.Sight.VOTES)).intValue();
		sight.lon = new Float((Double) entity.getProperty(Metadata.Sight.LON));
		sight.lat = new Float((Double) entity.getProperty(Metadata.Sight.LAT));
		sight.image_key = (String) entity.getProperty(Metadata.Sight.IMAGE_KEY);
		sight.thumb_key = (String) entity.getProperty(Metadata.Sight.THUMB_KEY);

		return sight;

	}
}
