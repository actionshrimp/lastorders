package com.actionshrimp.android.lastorders;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class SearchMapItemizedOverlay extends ItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
	private SearchMapResultsDisplayer displayer;
	
	public SearchMapItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}
	
	public void setResultsDisplayer(SearchMapResultsDisplayer displayer) {
		this.displayer = displayer;
	}
	
	public void loadResults(List<SearchResult> results) {
		
		Activity a = displayer.getActivity();
		
		for (SearchResult r: results) {
			OverlayItem item = new OverlayItem(r.getPoint(), r.getName(), r.getDescription());
			
			Drawable marker = r.getMarker(a);
			if (marker != null) {
				item.setMarker(boundCenterBottom(marker));
			}
			
			addOverlay(item);
		}
	}
	
	public void addOverlay(OverlayItem item) {
		items.add(item);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return items.get(i);
	}

	@Override
	public int size() {
		return items.size();
	}
	
	@Override
	public boolean onTap(int index) {
		displayer.resultTapped(index);
		return true;
	}

}
