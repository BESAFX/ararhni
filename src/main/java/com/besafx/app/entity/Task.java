package com.besafx.app.entity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
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
    @JsonView(Views.Summery.class)
    private Long id;

    @JsonView(Views.Summery.class)
    private Integer code;

    @JsonView(Views.Summery.class)
    private String title;

    @JsonView(Views.Summery.class)
    private Integer warn;

    @JsonView(Views.Summery.class)
    private Double deduction;

    @JsonView(Views.Summery.class)
    private Double deductionOnAutoClose;

    @Enumerated(EnumType.STRING)
    @Column(length = 32, columnDefinition = "varchar(32) default 'Pending'")
    @JsonView(Views.Summery.class)
    private CloseType closeType;

    @Enumerated(EnumType.STRING)
    @Column(length = 32, columnDefinition = "varchar(32) default 'Regular'")
    @JsonView(Views.Summery.class)
    private Importance importance;

    @Enumerated(EnumType.STRING)
    @Column(length = 32, columnDefinition = "varchar(32) default 'Day'")
    @JsonView(Views.Summery.class)
    private CommentType commentType;

    @JsonView(Views.Summery.class)
    @Column(length = 5, columnDefinition = "int default 1")
    private Integer commentTypeCount;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @JsonView(Views.Summery.class)
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonView(Views.Summery.class)
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonView(Views.Summery.class)
    private Date endDate;

    @ManyToOne
    @JoinColumn(name = "person")
    @JsonIgnoreProperties(value = {"companies", "regions", "branches", "departments", "employees", "team"}, allowSetters = true)
    @JsonView(Views.Summery.class)
    private Person person;

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"task"}, allowSetters = true)
    @JsonView(Views.Summery.class)
    private List<TaskTo> taskTos = new ArrayList<>();

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"task"}, allowSetters = true)
    @JsonView(Views.Summery.class)
    private List<TaskCloseRequest> taskCloseRequests = new ArrayList<>();

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"task"}, allowSetters = true)
    @JsonView(Views.Summery.class)
    private List<TaskOperation> taskOperations = new ArrayList<>();

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"task"}, allowSetters = true)
    @JsonView(Views.Summery.class)
    private List<TaskWarn> taskWarns = new ArrayList<>();

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"task"}, allowSetters = true)
    @JsonView(Views.Summery.class)
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

    public enum CloseType {
        Pending, Auto, Manual
    }

    public enum CommentType {
        Day, Week, Month
    }
}
