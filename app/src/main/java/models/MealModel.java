package models;

import java.io.Serializable;

public class MealModel implements Serializable {

   private String name,calories,potion_size;
   float protein,carbs,fats,total_cal;


    public float getTotal_cal() {
        return total_cal;
    }

    public void setTotal_cal(float total_cal) {
        this.total_cal = total_cal;
    }

    public float getProtein() {
        return protein;
    }

    public void setProtein(float protein) {
        this.protein = protein;
    }

    public float getCarbs() {
        return carbs;
    }

    public void setCarbs(float carbs) {
        this.carbs = carbs;
    }

    public float getFats() {
        return fats;
    }

    public void setFats(float fats) {
        this.fats = fats;
    }

    public String getPotion_size() {
        return potion_size;
    }

    public void setPotion_size(String potion_size) {
        this.potion_size = potion_size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }
}
