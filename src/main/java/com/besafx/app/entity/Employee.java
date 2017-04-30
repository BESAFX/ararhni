package com.besafx.app.entity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;

@Data
@Entity
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JsonView(Views.Summery.class)
    private Long id;

    @JsonView(Views.Summery.class)
    private Integer code;

    @ManyToOne
    @JoinColumn(name = "Person")
    @JsonIgnoreProperties(value = {"companies", "regions", "branches", "departments", "employees"}, allowSetters = true)
    @JsonView(Views.Summery.class)
    private Person person;

    @ManyToOne
    @JoinColumn(name = "Department")
    @JsonIgnoreProperties(value = {"employees"}, allowSetters = true)
    @JsonView(Views.Summery.class)
    private Department department;

    @JsonCreator
    public static Employee Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Employee employee = mapper.readValue(jsonString, Employee.class);
        return employee;
    }
}
