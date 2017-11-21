package com.trinitytec;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.github.silk8192.jpushbullet.PushbulletClient;
import com.github.silk8192.jpushbullet.items.push.Push;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import java.io.Console;
import java.io.IOException;

public class MainHandler implements GDaxInterface, NicehashInterface {

    private static final int MAX_EMERGENCY = 5;
    private static final int MAX_INFORMATION = 720;
    private final WebClient webClient;
    private final PushbulletClient pushbulletClient;
    private double maxPriceValue;
    private NicehashHandler nicehashHandler;
    private int emergencyCounter = 0;
    private int informationCounter =0;

    public MainHandler(WebClient webClient, PushbulletClient pushbulletClient) {
        this.pushbulletClient = pushbulletClient;
        this.webClient = webClient;
    }

    public double estimateEtherEarned() throws IOException {
            HtmlPage page = webClient.getPage("https://etherscan.io/ether-mining-calculator");
            HtmlInput searchBox = page.getElementByName("ctl00$ContentPlaceHolder1$txtYourHashRate");
            searchBox.setValueAttribute("1000");

            HtmlSubmitInput googleSearchSubmitButton =
                    page.getElementByName("ctl00$ContentPlaceHolder1$btnSubmit"); // sometimes it's "btnK"
            page = googleSearchSubmitButton.click();
            String rawEtherEarned = page.getElementsByTagName("td").get(5).getFirstChild().toString();
            return Double.parseDouble(rawEtherEarned.substring(0, rawEtherEarned.indexOf(" ")));
    }

    @Override
    public void onETHBTCRatioReady(double ratio) {
        try {
            maxPriceValue = maxPrice(estimateEtherEarned(), ratio);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("MaxPrice: "+ maxPriceValue);
        getOrderData(maxPriceValue);
    }

    private void getOrderData(double maxPriceValue) {
        nicehashHandler = new NicehashHandler(this);
        nicehashHandler.getPrices();
    }

    public double maxPrice(double ETH_ESTIM, double ETH_BTC_RATIO) {
        return round((100 * ETH_BTC_RATIO * ETH_ESTIM) / (100 * 1.06 + 5), 4);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }




    public void calculate() {
        new GDax(this).getETHBTCRatio();
    }

    @Override
    public void registerPrices(double lowestPrice, double myPrice) {
        handleNotifications(lowestPrice, myPrice);
        adaptOrder(lowestPrice,myPrice);
    }

    private void handleNotifications(double lowestPrice, double myPrice){
        if(myPrice > maxPriceValue && emergencyCounter < MAX_EMERGENCY){
            sendNotification("EMERGENCY", "Cédric, something is wrong!");
            emergencyCounter++;
        }

        if(informationCounter >= MAX_INFORMATION){
            sendNotification("Prices", "Lowest Market Price: "+lowestPrice+"\n"+"My Order Price: "+myPrice+"\n"+"Maximum Profitable Price: "+maxPriceValue);
            informationCounter = 0;
        }else informationCounter++;
    }

    private void adaptOrder(double lowestPrice, double myPrice) {

        System.out.println("Actual price: "+myPrice);
        System.out.println("Max price: "+maxPriceValue);

        if(myPrice > lowestPrice || myPrice > maxPriceValue){
            System.out.println("Decrease price");
            nicehashHandler.decrease();
        }else if(lowestPrice>maxPriceValue){
            System.out.println("Decrease price");
            nicehashHandler.decrease();
        }else{
            System.out.println("Set price to "+lowestPrice);
            nicehashHandler.setPrice(String.valueOf(lowestPrice));
        }

    }

    void sendNotification(String title, String body){
        pushbulletClient.sendNotePush(title,body);
    }

}
