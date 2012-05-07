package com.actionshrimp.android.lastorders;

import java.util.List;

import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;

public class SearchMapActivity extends MapActivity {

	private SearchMapDialogHelper dialogHelper;
	private SearchMapResultsDisplayer resultsDisplayer;
	
	private GeoPoint searchSource;
	private SearchOptions searchOptions;
	
	CurrentLocationLocator locator;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.results_map);
		
		dialogHelper = new SearchMapDialogHelper(this);
		resultsDisplayer = new SearchMapResultsDisplayer(this);
	}
	
	public void onStart() {
		super.onStart();
		
		searchOptions = this.getIntent().getParcelableExtra("searchOptions");
		locateSearchSource(searchOptions);
	}
	
	public void onPause() {
		super.onPause();
		if (locator != null) {
			locator.stopListeningForUpdates();
		}
	}
	
	public void onResume() {
		super.onResume();
		if (locator != null && locator.isLocating()) {
			locator.startListeningForLocationUpdates();
		}
	}
	
	public void onStop() {
		this.finish();
		super.onStop();
	}
	
	private void locateSearchSource(SearchOptions searchOptions) {
	
		if (searchOptions.getSourceLocation() == SearchOptions.SourceLocation.POSTCODE) {
			lookupPostcode(searchOptions.getSourceLocationPostcode());
		} else {
			getCurrentLocation();
		}
	
	}
	
	private void lookupPostcode(String postcode) {
		dialogHelper.displayProgressDialog("Looking up postcode...");
		PostcodeLocator locator = new PostcodeLocator(this);
		locator.locatePostcode(searchOptions.getSourceLocationPostcode(), new PostcodeLocator.PostcodeLocatorListener() {
			
			public void onPostcodeLocationError(Exception e) {
				if (e instanceof IllegalArgumentException || e instanceof IndexOutOfBoundsException) {
					dialogHelper.displayErrorDialog("Unable to find postcode", "Please check the postcode is valid.");
				} else {
					dialogHelper.displayErrorDialog("Unable to find postcode", "Please check your data connection.");
				}
			}
			
			public void onPostcodeLocated(GeoPoint postcodeLocation) {
				searchSourceSelected(postcodeLocation);
			}
		});		
	}
	
	private void getCurrentLocation() {
		dialogHelper.displayProgressDialog("Getting current location...");
		
		
		final DialogInterface.OnClickListener acceptAccuracyListener = new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				if (which == DialogInterface.BUTTON_POSITIVE){
					locator.acceptCurrentAccuracy();
				}
			}
			
		};
		
		locator = new CurrentLocationLocator(this, new CurrentLocationLocator.CurrentLocationLocatorListener() {
			
			public void onLocationUpdateReceived(String source, double accuracyMeters) {
				dialogHelper.displayLocatorDialog(acceptAccuracyListener, source, accuracyMeters);
			}
			
			public void onCurrentLocationLocated(GeoPoint currentLocation) {
				searchSourceSelected(currentLocation);
			}
			
			public void onLocateCurrentLocationError(Exception e) {
				dialogHelper.displayErrorDialog("Unable to find current location", "Please check a location source is enabled (GPS, network).");
			}
			
		});
		
		locator.startListeningForLocationUpdates();
		
	}
	
	private void getSearchResults(GeoPoint searchSource, SearchOptions searchOptions) {
		
		dialogHelper.displayProgressDialog("Finding venues list near location...");
		
		XMLRPCSearcher.SearchListener searchListener = getSearchListener();
		XMLRPCSearcher searcher = new XMLRPCSearcher(searchListener);
		
		searcher.startSearch(searchSource, searchOptions);
		
	}
	
	private void searchSourceSelected(GeoPoint searchSource) {
		dialogHelper.dismissAllDialogs();
		this.searchSource = searchSource;
		
		getSearchResults(searchSource, searchOptions);
	}
	

	
	private void displayResults(List<SearchResult> results) {
		dialogHelper.dismissAllDialogs();
		resultsDisplayer.displayResults(searchSource, results);
	}
		
	private XMLRPCSearcher.SearchListener getSearchListener() {
		XMLRPCSearcher.SearchListener listener = new XMLRPCSearcher.SearchListener() {
			
			public void onResultsReceived(final List<SearchResult> results) {
				SearchMapActivity.this.displayResults(results);
			}

			public void onSearchError(Exception e) {
				//dialogHelper.displayErrorDialog("Unable to fetch results", "Please check your data connection.");
				dialogHelper.displayErrorDialog("Unable to fetch results", e.getMessage());
				
			}			
		};
		
		return listener;
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
}
