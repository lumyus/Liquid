package com.trinitytec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.github.silk8192.jpushbullet.PushbulletClient;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;


public class Main {



    static double price;
    private static ObjectMapper objectMapper;

    public static void main(String[] args) throws ExecutionException, InterruptedException, UnirestException, IOException {

        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        WebClient webClient = new WebClient();
        PushbulletClient client = new PushbulletClient("o.y5LdssNXxnwhZV6VF9kW1XBLqn4hadwD");
        double lowestPrice = 0.0069;
        double myPrice = 0.0069;
        double maxPriceValue = 0.0071;

        //client.sendNotePush("EMERGENCY", "Cédric, something is wrong!");


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

        Unirest.setObjectMapper(objectMapper);

        MainHandler mainHandler = new MainHandler(webClient, client);

        while (true) {
            mainHandler.calculate();
            mainHandler.checkStats();
            Thread.sleep(15000); //Nicehash orders updated every 30s
        }


    }





            /*

 */


}
