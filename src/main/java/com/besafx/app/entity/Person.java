package com.besafx.app.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class Person implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JsonView(Views.Summery.class)
    private Long id;

    @JsonView(Views.Summery.class)
    private String name;

    @JsonView(Views.Summery.class)
    private String nickname;

    private String address;

    private String mobile;

    private String nationality;

    private String identityNumber;

    private String photo;

    private String qualification;

    @JsonView(Views.Summery.class)
    private String email;

    private String password;

    private Boolean enabled;

    private Boolean tokenExpired;

    private String optionThemeName;

    private Boolean active;

    @JsonIgnore
    private String hiddenPassword;

    private Date lastLoginDate;

    private String lastLoginLocation;

    @ManyToOne
    @JoinColumn(name = "Team")
    @JsonIgnoreProperties(value = {"persons"}, allowSetters = true)
    private Team team;

    @OneToMany(mappedBy = "manager", fetch = FetchType.LAZY)
    private List<Company> companies = new ArrayList<>();

    @OneToMany(mappedBy = "manager", fetch = FetchType.LAZY)
    private List<Region> regions = new ArrayList<>();

    @OneToMany(mappedBy = "manager", fetch = FetchType.LAZY)
    private List<Branch> branches = new ArrayList<>();

    @OneToMany(mappedBy = "manager", fetch = FetchType.LAZY)
    private List<Department> departments = new ArrayList<>();

    @OneToMany(mappedBy = "person", fetch = FetchType.LAZY)
    private List<Employee> employees = new ArrayList<>();

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;

    @JsonCreator
    public static Person Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Person person = mapper.readValue(jsonString, Person.class);
        return person;
    }
}
