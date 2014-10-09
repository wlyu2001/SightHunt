package com.sighthunt.util;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.MemcacheService;
import com.sighthunt.data.Metadata;
import com.sighthunt.network.model.Sight;

import java.util.Date;

public class ScoreKeeper {
	public static void userSpendToCreateSight(String user, DatastoreService datastore, MemcacheService cache) {
		Query.Filter filter = new Query.FilterPredicate(Metadata.User.USERNAME, Query.FilterOperator.EQUAL, user);

		Query q = new Query(Metadata.User.ENTITY_NAME).setFilter(filter);
		PreparedQuery pq = datastore.prepare(q);
		Entity userEntity = pq.asSingleEntity();
		long points = (Long) userEntity.getProperty(Metadata.User.POINTS);

		userEntity.setProperty(Metadata.User.POINTS, points - Constants.NEW_SIGHT_COST);

		datastore.put(userEntity);
	}

	public static String updateSightScoreAfterHuntAndReturnCreator(String sightKey, int vote, DatastoreService datastore, MemcacheService cache) {

		Key key = KeyFactory.createKey(Metadata.Sight.ENTITY_NAME, Long.parseLong(sightKey));
		Query.Filter keyFilter = new Query.FilterPredicate(Entity.KEY_RESERVED_PROPERTY, Query.FilterOperator.EQUAL, key);

		Query q = new Query(Metadata.Sight.ENTITY_NAME).setFilter(keyFilter);
		PreparedQuery pq = datastore.prepare(q);
		Entity sightEntity = pq.asSingleEntity();
		String creator = (String) sightEntity.getProperty(Metadata.Sight.CREATOR);
		long votes = (Long) sightEntity.getProperty(Metadata.Sight.VOTES);
		long hunts = (Long) sightEntity.getProperty(Metadata.Sight.HUNTS);
		Date now = new Date();

		Entity sightEntity1 = new Entity(Metadata.Sight.ENTITY_NAME);
		sightEntity1.setPropertiesFrom(sightEntity);

		sightEntity1.setProperty(Metadata.Sight.VOTES, votes + vote);
		sightEntity1.setProperty(Metadata.Sight.HUNTS, hunts + 1);
		sightEntity1.setProperty(Metadata.Sight.LAST_MODIFIED, now);

		Key key1 = datastore.put(sightEntity1);
		datastore.delete(sightEntity.getKey());

		Sight sight = (Sight) cache.get(key.getId());

		if (sight != null) {
			sight.votes = (int)votes + vote;
			sight.hunts = (int)hunts + 1;
			sight.last_modified = now.getTime();
			cache.delete(key.getId());
			cache.put(key1.getId(), sight);
		}

		return creator;
	}

	public static void userEarnFromCreatedSight(String user, int vote, DatastoreService datastore, MemcacheService cache) {
		Query.Filter filter = new Query.FilterPredicate(Metadata.User.USERNAME, Query.FilterOperator.EQUAL, user);

		Query q = new Query(Metadata.User.ENTITY_NAME).setFilter(filter);
		PreparedQuery pq = datastore.prepare(q);
		Entity userEntity = pq.asSingleEntity();
		long points = (Long) userEntity.getProperty(Metadata.User.POINTS);
		long votes = (Long) userEntity.getProperty(Metadata.User.VOTES);

		userEntity.setProperty(Metadata.User.POINTS, points + vote);
		userEntity.setProperty(Metadata.User.VOTES, votes + vote);

		datastore.put(userEntity);
	}

	public static void userEarnFromHuntSight(String user, DatastoreService datastore, MemcacheService cache) {
		Query.Filter filter = new Query.FilterPredicate(Metadata.User.USERNAME, Query.FilterOperator.EQUAL, user);

		Query q = new Query(Metadata.User.ENTITY_NAME).setFilter(filter);
		PreparedQuery pq = datastore.prepare(q);
		Entity userEntity = pq.asSingleEntity();
		long hunts = (Long) userEntity.getProperty(Metadata.User.HUNTS);

		userEntity.setProperty(Metadata.User.HUNTS, hunts + 1);

		datastore.put(userEntity);
	}


}
