package com.besafx.app.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
public class TaskTo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JsonView(Views.Summery.class)
    private Long id;

    @JsonView(Views.Summery.class)
    private Integer progress;

    @JsonView(Views.Summery.class)
    private Boolean closed;

    @Enumerated(EnumType.STRING)
    @JsonView(Views.Summery.class)
    private PersonDegree degree;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonView(Views.Summery.class)
    private Date closeDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "task")
    @JsonIgnoreProperties(value = {"taskTos", "taskOperations", "taskWarns", "taskDeductions", "taskCloseRequests"}, allowSetters = true)
    private Task task;

    @ManyToOne
    @JoinColumn(name = "person")
    @JsonView(Views.Summery.class)
    @JsonIgnoreProperties(value = {"companies", "regions", "branches", "departments", "employees", "team"}, allowSetters = true)
    private Person person;

    @JsonCreator
    public static TaskTo Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TaskTo taskTo = mapper.readValue(jsonString, TaskTo.class);
        return taskTo;
    }

    public enum PersonDegree {
        A, B, C, D, F
    }
}
