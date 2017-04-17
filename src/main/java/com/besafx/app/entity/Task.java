package com.besafx.app.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Integer code;

    private String title;

    private Integer warn;

    private Double deduction;

    private Double deductionOnAutoClose;

    @Enumerated(EnumType.STRING)
    @Column(length = 32, columnDefinition = "varchar(32) default 'Regular'")
    private Importance importance;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @ManyToOne
    @JoinColumn(name = "person")
    @JsonIgnoreProperties(value = {"companies", "regions", "branches", "departments", "employees", "team"}, allowSetters = true)
    private Person person;

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"task"}, allowSetters = true)
    private List<TaskTo> taskTos = new ArrayList<>();

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"task"}, allowSetters = true)
    private List<TaskCloseRequest> taskCloseRequests = new ArrayList<>();

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"task"}, allowSetters = true)
    private List<TaskOperation> taskOperations = new ArrayList<>();

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"task"}, allowSetters = true)
    private List<TaskWarn> taskWarns = new ArrayList<>();

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"task"}, allowSetters = true)
    private List<TaskDeduction> taskDeductions = new ArrayList<>();

    @JsonCreator
    public static Task Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Task task = mapper.readValue(jsonString, Task.class);
        return task;
    }

    public enum Importance {
        Regular, Important, Critical
    }
}
