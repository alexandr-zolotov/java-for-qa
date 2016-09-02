package com.thomascook.jfq.http;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseBean {

    String textField;
    int number;

    @JsonCreator
    public ResponseBean(@JsonProperty("textField") String textField, @JsonProperty("number") int number) {
        this.textField = textField;
        this.number = number;
    }

    @Override
    public String toString() {
        return "ResponseBean{" +
                "number=" + number +
                ", textField='" + textField + '\'' +
                '}';
    }
}
