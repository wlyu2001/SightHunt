package com.sighthunt.network.model;

public class Sight {
	// GSON use variable name for serialization by default
	// Key is so long that attaching all other fields in data transfer doesn't seem to be so much waste
	public long key;
	public String title;
	public String description;
	public int hunts;
	public int votes;
	public String region;
	public String image_key;
	public String thumb_key;
	public String creator;
	public float lon;
	public float lat;
	public long time_created;
	public long last_modified;
}
