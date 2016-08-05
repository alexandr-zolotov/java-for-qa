package com.thomascook.jfq.cucumber.domains.cooking;

import org.apache.commons.lang3.Validate;

public class Amount {

    private final int left;

    public Amount(int left) {
        Validate.isTrue(left >= 0);
        this.left = left;
    }

    public boolean isAtLeast(int lowerBound) {
        return left >= lowerBound;
    }

    public Amount subtract(Amount amountToSubtract) {
        Validate.notNull(amountToSubtract);
        if(this.isAtLeast(amountToSubtract.left)){
            return new Amount(left - amountToSubtract.left);
        } else {
            throw new IllegalArgumentException("Not enough to subtract from");
        }
    }

    public Amount add(Amount amountToAdd){
        Validate.notNull(amountToAdd);
        return new Amount(left + amountToAdd.left);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        Amount amount = (Amount) other;
        return left == amount.left;
    }

    @Override
    public int hashCode() {
        return left;
    }
}

