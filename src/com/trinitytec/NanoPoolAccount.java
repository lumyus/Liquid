package com.trinitytec;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NanoPoolAccount {
    @JsonProperty("data")
    String balance;
    @JsonProperty("status")
    String status;
}
