package com.actionshrimp.android.lastorders;

import java.util.List;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;

public class CurrentLocationLocator {
	
	private Context context;
	private CurrentLocationLocatorListener listener;
	
	private boolean isListeningForUpdates = false;
	private LocationListener locationListener;
	private Location currentBestLocation;
	
	public CurrentLocationLocator(Context context, CurrentLocationLocatorListener listener) {
		this.context = context;
		this.listener = listener;
	}
	
	public interface CurrentLocationLocatorListener {
		public void onLocationUpdateReceived(String source, double accuracyMeters);
		public void onCurrentLocationLocated(GeoPoint currentLocation);
		public void onLocateCurrentLocationError(Exception e);
	}
	
	public boolean isLocating() {
		return isListeningForUpdates;
	}
	
	public void startListeningForLocationUpdates() {
		
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		
		LocationListener locationListener = getLocationListener();
		
		List<String> providers = locationManager.getAllProviders();
		
		for (String p : providers) {
			locationManager.requestLocationUpdates(p, 0, 0, locationListener, this.context.getMainLooper());
		}
		
		isListeningForUpdates = true;
		
	}
	
	public void stopListeningForUpdates() {
		if (isListeningForUpdates) {
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
					CurrentLocationLocator.this.changedLocation(location);
				}
				
			    public void onStatusChanged(String provider, int status, Bundle extras) {}
	
			    public void onProviderEnabled(String provider) {}
	
			    public void onProviderDisabled(String provider) {}
			};
		}
		
		return locationListener;
	}
	
	private void changedLocation(Location location) {
		if ( isBetterLocation(location, currentBestLocation) ) {
			currentBestLocation = location;
			
			if ( location.getAccuracy() < 50 ) {
				acceptCurrentAccuracy();
			} else {
				listener.onLocationUpdateReceived(location.getProvider(), location.getAccuracy());
			}
			
		}
	}
	
	public void acceptCurrentAccuracy() {
		stopListeningForUpdates();
		listener.onCurrentLocationLocated(geopointFromLocation(currentBestLocation));
	}
	
	private GeoPoint geopointFromLocation(Location l) {
		GeoPoint g = new GeoPoint(udeg(l.getLatitude()), udeg(l.getLongitude()));
		return g;
	}
	
	private int udeg(double value) {
		return (int) (value*1000000);
	}
	
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isNewer = timeDelta > 0;

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}

}
