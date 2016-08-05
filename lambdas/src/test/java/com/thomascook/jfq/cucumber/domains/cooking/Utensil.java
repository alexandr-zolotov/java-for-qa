package com.thomascook.jfq.cucumber.domains.cooking;

import org.apache.commons.lang3.Validate;

public class Utensil {

    private final String name;

    public Utensil(String name) {
        Validate.notBlank(name);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Utensil utensil = (Utensil) o;

        return name.equals(utensil.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
