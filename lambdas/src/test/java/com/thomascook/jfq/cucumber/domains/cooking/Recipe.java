package com.thomascook.jfq.cucumber.domains.cooking;

import org.apache.commons.lang3.Validate;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Recipe {

    final String name;
    final Integer time; //in minutes
    final String level;

    private List<Instruction> instructions;

    public Recipe(String name, Integer time, String level) {
        this.name = name;
        this.time = time;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public Integer getTime() {
        return time;
    }

    public String getLevel() {
        return level;
    }

    public List<Instruction> getInstructions() {
        return Collections.unmodifiableList(instructions);
    }

    public Recipe withInstruction(Instruction instruction){
        Validate.notNull(instruction);
        if(instructions == null) {
            instructions = new LinkedList<>();
        }
        instructions.add(instruction);
        return this;
    }

    public static class Instruction {

        private final String ingredient;
        private final int amount;
        private final String action;

        public Instruction(String ingredient, int amount, String action) {
            Validate.notBlank(action);
            Validate.notBlank(ingredient);
            Validate.isTrue(amount > 0);

            this.ingredient = ingredient;
            this.amount = amount;
            this.action = action;
        }

        public String getIngredient() {
            return ingredient;
        }

        public int getAmount() {
            return amount;
        }

        public String getAction() {
            return action;
        }

        @Override
        public String toString() {
            return "Instruction{" +
                    "ingredient='" + ingredient + '\'' +
                    ", amount=" + amount +
                    ", action='" + action + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Instruction that = (Instruction) o;

            return amount == that.amount
                    && ingredient.equals(that.ingredient)
                    && action.equals(that.action);
        }

        @Override
        public int hashCode() {
            int result = ingredient.hashCode();
            result = 31 * result + amount;
            result = 31 * result + action.hashCode();
            return result;
        }
    }
}
