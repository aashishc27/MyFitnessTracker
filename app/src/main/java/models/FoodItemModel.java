package models;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;

public class FoodItemModel implements Serializable{
   public ArrayList<String> carbs,fats;
   public ArrayList<Protein> protein;

    public FoodItemModel() {
    }

    public ArrayList<String> getCarbs() {
        return carbs;
    }

    public void setCarbs(ArrayList<String> carbs) {
        this.carbs = carbs;
    }

    public ArrayList<String> getFats() {
        return fats;
    }

    public void setFats(ArrayList<String> fats) {
        this.fats = fats;
    }

    public ArrayList<Protein> getProtein() {
        return protein;
    }

    public void setProtein(ArrayList<Protein> protein) {
        this.protein = protein;
    }



}
