package models;

import com.google.gson.Gson;

import java.io.Serializable;

public class Calories implements Serializable {
    public float totalcalories;
    public float totalfat;
    public float totalcarbs;
    public float totalprotein;

    public String serialize() {
        // Serialize this class into a JSON string using GSON
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public float getTotalcalories() {
        return totalcalories;
    }

    public void setTotalcalories(float totalcalories) {
        this.totalcalories = totalcalories;
    }

    public float getTotalfat() {
        return totalfat;
    }

    public void setTotalfat(float totalfat) {
        this.totalfat = totalfat;
    }

    public float getTotalcarbs() {
        return totalcarbs;
    }

    public void setTotalcarbs(float totalcarbs) {
        this.totalcarbs = totalcarbs;
    }

    public float getTotalprotein() {
        return totalprotein;
    }

    public void setTotalprotein(float totalprotein) {
        this.totalprotein = totalprotein;
    }
}