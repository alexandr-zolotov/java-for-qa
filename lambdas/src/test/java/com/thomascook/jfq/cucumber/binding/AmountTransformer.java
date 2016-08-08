package com.thomascook.jfq.cucumber.binding;

import com.thomascook.jfq.cucumber.domains.cooking.Amount;
import cucumber.api.Transformer;

public class AmountTransformer extends Transformer<Amount>{

    @Override
    public Amount transform(String value) {
        return new Amount(Integer.parseInt(value));
    }
}
