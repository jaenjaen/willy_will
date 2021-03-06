package com.willy.will.common.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

public class Location implements Parcelable {

    String locationId;
    double longitude;
    double latitude;
    String placeName;
    String addressName;
    String roadAddressName;
    String userPlaceName;

    public Location(){ }

    public Location(String locationId, double longitude, double latitude, String placeName, String addressName, String roadAddressName, String userPlaceName){
        this.locationId = locationId;
        this.longitude = longitude;
        this.latitude = latitude;
        this.placeName = placeName;
        this.addressName = addressName;
        this.roadAddressName = roadAddressName;
        this.userPlaceName = userPlaceName;
    }

    public Location(Parcel in) {
        locationId = in.readString();
        longitude = in.readDouble();
        latitude = in.readDouble();
        placeName = in.readString();
        addressName = in.readString();
        roadAddressName = in.readString();
        userPlaceName = in.readString();
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };



    public String getLocationId() { return locationId; }

    public void setLocationId(String locationId) { this.locationId = locationId; }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getRoadAddressName() { return roadAddressName; }

    public void setRoadAddressName(String roadAddressName) { this.roadAddressName = roadAddressName; }

    public String getUserPlaceName() { return userPlaceName; }

    public void setUserPlaceName(String userPlaceName) { this.userPlaceName = userPlaceName; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.locationId);
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.latitude);
        dest.writeString(this.placeName);
        dest.writeString(this.addressName);
        dest.writeString(this.roadAddressName);
        dest.writeString(this.userPlaceName);
    }
}
