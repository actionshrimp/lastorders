package com.actionshrimp.android.lastorders;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;

import com.google.android.maps.GeoPoint;

public class PostcodeLocator {
	
	private Context context;

	public PostcodeLocator(Context context) {
		this.context = context;
	}
	
	public interface PostcodeLocatorListener {
		public void onPostcodeLocated(final GeoPoint postcodeLocation);
		public void onPostcodeLocationError(Exception e);
	}
	
	public void locatePostcode(String postcode, PostcodeLocatorListener listener) {
		new Thread(new PostcodeLocateRunnable(postcode, listener)).start();		
	}
	
	private Handler lookupHandler = new Handler();
	
	private class PostcodeLocateRunnable implements Runnable {
		
		private String postcode;
		private PostcodeLocatorListener listener;
		
		PostcodeLocateRunnable(String postcode, PostcodeLocatorListener listener) {
			this.postcode = postcode;
			this.listener = listener;
		}
		
		public void run() {
			try {
				
				final GeoPoint postcodeLocation = PostcodeLocator.this.locatePostcode(postcode);
				
				//Send the results to the UI
				PostcodeLocator.this.lookupHandler.post(new Runnable() {
					public void run() {
						listener.onPostcodeLocated(postcodeLocation);
					}
				});
				
			} catch (final Exception e) {	
				//Send the exception to be displayed in the UI
				PostcodeLocator.this.lookupHandler.post(new Runnable() {
					public void run() {
						listener.onPostcodeLocationError(e);
					}
				});
				
			}
		}
	}
	
	private GeoPoint locatePostcode(String postcode) throws IllegalArgumentException, IOException {
		
		GeoPoint sourceLocation = null;
		
		Geocoder geocoder = new Geocoder(this.context);
		
		List<Address> addressList = geocoder.getFromLocationName(postcode, 1);
		sourceLocation = new GeoPoint(udeg(addressList.get(0).getLatitude()), udeg(addressList.get(0).getLongitude()));

		return sourceLocation;
		
	}
	
	public int udeg(double value) {
		return (int) (value*1000000);
	}

}
