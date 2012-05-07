package com.actionshrimp.android.lastorders;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.os.Parcel;
import android.os.Parcelable;

import com.actionshrimp.android.lastorders.SearchResult.VenueType;

public class SearchOptions implements Parcelable {

	public static enum SourceLocation {CURRENT_LOCATION, POSTCODE};
	public static enum TimeConstraint {STILL_OPEN, STILL_OPEN_FOR_X_HOURS};
	
	private Set<VenueType> venueTypes;
	private SourceLocation sourceLocation;
	private TimeConstraint timeConstraint;
	private String sourceLocationPostcode;
	private double hoursStillOpenFor;
	
	public SearchOptions() {
		venueTypes = new HashSet<VenueType>();
	}
			
	public void addVenueType(VenueType t) {
		venueTypes.add(t);
	}
	
	private List<VenueType> getVenueTypesList() {
		List<VenueType> types = new ArrayList<VenueType>(venueTypes);
		return types;
	}
	
	public List<String> getVenueTypesStringList() {
		List<String> VenueTypes = new ArrayList<String>();
		
		for (VenueType l : venueTypes) {
			VenueTypes.add(l.name());			
		}
		
		return VenueTypes;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(getVenueTypesList());
		dest.writeString(sourceLocation.toString());
		dest.writeString(sourceLocationPostcode);
		dest.writeString(timeConstraint.toString());
		dest.writeDouble(hoursStillOpenFor);
	}
	
	public static final Parcelable.Creator<SearchOptions> CREATOR = new Parcelable.Creator<SearchOptions>() {

		public SearchOptions createFromParcel(Parcel source) {
			return new SearchOptions(source);
		}

		public SearchOptions[] newArray(int size) {
			return new SearchOptions[size];
		}
	};
	
	private SearchOptions(Parcel source) {
		List<VenueType> typeList = new ArrayList<VenueType>();
		source.readList(typeList, null);
		venueTypes = new HashSet<VenueType>(typeList);
		
		sourceLocation = SourceLocation.valueOf(source.readString());
		sourceLocationPostcode = source.readString();
		timeConstraint = TimeConstraint.valueOf(source.readString());
		hoursStillOpenFor = source.readDouble();	
	}

	public void setSourceLocation(SourceLocation locationSource) {
		this.sourceLocation = locationSource;		
	}
	
	public SourceLocation getSourceLocation() {
		return this.sourceLocation;
	}

	public void setSourceLocationPostcode(String postcode) {
		this.sourceLocationPostcode = postcode;		
	}
	
	public String getSourceLocationPostcode() {
		return this.sourceLocationPostcode;
	}

	public void setTimeConstraint(TimeConstraint timeConstraint) {
		this.timeConstraint = timeConstraint;
	}

	public void setTimeConstraintHours(String hoursString) {
		this.hoursStillOpenFor = Double.parseDouble(hoursString);
	}

}