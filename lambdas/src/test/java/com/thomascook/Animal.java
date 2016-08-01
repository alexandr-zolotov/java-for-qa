package com.thomascook;

/**
 * @author Alexandr Zolotov
 */
public class Animal {

    private final String kind;
    private final String description;

    public Animal(String kind, String description) {
        this.kind = kind;
        this.description = description;
    }

    public String getKind() {
        return kind;
    }

    public String getDescription() {
        return description;
    }

}
