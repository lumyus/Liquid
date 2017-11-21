package com.trinitytec;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NiceHashOrder {
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
}
