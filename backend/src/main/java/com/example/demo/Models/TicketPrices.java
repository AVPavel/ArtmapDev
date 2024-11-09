package com.example.demo.Models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TicketPrices {
    private Map<String, Double> prices = new HashMap<>();

    public static TicketPrices fromJson(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Double> pricesMap = mapper.readValue(json, new TypeReference<>() {});
        TicketPrices ticketPrices = new TicketPrices();
        ticketPrices.setPrices(pricesMap);
        return ticketPrices;
    }

    public String toJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

    @JsonAnySetter
    public void addPrice(String type, Double price) {
        prices.put(type, price);
    }
}
