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


    private final static String NANOPOOL_WORKER_ID = "0x8813b025e4d016954bb3c1b366485b2529b30a48";
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
            Thread.sleep(5000);
        }


    }





            /*Future<HttpResponse<NanoPoolAccount>> future = Unirest.get("https://api.nanopool.org/v1/eth/balance/"+NANOPOOL_WORKER_ID)
                .asObjectAsync(NanoPoolAccount.class, new Callback<>() {
                    public void failed(UnirestException e) {
                        System.out.println("The request has failed");
                    }

                    public void completed(HttpResponse<NanoPoolAccount> response) {
                        NanoPoolAccount nanoPoolAccountObject = response.getBody();
                        System.out.println(nanoPoolAccountObject.balance);

                    }

                    public void cancelled() {
                        System.out.println("The request has been cancelled");
                    }
                });
List<NiceHashOrder>
 */


}
