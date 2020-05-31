package models;

import java.io.Serializable;

public class Protein implements Serializable {

    public String type,val;

    public Protein() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }
}
