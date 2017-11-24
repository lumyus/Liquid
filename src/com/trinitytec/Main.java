package com.trinitytec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.github.silk8192.jpushbullet.PushbulletClient;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;


public class Main {


    static int MIN_PCT_PROFIT = 5;
    static double WITHDRAWN_NANOPOOL_BALANCE = 0;

    public static void main(String[] args) throws ExecutionException, InterruptedException, UnirestException, IOException {

        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        WebClient webClient = new WebClient();
        PushbulletClient client = new PushbulletClient("o.y5LdssNXxnwhZV6VF9kW1XBLqn4hadwD");

        System.out.println("\n*Welcome to Liquid*\n------------------");

        if(args.length > 0){
            MIN_PCT_PROFIT = Integer.parseInt(args[0]);
            WITHDRAWN_NANOPOOL_BALANCE = Double.parseDouble(args[1]);
        }
        else {
            Scanner reader = new Scanner(System.in);  // Reading from System.in
            System.out.println("What minimum profit would you like: ");
            MIN_PCT_PROFIT = reader.nextInt(); // Scans the next token of the input as an int.
//once finished
            System.out.println("What amount has been withdrawn from NanoPool: ");
            WITHDRAWN_NANOPOOL_BALANCE = reader.nextDouble(); // Scans the next token of the input as an int.
            reader.close();
        }


        initiateObjectMapper();

        MainHandler mainHandler = new MainHandler(webClient, client);

        while (true) {
            mainHandler.calculate();
            mainHandler.checkStats();
            Thread.sleep(5000); //Nicehash orders updated every 30s
        }


    }

    private static void initiateObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper() {
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
    }





            /*

 */


}
