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

    @JsonView(Views.Summery.class)
    private String address;

    @JsonView(Views.Summery.class)
    private String mobile;

    @JsonView(Views.Summery.class)
    private String nationality;

    @JsonView(Views.Summery.class)
    private String identityNumber;

    @JsonView(Views.Summery.class)
    private String photo;

    @JsonView(Views.Summery.class)
    private String qualification;

    @JsonView(Views.Summery.class)
    private String email;

    @JsonView(Views.Summery.class)
    private String password;

    @JsonView(Views.Summery.class)
    private Boolean enabled;

    @JsonView(Views.Summery.class)
    private Boolean tokenExpired;

    @JsonView(Views.Summery.class)
    private String optionThemeName;

    @JsonView(Views.Summery.class)
    private Boolean active;

    @JsonIgnore
    private String hiddenPassword;

    @JsonView(Views.Summery.class)
    private Date lastLoginDate;

    @JsonView(Views.Summery.class)
    private String lastLoginLocation;

    @JsonView(Views.Summery.class)
    private String ipAddress;

    @JsonView(Views.Summery.class)
    private String hostName;

    @ManyToOne
    @JoinColumn(name = "Team")
    @JsonIgnoreProperties(value = {"persons"}, allowSetters = true)
    @JsonView(Views.Summery.class)
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
    @JsonView(Views.Summery.class)
    private Date lastUpdate;

    @JsonCreator
    public static Person Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Person person = mapper.readValue(jsonString, Person.class);
        return person;
    }
}
