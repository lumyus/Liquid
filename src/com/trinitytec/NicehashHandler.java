package com.trinitytec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class NicehashHandler implements Callback<JsonNode> {

    private static ObjectMapper objectMapper;
    private final MainHandler mainHandler;
    private double emptyPrice = 0;
    private String myOrderId;
    private String myNewPrice;
    private PrivateNiceHashOrder myOrder;

    public NicehashHandler(MainHandler mainHandler) {
        this.mainHandler = mainHandler;
        // Only one time

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

    public void setPrice(String myNewPrice) {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get("https://api.nicehash.com/api?method=orders.set.price&id=174786&key=b1bfbdc8-0687-44e3-8def-ad9a03c298b4&location=0&algo=20&order="+myOrderId+"&price="+myNewPrice).asJson();
            System.out.println(jsonResponse.getBody());
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    public void getPrices() {
        Future future = Unirest.get("https://api.nicehash.com/api?method=orders.get&my&id=174786&key=b1bfbdc8-0687-44e3-8def-ad9a03c298b4&location=0&algo=20")
                .asJsonAsync(this);
    }

    void lowestPrice(double myPrice) {
        Future<HttpResponse<JsonNode>> future = Unirest.get("https://api.nicehash.com/api?method=orders.get&location=0&algo=20")
                .asJsonAsync(new Callback<>() {
                    public void completed(HttpResponse<JsonNode> httpResponse) {
                        JSONObject myObj = httpResponse.getBody().getObject();

                        JSONObject results = myObj.getJSONObject("result");
                        JSONArray resultsArray = results.getJSONArray("orders");

                        List<NiceHashOrder> niceHashOrders = new ArrayList<>();

                        for (int i = 0; i < resultsArray.length(); i++) {
                            niceHashOrders.add(objectMapper.readValue(resultsArray.getJSONObject(i).toString(), NiceHashOrder.class));
                        }
                        double lowestPrice = orderWithLowestPrice(niceHashOrders);
                        System.out.println("Lowest Market Price: " + lowestPrice);
                        registerPrices(mainHandler, lowestPrice, myOrder);
                    }

                    public void failed(UnirestException e) {
                        System.out.println("The Nicehash request has failed");
                    }

                    public void cancelled() {
                        System.out.println("The request has been cancelled");
                    }
                });


    }


    @Override
    public void completed(HttpResponse<JsonNode> httpResponse) {
        JSONObject myObj = httpResponse.getBody().getObject();

        JSONObject results = myObj.getJSONObject("result");
        JSONArray resultsArray = results.getJSONArray("orders");
        double price = Double.parseDouble(objectMapper.readValue(resultsArray.getJSONObject(0).toString(), PrivateNiceHashOrder.class).price);
        myOrderId = objectMapper.readValue(resultsArray.getJSONObject(0).toString(), PrivateNiceHashOrder.class).id;
        myOrder = objectMapper.readValue(resultsArray.getJSONObject(0).toString(), PrivateNiceHashOrder.class);
        //System.out.println(price);
        lowestPrice(price);
    }
    @Override
    public void failed(UnirestException e) {
        System.out.println("The request has failed");
    }

    @Override
    public void cancelled() {
        System.out.println("The request has been cancelled");
    }


    public double orderWithLowestPrice(List<NiceHashOrder> list) {

        NiceHashOrder smallest = list.get(0);

        for (int i = 1; i < list.size(); i++) {

            if (Double.parseDouble(list.get(i).price) <= Double.parseDouble(smallest.price) && (Integer.parseInt(smallest.workers) != 0)) {
                smallest = list.get(i);
            } else if (Integer.parseInt(list.get(i - 1).workers) != 0) {
                smallest = list.get(i);
            }
        }

        return Double.parseDouble(smallest.price);
    }

    private void registerPrices(NicehashInterface callback, double lowestPrice, PrivateNiceHashOrder niceHashOrder) {
        callback.registerPrices(lowestPrice, niceHashOrder);
    }

    public void decrease() {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get("https://api.nicehash.com/api?method=orders.set.price.decrease&id=174786&key=b1bfbdc8-0687-44e3-8def-ad9a03c298b4&location=0&algo=20&order="+myOrderId).asJson();
            System.out.println(jsonResponse.getBody());
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }
}

