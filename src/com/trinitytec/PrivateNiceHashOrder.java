package com.trinitytec;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PrivateNiceHashOrder {
    @JsonProperty("type")
    String type;
    @JsonProperty("id")
    String id;
    @JsonProperty("price")
    String price;
    @JsonProperty("algo")
    String algo;
    @JsonProperty("alive")
    String alive;
    @JsonProperty("limit_speed")
    String limit_speed;
    @JsonProperty("workers")
    String workers;
    @JsonProperty("accepted_speed")
    String accepted_speed;

    @JsonProperty("btc_avail")
    String btc_avail;
    @JsonProperty("pool_user")
    String pool_user;
    @JsonProperty("pool_port")
    String pool_port;
    @JsonProperty("pool_pass")
    String pool_pass;
    @JsonProperty("btc_paid")
    String btc_paid;
    @JsonProperty("pool_host")
    String pool_host;
    @JsonProperty("end")
    String end;
}
