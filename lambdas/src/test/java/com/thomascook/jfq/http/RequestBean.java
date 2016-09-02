package com.thomascook.jfq.http;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestBean {

    @JsonProperty
    private String textField;
    @JsonProperty
    private int number;

    public RequestBean(String textField, int number) {
        this.textField = textField;
        this.number = number;
    }

    @Override
    public String toString() {
        return "RequestBean{" +
                "number=" + number +
                ", textField='" + textField + '\'' +
                '}';
    }
}
