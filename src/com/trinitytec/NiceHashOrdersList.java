package com.trinitytec;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class NiceHashOrdersList {
    @JsonProperty("orders")
    public List<NiceHashOrder> orders = null;
}
