package com.besafx.app.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Team implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "team")
    @JsonIgnoreProperties(value = {"companies", "regions", "branches", "departments", "employees"}, allowSetters = true)
    private List<Person> persons = new ArrayList<>();

    @JsonCreator
    public static Team Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Team team = mapper.readValue(jsonString, Team.class);
        return team;
    }
}
