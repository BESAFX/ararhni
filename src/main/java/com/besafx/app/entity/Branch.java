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
public class Branch implements Serializable {

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

    @JsonView(Views.Summery.class)
    private String phone;

    @JsonView(Views.Summery.class)
    private String mobile;

    @JsonView(Views.Summery.class)
    private String fax;

    @JsonView(Views.Summery.class)
    private String email;

    @JsonView(Views.Summery.class)
    private String website;

    @JsonView(Views.Summery.class)
    private String commericalRegisteration;

    @JsonView(Views.Summery.class)
    private String logo;

    @ManyToOne
    @JoinColumn(name = "Manager")
    @JsonIgnoreProperties(value = {"companies", "regions", "branches", "departments", "employees"}, allowSetters = true)
    @JsonView(Views.Summery.class)
    private Person manager;

    @ManyToOne
    @JoinColumn(name = "Region")
    @JsonIgnoreProperties(value = {"branches"}, allowSetters = true)
    @JsonView(Views.Summery.class)
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
