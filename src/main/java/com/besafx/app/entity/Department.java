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
public class Department implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Integer code;

    private String name;

    @ManyToOne
    @JoinColumn(name = "Manager")
    @JsonIgnoreProperties(value = {"companies", "branches", "departments", "employees"}, allowSetters = true)
    private Person manager;

    @ManyToOne
    @JoinColumn(name = "Branch")
    @JsonIgnoreProperties(value = {"departments"}, allowSetters = true)
    private Branch branch;

    @OneToMany(mappedBy = "department")
    private List<Employee> employees = new ArrayList<>();

    @JsonCreator
    public static Department Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Department department = mapper.readValue(jsonString, Department.class);
        return department;
    }
}
