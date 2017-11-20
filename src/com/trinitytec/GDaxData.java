package com.trinitytec;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GDaxData {
    @JsonProperty("trade_id")
    String trade_id;
    @JsonProperty("price")
    String price;
    @JsonProperty("size")
    String size;
    @JsonProperty("bid")
    String bid;
    @JsonProperty("ask")
    String ask;
    @JsonProperty("volume")
    String volume;
    @JsonProperty("time")
    String time;
}
