package com.ouam.app.entity;


public class CityEntity {

    private String id;
    private String name;
    private String locality;
    private String country;
    private String locationLat;
    private String locationLon;
    private double lat;
    private double lon;
    private String pinnedAs;
    public String getPinnedAs() {
        return pinnedAs == null ? "" : pinnedAs;
    }

    public void setPinnedAs(String pinnedAs) {
        this.pinnedAs = pinnedAs;
    }


    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }



    public String getId() {
        return id == null ? "" : id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocality() {
        return locality == null ? "" : locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCountry() {
        return country == null ? "" : country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLocationLat() {
        return locationLat == null ? "" : locationLat;
    }

    public void setLocationLat(String locationLat) {
        this.locationLat = locationLat;
    }

    public String getLocationLon() {
        return locationLon == null ? "" : locationLon;
    }

    public void setLocationLon(String locationLon) {
        this.locationLon = locationLon;
    }
}
