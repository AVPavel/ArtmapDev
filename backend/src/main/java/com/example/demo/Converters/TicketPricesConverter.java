package com.example.demo.Converters;

import com.example.demo.Models.TicketPrices;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TicketPricesConverter implements AttributeConverter<TicketPrices, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(TicketPrices ticketPrices) {
        try {
            return objectMapper.writeValueAsString(ticketPrices);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting TicketPrices to JSON", e);
        }
    }

    @Override
    public TicketPrices convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, TicketPrices.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting JSON to TicketPrices", e);
        }
    }
}
