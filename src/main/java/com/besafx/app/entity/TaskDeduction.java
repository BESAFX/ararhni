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
import java.util.Date;

@Data
@Entity
public class TaskDeduction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JsonView(Views.Summery.class)
    private Long id;

    @JsonView(Views.Summery.class)
    private Integer code;

    @Enumerated(EnumType.STRING)
    @JsonView(Views.Summery.class)
    private TaskDeductionType type;

    @JsonView(Views.Summery.class)
    private Double deduction;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @JsonView(Views.Summery.class)
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonView(Views.Summery.class)
    private Date date;

    @ManyToOne
    @JoinColumn(name = "task")
    @JsonIgnoreProperties(value = {"taskTos", "taskOperations", "taskWarns", "taskDeductions", "taskCloseRequests"}, allowSetters = true)
    private Task task;

    @ManyToOne
    @JoinColumn(name = "toPerson")
    @JsonIgnoreProperties(value = {"companies", "regions", "branches", "departments", "employees", "team"}, allowSetters = true)
    @JsonView(Views.Summery.class)
    private Person toPerson;

    @JsonCreator
    public static TaskDeduction Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TaskDeduction taskDeduction = mapper.readValue(jsonString, TaskDeduction.class);
        return taskDeduction;
    }

    public enum TaskDeductionType {
        Auto, Manual
    }
}
