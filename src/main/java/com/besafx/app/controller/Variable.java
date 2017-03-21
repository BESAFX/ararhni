package com.besafx.app.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Variable {
    private String name;
    private String expression;
    private String operation;

    @JsonCreator
    public static Variable Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Variable variable = mapper.readValue(jsonString, Variable.class);
        return variable;
    }
}
