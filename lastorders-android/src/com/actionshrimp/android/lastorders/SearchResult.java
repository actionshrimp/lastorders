package com.actionshrimp.android.lastorders;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;

import java.util.Date;

public class SearchResult {

	public static enum VenueType { PUB, BAR, CLUB, UNKNOWN }
	
	private String name;
	private VenueType type;
	private GeoPoint geoPoint;
	private String description;
	private double rating;
	private String price;
	private String time;
	
	public SearchResult(String name, VenueType type, GeoPoint geoPoint, String description, double rating, String price, String time) {
		this.name = name;
		this.type = type;
		this.geoPoint = geoPoint;
		this.description = description;
		this.rating = rating;
		this.price = price;
		this.time = time;
	};
			
	public String getName() {
		return name;
	}
	
	public VenueType getType() {
		return type;
	}
	
	public GeoPoint getPoint() {
		return geoPoint;
	}
	
	public double getRating() {
		return rating;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getPrice() {
		return price;
	}
		
	public String getOpenFor() {
		if ("C".equalsIgnoreCase(time)) {
			return "Closed";
		} else if (time != null){
			double t = Double.parseDouble(time);
			
			Date now = new Date();
			
			double normalizedTime = t;
			if (t < 6) {
				normalizedTime += 12;
			}
			
			Double delta = normalizedTime - (now.getHours() - 12);
			return delta.toString();
		} 
		return "?";
	}
	
	public Drawable getMarker(Activity activity) {
		switch(type) {
			case PUB:
				return activity.getResources().getDrawable(R.drawable.pubicon);
			case BAR:
				return activity.getResources().getDrawable(R.drawable.baricon);
			case CLUB:
				return activity.getResources().getDrawable(R.drawable.clubicon);
			default:
				return null;
		}
	}
	
}
