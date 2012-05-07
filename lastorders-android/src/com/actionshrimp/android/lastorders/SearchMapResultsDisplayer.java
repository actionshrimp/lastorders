package com.actionshrimp.android.lastorders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class SearchMapResultsDisplayer {
	
	private SearchMapActivity activity;
	private MapView map;
	
	private MapController mapController;
	private List<Overlay> mapOverlays;
	private SearchMapItemizedOverlay resultsOverlay;
	
	private List<SearchResult> results;
	private GeoPoint mapCenter;
	private int latSpan;
	private int lonSpan;
	
	LinearLayout bubble;
		
	public SearchMapResultsDisplayer(SearchMapActivity activity) {
		this.activity = activity;
		map = (MapView) activity.findViewById(R.id.mapview);
		
		map.setBuiltInZoomControls(true);
		mapController = map.getController();
		mapOverlays = map.getOverlays();

		LayoutInflater inflater = activity.getLayoutInflater();
		bubble = (LinearLayout) inflater.inflate(R.layout.bubble, map, false);
		
		ImageButton bubbleClose = (ImageButton) bubble.findViewById(R.id.bubbleclose);
		bubbleClose.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Animation fadeOut = AnimationUtils.loadAnimation(SearchMapResultsDisplayer.this.activity, R.anim.fadeout);
				bubble.startAnimation(fadeOut);
				bubble.setVisibility(View.GONE);
			}
		});
	}
	
	public void displayResults(GeoPoint searchFromLocation, List<SearchResult> results) {
		resultsOverlay = buildResultsOverlay(results);
		mapOverlays.add(resultsOverlay);
		
		zoomToResultsOverlay(searchFromLocation, results);
	}
	
	public Activity getActivity() {
		return activity;
	}
	
	public void resultTapped(int index) {
		SearchResult r = results.get(index);
		displaySearchResultBubble(r);	
	}
	
	private SearchMapItemizedOverlay buildResultsOverlay(List<SearchResult> results) {
		this.results = results;
		Drawable drawable = activity.getResources().getDrawable(R.drawable.androidmarker);
		SearchMapItemizedOverlay overlay = new SearchMapItemizedOverlay(drawable);
		overlay.setResultsDisplayer(this);
		overlay.loadResults(results);
		return overlay;		
	}
	
	private void zoomToResultsOverlay(GeoPoint searchFromLocation, List<SearchResult> results) {
		calculateZoomExtents(searchFromLocation, results);
		mapController.zoomToSpan(latSpan, lonSpan);
		mapController.animateTo(mapCenter);
	}
	
	private void calculateZoomExtents(GeoPoint searchFromLocation, List<SearchResult> results) {
		
		List<Integer> lats = new ArrayList<Integer>();
		List<Integer> lons = new ArrayList<Integer>();
		
		lats.add(searchFromLocation.getLatitudeE6());
		lons.add(searchFromLocation.getLongitudeE6());
		for (SearchResult r : results) {
			GeoPoint p = r.getPoint();
			lats.add(p.getLatitudeE6());
			lons.add(p.getLongitudeE6());
		}
		
		int latMax = Collections.max(lats); int latMin = Collections.min(lats);
		int lonMax = Collections.max(lons); int lonMin = Collections.min(lons);
		
		latSpan = latMax - latMin;
		lonSpan = lonMax - lonMin;
		
		int latMid = latMin + (latSpan / 2);
		int lonMid = lonMin + (lonSpan / 2);
		
		mapCenter = new GeoPoint(latMid, lonMid);
	}
	
	
	private void displaySearchResultBubble(final SearchResult result) {
		
		map.removeView(bubble);
		bubble.setVisibility(View.GONE);
		
		TextView venueName = (TextView) bubble.findViewById(R.id.venuename);
		venueName.setText(result.getName());
		
		TextView venueTime = (TextView) bubble.findViewById(R.id.venueopenfor);
		venueTime.setText("Open for " + result.getOpenFor() + "h");
		
		TextView venueFee = (TextView) bubble.findViewById(R.id.venuefee);
		venueFee.setText("Entry fee " + result.getPrice());
		
		MapView.LayoutParams params = new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 
				result.getPoint(), MapView.LayoutParams.BOTTOM_CENTER);
	
		bubble.setLayoutParams(params);
		
		map.addView(bubble);		
		map.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
				
		Runnable r = new Runnable() {
			public void run() {
				Animation fadeIn = AnimationUtils.loadAnimation(activity, R.anim.fadein);
				bubble.setVisibility(View.VISIBLE);
				bubble.startAnimation(fadeIn);
			}
		};

		Projection projection = map.getProjection();
		
		Point p = new Point();
		projection.toPixels(result.getPoint(), p);
		
		p.offset(0, -(bubble.getMeasuredHeight() / 2));
		GeoPoint target = projection.fromPixels(p.x, p.y);

		
		mapController.animateTo(target, r);

	}

}
