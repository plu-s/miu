package com.corydon.miu.bean;

import org.json.JSONException;
import org.json.JSONObject;

public class Unit {
    private String projectId;
    private String name;
    private String city;
    private float price;
    private String priceRange;
    private String status;
    private String showTime;
    private String venue;
    private String venueCity;
    private String verticalPic;

    public static Unit parse(JSONObject jsonObject) throws JSONException {
        Unit unit=new Unit();
        unit.projectId=jsonObject.getString("projectid");
        unit.name=jsonObject.getString("name");
        unit.city=jsonObject.getString("cityname");
        unit.price=(float)jsonObject.getDouble("price");
        unit.priceRange=jsonObject.getString("price_str");
        unit.status=jsonObject.getString("showstatus");
        unit.showTime=jsonObject.getString("showtime");
        unit.venue=jsonObject.getString("venue");
        unit.venueCity=jsonObject.getString("venuecity");
        unit.verticalPic=jsonObject.getString("verticalPic");
        return unit;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public float getPrice() {
        return price;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public String getStatus() {
        return status;
    }

    public String getShowTime() {
        return showTime;
    }

    public String getVenue() {
        return venue;
    }

    public String getVenueCity() {
        return venueCity;
    }

    public String getVerticalPic() {
        return verticalPic;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public void setVenueCity(String venueCity) {
        this.venueCity = venueCity;
    }

    public void setVerticalPic(String verticalPic) {
        this.verticalPic = verticalPic;
    }
}
