package com.actionshrimp.android.lastorders;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

public class SearchOptionsActivity extends Activity {
    /** Called when the activity is first created. */
	
	private LinearLayout searchLayout;
	
	private RelativeLayout optionsView;
	private CheckBox optionsExpander;
	
	private RadioButton venueNearHere;
	private RadioButton venueNearPostcode;
	private EditText venuePostcode;
	
	private RadioButton venueStillOpen;
	private RadioButton venueOpenFor;
	private EditText venueOpenForHours;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.search);
        searchLayout = (LinearLayout) this.findViewById(R.id.search_layout);
        
        optionsView = (RelativeLayout) this.findViewById(R.id.search_options);
        optionsExpander = (CheckBox) this.findViewById(R.id.options_expander);
        
        venueNearHere = (RadioButton) this.findViewById(R.id.radio_here);
        venueNearPostcode = (RadioButton) this.findViewById(R.id.radio_postcode);
        venuePostcode = (EditText) this.findViewById(R.id.postcode);
        
        venueStillOpen = (RadioButton) this.findViewById(R.id.radio_still_open);
        venueOpenFor = (RadioButton) this.findViewById(R.id.radio_open_for);
        venueOpenForHours = (EditText) this.findViewById(R.id.hours);
        
        setupVenueNearRadioEvents();
        setupVenueStillOpenRadioEvents();
    }
    
    public void onResume() {
    	super.onResume();
    	searchLayout.requestFocus();
    }
    
    public void performSearch(View view) {
    	
    	SearchOptions searchOptions = buildSearchOptions();
    	Intent intent = buildIntent(searchOptions);
    	
    	this.startActivity(intent);
    	
    }
    
    public void toggleSearchOptions(View view) {
    	if (optionsExpander.isChecked()) {
			Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
    		optionsView.setVisibility(View.VISIBLE);
    		optionsView.startAnimation(fadeIn);
    	} else {
    		optionsView.setVisibility(View.GONE);
    	}
    	
    }
    
    public void setupVenueNearRadioEvents() {
    	CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//Check for isChecked prevents our setChecked(false) calls from re-triggering the listener
				if (isChecked) {
					if (buttonView == SearchOptionsActivity.this.venueNearHere) {
						SearchOptionsActivity.this.venueNearPostcode.setChecked(false);
						SearchOptionsActivity.this.venuePostcode.setEnabled(false);
						SearchOptionsActivity.this.venuePostcode.setFocusable(false);
						SearchOptionsActivity.this.venuePostcode.setFocusableInTouchMode(false);
					} else {
						SearchOptionsActivity.this.venueNearHere.setChecked(false);
						SearchOptionsActivity.this.venuePostcode.setEnabled(true);
						SearchOptionsActivity.this.venuePostcode.setFocusable(true);
						SearchOptionsActivity.this.venuePostcode.setFocusableInTouchMode(true);
					}
				}
			}
			
		};
		
		venueNearPostcode.setOnCheckedChangeListener(listener);
		venueNearHere.setOnCheckedChangeListener(listener);	
    }
    
    public void setupVenueStillOpenRadioEvents() {
    	CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//Check for isChecked prevents our setChecked(false) calls from re-triggering the listener
				if (isChecked) {
					if (buttonView == SearchOptionsActivity.this.venueStillOpen) {
						SearchOptionsActivity.this.venueOpenFor.setChecked(false);
						SearchOptionsActivity.this.venueOpenForHours.setEnabled(false);
						SearchOptionsActivity.this.venueOpenForHours.setFocusable(false);
						SearchOptionsActivity.this.venueOpenForHours.setFocusableInTouchMode(false);
					} else {
						SearchOptionsActivity.this.venueStillOpen.setChecked(false);
						SearchOptionsActivity.this.venueOpenForHours.setEnabled(true);
						SearchOptionsActivity.this.venueOpenForHours.setFocusable(true);
						SearchOptionsActivity.this.venueOpenForHours.setFocusableInTouchMode(true);
					}
				}
			}
			
		};
		
		venueOpenFor.setOnCheckedChangeListener(listener);
		venueStillOpen.setOnCheckedChangeListener(listener);	
    }
    
    private SearchOptions buildSearchOptions() {
    	
    	SearchOptions searchOptions = new SearchOptions();
    	
    	searchOptions = addVenueTypesToSearchOptions(searchOptions);
    	searchOptions = addSourceLocationToSearchOptions(searchOptions);
    	searchOptions = addTimeConstraintToSearchOptions(searchOptions);
    	
    	return searchOptions;
    	
    }
    
    private SearchOptions addVenueTypesToSearchOptions(SearchOptions searchOptions) {
    	List<Integer> toggleIDs = Arrays.asList(new Integer[] {R.id.check_pubs, R.id.check_bars, R.id.check_clubs});
    	
    	for (int i: toggleIDs) {
    		CheckBox check = (CheckBox) this.findViewById(i);
    		if (check.isChecked()) {
    			switch (i) {
    				case R.id.check_pubs:
    					searchOptions.addVenueType(SearchResult.VenueType.PUB);
    					break;
    				case R.id.check_bars:
    					searchOptions.addVenueType(SearchResult.VenueType.BAR);
    					break;
    				case R.id.check_clubs:
    					searchOptions.addVenueType(SearchResult.VenueType.CLUB);
    					break;
    			}
    		}
    	}
    	
    	return searchOptions;
    }
    
    private SearchOptions addSourceLocationToSearchOptions(SearchOptions searchOptions) {
    	
    	RadioButton usePostcode = (RadioButton) this.findViewById(R.id.radio_postcode);
    	
    	if (usePostcode.isChecked()) {
    		EditText postcode = (EditText) this.findViewById(R.id.postcode);
    		searchOptions.setSourceLocation(SearchOptions.SourceLocation.POSTCODE);
    		searchOptions.setSourceLocationPostcode(postcode.getText().toString());
    	} else {
    		searchOptions.setSourceLocation(SearchOptions.SourceLocation.CURRENT_LOCATION);
    	}
    	
    	return searchOptions;
    }
    
    private SearchOptions addTimeConstraintToSearchOptions(SearchOptions searchOptions) {
    	RadioButton openFor = (RadioButton) this.findViewById(R.id.radio_open_for);
    	
    	if (openFor.isChecked()) {
    		EditText hours = (EditText) this.findViewById(R.id.hours);
    		searchOptions.setTimeConstraint(SearchOptions.TimeConstraint.STILL_OPEN_FOR_X_HOURS);
    		searchOptions.setTimeConstraintHours(hours.getText().toString());
    	} else {
    		searchOptions.setTimeConstraint(SearchOptions.TimeConstraint.STILL_OPEN);
    	}
    	
    	return searchOptions;
    }
    
    private Intent buildIntent(SearchOptions searchOptions) {
    	Intent intent = new Intent(this, SearchMapActivity.class);
    	intent.putExtra("searchOptions", searchOptions);
    	return intent;
    }
}