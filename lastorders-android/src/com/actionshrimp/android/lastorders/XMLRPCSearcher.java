package com.actionshrimp.android.lastorders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import android.os.Handler;

import com.actionshrimp.android.lastorders.SearchResult.VenueType;
import com.google.android.maps.GeoPoint;

public class XMLRPCSearcher {
	
	private SearchListener listener;
	
	XMLRPCSearcher(SearchListener listener) {
		this.listener = listener;
	}
	
	public interface SearchListener {
		public void onResultsReceived(List<SearchResult> results);
		public void onSearchError(Exception e);
	}
	
	public void startSearch(GeoPoint sourceLocation, SearchOptions options) {
		new Thread(new SearchRunnable(sourceLocation, options, listener)).start();		
	}
	
	private Handler searchHandler = new Handler();
	
	private class SearchRunnable implements Runnable {
		
		private GeoPoint sourceLocation;
		private SearchOptions options;
		private SearchListener listener;
		
		SearchRunnable(GeoPoint sourceLocation, SearchOptions options, SearchListener listener) {
			this.sourceLocation = sourceLocation;
			this.options = options;
			this.listener = listener;
		}
		
		public void run() {
			try {
				
				final List<SearchResult> results = XMLRPCSearcher.this.getSearchResults(sourceLocation, options);
				
				//Send the results to the UI
				XMLRPCSearcher.this.searchHandler.post(new Runnable() {
					public void run() {
						listener.onResultsReceived(results);
					}
				});
				
			} catch (final Exception e) {	
				
				//Send the exception to be displayed in the UI
				XMLRPCSearcher.this.searchHandler.post(new Runnable() {
					public void run() {
						listener.onSearchError(e);
					}
				});
				
			}
		}
	}
	
	public List<SearchResult> getSearchResults(GeoPoint loc, SearchOptions options) throws XMLRPCException {
		
		XMLRPCClient client = new XMLRPCClient("http://actionshrimp.com:7080");

		List<SearchResult> results = new ArrayList<SearchResult>();
		List<String> locationTypes = options.getVenueTypesStringList();
		HashMap<String, Object> resultsMap = (HashMap<String, Object>) client.call("get_locations", "APIKEY", loc.getLatitudeE6() / 1000000., loc.getLongitudeE6() / 1000000., locationTypes, 10);
		
		Set<String> ids = resultsMap.keySet();
		for (String s : ids) {
			HashMap<String, Object> queryResult = (HashMap<String, Object>)resultsMap.get(s);
			
			String rName = (String)queryResult.get("name");
			String rType = ((String)queryResult.get("type")).toUpperCase();
			String rLat = (String)queryResult.get("latitude");
			String rLon = (String)queryResult.get("longitude");
			String rDist = (String)queryResult.get("distance");
			String rPrice = (String)queryResult.get("price");
			String rTime = (String) queryResult.get("time");
			
			SearchResult result = new SearchResult(rName, VenueType.valueOf(rType), new GeoPoint(Integer.parseInt(rLat), Integer.parseInt(rLon)), "FakeDesc", 0, rPrice, rTime);
			results.add(result);
		}
		
		return results;
		
	}
	
}
