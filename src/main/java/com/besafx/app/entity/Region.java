package com.besafx.app.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Region implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JsonView(Views.Summery.class)
    private Long id;

    @JsonView(Views.Summery.class)
    private Integer code;

    @JsonView(Views.Summery.class)
    private String name;

    @JsonView(Views.Summery.class)
    private String address;

    @ManyToOne
    @JoinColumn(name = "Manager")
    @JsonIgnoreProperties(value = {"companies", "regions", "branches", "departments", "employees"}, allowSetters = true)
    @JsonView(Views.Summery.class)
    private Person manager;

    @ManyToOne
    @JoinColumn(name = "Company")
    @JsonIgnoreProperties(value = {"regions"}, allowSetters = true)
    @JsonView(Views.Summery.class)
    private Company company;

    @OneToMany(mappedBy = "region")
    private List<Branch> branches = new ArrayList<>();

    @JsonCreator
    public static Region Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Region branch = mapper.readValue(jsonString, Region.class);
        return branch;
    }
}
