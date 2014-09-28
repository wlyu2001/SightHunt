package com.sighthunt.data;

public class Metadata {

    public class User {
        public static final String ENTITY_NAME = "User";

        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String EMAIL = "email";
        public static final String VOTES = "votes";
        public static final String HUNTS = "hunts";
        public static final String POINTS = "points";
        public static final String SIGHTS = "sights";
        public static final String NICK = "nick";
        public static final String TOKEN = "token";
    }

    public class Sight {
        public static final String ENTITY_NAME = "Sight";

        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final String CREATOR = "creator";
        public static final String HUNTS = "hunts";
        public static final String VOTES = "votes";
        public static final String TIME_CREATED = "time_created";
        public static final String LON = "lon";
        public static final String LAT = "lat";
    }

    public class Hunt {
        public static final String ENTITY_NAME = "hunt";

        public static final String VOTE = "vote";
        public static final String SIGHT = "sight";
        public static final String USER = "user";

    }
}
