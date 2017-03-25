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
public class Branch implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Integer code;

    private String name;

    private String address;

    private String phone;

    private String mobile;

    private String fax;

    private String email;

    private String website;

    private String commericalRegisteration;

    private String logo;

    @ManyToOne
    @JoinColumn(name = "Manager")
    @JsonIgnoreProperties(value = {"companies", "regions", "branches", "departments", "employees"}, allowSetters = true)
    private Person manager;

    @ManyToOne
    @JoinColumn(name = "Region")
    @JsonIgnoreProperties(value = {"branches"}, allowSetters = true)
    private Region region;

    @OneToMany(mappedBy = "branch")
    private List<Department> departments = new ArrayList<>();

    @JsonCreator
    public static Branch Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Branch branch = mapper.readValue(jsonString, Branch.class);
        return branch;
    }
}
