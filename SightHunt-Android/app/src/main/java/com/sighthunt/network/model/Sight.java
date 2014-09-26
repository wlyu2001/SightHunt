package com.sighthunt.network.model;

public class Sight {
	// GSON use variable name for serialization by default
	// Key is so long that attaching all other fields in data transfer doesn't seem to be so much waste
	public String key;
	public String title;
	public String description;
	public int hunts;
	public int votes;
	public int region;
	public String image_uri;
}
