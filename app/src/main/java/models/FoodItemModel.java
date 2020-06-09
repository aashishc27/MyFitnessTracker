package models;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;

public class FoodItemModel implements Serializable{
   public ArrayList<FoodList> carbs,fats;
   public ArrayList<FoodList> protein;

    public FoodItemModel() {
    }

    public ArrayList<FoodList> getCarbs() {
        return carbs;
    }

    public void setCarbs(ArrayList<FoodList> carbs) {
        this.carbs = carbs;
    }

    public ArrayList<FoodList> getFats() {
        return fats;
    }

    public void setFats(ArrayList<FoodList> fats) {
        this.fats = fats;
    }

    public ArrayList<FoodList> getProtein() {
        return protein;
    }

    public void setProtein(ArrayList<FoodList> protein) {
        this.protein = protein;
    }
}
