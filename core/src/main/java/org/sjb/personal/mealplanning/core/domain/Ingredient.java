package org.sjb.personal.mealplanning.core.domain;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class Ingredient {
    private String name;
    private double quantity;
    private String unit; // e.g., "g", "ml", "pcs", or null/empty for count

    public Ingredient() {
    }

    public Ingredient(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}