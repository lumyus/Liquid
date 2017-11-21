package com.trinitytec;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.concurrent.Future;

public class NanoPoolHandler implements Callback<NanoPoolAccount>{

    private final static String NANOPOOL_WORKER_ID = "0x8813b025e4d016954bb3c1b366485b2529b30a48";
    private final MainHandler mainHandler;

    public NanoPoolHandler(MainHandler mainHandler) {
        this.mainHandler = mainHandler;
    }

    void getNanoPoolAccountBalance(){
        Future<HttpResponse<NanoPoolAccount>> future = Unirest.get("https://api.nanopool.org/v1/eth/balance/"+NANOPOOL_WORKER_ID)
                .asObjectAsync(NanoPoolAccount.class, this);
    }

    private void registerNanoPoolAccountBalance(NanoPoolInterface callback, double accountBalance) {
        callback.OnAccountBalanceReady(accountBalance);
    }

    @Override
    public void completed(HttpResponse<NanoPoolAccount> httpResponse) {
        NanoPoolAccount nanoPoolAccountObject = httpResponse.getBody();
        System.out.println(nanoPoolAccountObject.balance);
        registerNanoPoolAccountBalance(mainHandler, Double.parseDouble(nanoPoolAccountObject.balance));
    }

    @Override
    public void failed(UnirestException e) {

    }

    @Override
    public void cancelled() {

    }
}
