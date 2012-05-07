package com.actionshrimp.android.lastorders;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.google.android.maps.GeoPoint;

public class SearchSourceLocator {
	
	private Context context;
	private SourceLocateListener listener;
	
	private LocationListener locationListener = null;
	private boolean listeningForUpdates = false;
	
	private boolean sourceLocated = false;
	private GeoPoint sourceLocation;

	public SearchSourceLocator(Context context) {
		this.context = context;
	}
	
	public void setSourceLocateListener(SourceLocateListener listener) {
		this.listener = listener;
	}

	public interface SourceLocateListener {
		public void onSourceLocationSelected(final GeoPoint sourceLocation);
		public void onSourceLocationError(Exception e);
	}
	
	public void selectSourceLocation(SearchOptions options) {
		new Thread(new SourceLocateRunnable(options, listener)).start();		
	}
	
	private Handler locateHandler = new Handler();
	
	private class PostcodeLookupRunnable implements Runnable {
		
		private SearchOptions options;
		private SourceLocateListener listener;
		
		SourceLocateRunnable(SearchOptions options, SourceLocateListener listener) {
			this.options = options;
			this.listener = listener;
		}
		
		public void run() {
			try {
				
				SearchSourceLocator.this.locateSearchSource(options);
				//Block while waiting for a source
				while (!sourceLocated) {}
				
				SearchSourceLocator.this.stopListeningForUpdates();
				final GeoPoint finalSource = sourceLocation;
				
				//Send the results to the UI
				SearchSourceLocator.this.locateHandler.post(new Runnable() {
					public void run() {
						listener.onSourceLocationSelected(finalSource);
					}
				});
				
			} catch (final Exception e) {	
				SearchSourceLocator.this.stopListeningForUpdates();
				//Send the exception to be displayed in the UI
				SearchSourceLocator.this.locateHandler.post(new Runnable() {
					public void run() {
						listener.onSourceLocationError(e);
					}
				});
				
			}
		}
	}
	
	public void locateSearchSource(SearchOptions options) throws IllegalArgumentException, IOException {
		
		if (options.getSourceLocation() == SearchOptions.SourceLocation.POSTCODE) {
			sourceLocated(sourceLocationFromPostcode(options.getSourceLocationPostcode()));
		} else {
			startCheckingCurrentLocation();
		}
		
	}
	
	private void sourceLocated(GeoPoint sourceLocation) {
		this.sourceLocation = sourceLocation;
		sourceLocated = true;
	}
	
	private GeoPoint sourceLocationFromPostcode(String postcode) throws IllegalArgumentException, IOException{
		
		GeoPoint sourceLocation = null;
		
		Geocoder geocoder = new Geocoder(this.context);
		
		List<Address> addressList = geocoder.getFromLocationName(postcode, 1);
		sourceLocation = new GeoPoint(udeg(addressList.get(0).getLatitude()), udeg(addressList.get(0).getLongitude()));

		return sourceLocation;
		
	}
	
	private void startCheckingCurrentLocation() {

		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		
		String bestProvider = locationManager.getBestProvider(criteria, true);
			
		LocationListener locationListener = getLocationListener();
		
		locationManager.requestLocationUpdates(bestProvider, 0, 0, locationListener, Looper.getMainLooper());
		listeningForUpdates = true;
		
	}
	
	public void stopListeningForUpdates() {
		if (listeningForUpdates) {
			LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			
			LocationListener locationListener = getLocationListener();
			if (locationListener != null) {
				locationManager.removeUpdates(locationListener);
			}
		}
	}
	
	private LocationListener getLocationListener() {
		if (locationListener == null) {
			locationListener = new LocationListener() {
				public void onLocationChanged(Location location) {
					SearchSourceLocator.this.checkChangedLocation(location);
				}
				
			    public void onStatusChanged(String provider, int status, Bundle extras) {}
	
			    public void onProviderEnabled(String provider) {}
	
			    public void onProviderDisabled(String provider) {}
			};
		}
		
		return locationListener;
	}
	
	private void checkChangedLocation(Location location) {
		GeoPoint source = new GeoPoint(udeg(location.getLatitude()), udeg(location.getLongitude()));
		sourceLocated(source);
	}
	
	private GeoPoint getCurrentLocation() {
		GeoPoint currentLocation = new GeoPoint((int) (1000000 * 51.51885), (int) (1000000 * -0.10812));
		return currentLocation;
	}
	
	public int udeg(double value) {
		return (int) (value*1000000);
	}

}
