package com.trinitytec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.util.concurrent.Future;

public class GDax implements Callback<GDaxData> {

    private static ObjectMapper objectMapper;
    private final MainHandler mainHandler;
    private double emptyPrice = 0;

    public GDax(MainHandler mainHandler) {
        // Only one time
        this.mainHandler = mainHandler;

        objectMapper = new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
                    = new com.fasterxml.jackson.databind.ObjectMapper();

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public void getETHBTCRatio() {
        Future future = Unirest.get("https://api.gdax.com/products/ETH-BTC/ticker")
                .asObjectAsync(GDaxData.class, this);
    }

    @Override
    public void completed(HttpResponse<GDaxData> response) {
        GDaxData GDaxDataObject = response.getBody();
       //System.out.println("MaxPrice: " + maxPrice(Double.parseDouble(GDaxDataObject.price), ETH_ESTIM));
        register(mainHandler, GDaxDataObject.price);
    }

    @Override
    public void failed(UnirestException e) {
        System.out.println("The GDAX request has failed");
    }

    @Override
    public void cancelled() {
        System.out.println("The request has been cancelled");
    }


    private void register(GDaxInterface callback, String ratio) {
        callback.onETHBTCRatioReady(Double.parseDouble(ratio));
    }
}
