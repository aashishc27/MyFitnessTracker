package models;

import java.io.Serializable;

public class FoodModel implements Serializable {

    String name;
    boolean isSelected;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
