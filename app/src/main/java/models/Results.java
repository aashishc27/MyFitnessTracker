package models;

public class Results {

   public FoodList carbsFood,protienFood,fatFood;

    public Results(FoodList protienFood, FoodList carbsFood, FoodList fatFood) {
        this.carbsFood = carbsFood;
        this.protienFood = protienFood;
        this.fatFood = fatFood;
    }
}
