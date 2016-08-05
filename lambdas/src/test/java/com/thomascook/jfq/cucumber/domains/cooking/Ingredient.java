package com.thomascook.jfq.cucumber.domains.cooking;

import org.apache.commons.lang3.Validate;

public class Ingredient {

    protected Amount amount;

    protected final String name;

    public Ingredient(String name, Amount amount) {
        Validate.notNull(amount);
        Validate.notBlank(name);
        this.name = name;
        this.amount = amount;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        Ingredient that = (Ingredient) other;

        return amount.equals(that.amount) && name.equals(that.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public Amount getAmount() {
        return amount;
    }

    public String getName() {
        return name;
    }
}
