package com.trinitytec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.sourceforge.htmlunit.cyberneko.HTMLElements;

import java.awt.print.Book;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class Main {


    private final static String NANOPOOL_WORKER_ID = "0x8813b025e4d016954bb3c1b366485b2529b30a48";

    public static void main(String[] args) throws ExecutionException, InterruptedException, UnirestException, IOException {

        WebClient webClient = new WebClient();
        final double[] ETH_BTC_RATIO = new double[1];
         double ETH_ESTIM;
        double HASH_PRICE = 0.0067;


        // Only one time
        Unirest.setObjectMapper(new ObjectMapper() {
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
        });



        ETH_ESTIM = estimateEtherEarned(webClient);

         System.out.println(ETH_ESTIM);


        Future<HttpResponse<GDaxData>> future2 = Unirest.get("https://api.gdax.com/products/ETH-BTC/ticker")
                .asObjectAsync(GDaxData.class, new Callback<>() {
                    public void failed(UnirestException e) {
                        System.out.println("The request has failed");
                    }

                    public void completed(HttpResponse<GDaxData> response) {
                        GDaxData GDaxDataObject = response.getBody();
                        System.out.println(GDaxDataObject.price);
                        System.out.println("Estimated Profit: "+profitCalculated(Double.parseDouble(GDaxDataObject.price),ETH_ESTIM,HASH_PRICE)+"%");

                    }

                    public void cancelled() {
                        System.out.println("The request has been cancelled");
                    }
                });

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

 */

    }

    private static double estimateEtherEarned(WebClient webClient) throws IOException {
        HtmlPage page = webClient.getPage("https://etherscan.io/ether-mining-calculator");
        HtmlInput searchBox = page.getElementByName("ctl00$ContentPlaceHolder1$txtYourHashRate");
        searchBox.setValueAttribute("1000");

        HtmlSubmitInput googleSearchSubmitButton =
                page.getElementByName("ctl00$ContentPlaceHolder1$btnSubmit"); // sometimes it's "btnK"
        page = googleSearchSubmitButton.click();
        String rawEtherEarned = page.getElementsByTagName("td").get(5).getFirstChild().toString();

        return Double.parseDouble(rawEtherEarned.substring(0,rawEtherEarned.indexOf(" ")));
    }

    private static double profitCalculated(double ETH_BTC_RATIO, double ETH_ESTIM, double HASH_PRICE ){
        return 100*((ETH_BTC_RATIO*ETH_ESTIM/HASH_PRICE)-1.06);
    }

}
