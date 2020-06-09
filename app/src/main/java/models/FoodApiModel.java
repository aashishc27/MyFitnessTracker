package models;

import java.io.Serializable;
import java.util.ArrayList;

public class FoodApiModel implements Serializable {
    String description;
    ArrayList<FoodNutrients> foodNutrients;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<FoodNutrients> getFoodNutrients() {
        return foodNutrients;
    }

    public void setFoodNutrients(ArrayList<FoodNutrients> foodNutrients) {
        this.foodNutrients = foodNutrients;
    }

    public class FoodNutrients implements Serializable{
        String number,name,unitName;
        float amount;


        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUnitName() {
            return unitName;
        }

        public void setUnitName(String unitName) {
            this.unitName = unitName;
        }

        public float getAmount() {
            return amount;
        }

        public void setAmount(float amount) {
            this.amount = amount;
        }
    }
}
