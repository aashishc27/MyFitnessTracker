package models;

import java.io.Serializable;

public class MealModel implements Serializable {

   private String name,calories,potion_size;


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
