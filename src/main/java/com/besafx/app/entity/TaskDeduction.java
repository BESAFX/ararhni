package com.besafx.app.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    private Long id;

    private Integer code;

    @Enumerated(EnumType.STRING)
    private TaskDeductionType type;

    private Double deduction;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @ManyToOne
    @JoinColumn(name = "task")
    @JsonIgnoreProperties(value = {"taskTos", "taskOperations", "taskWarns", "taskDeductions", "taskCloseRequests"}, allowSetters = true)
    private Task task;

    @ManyToOne
    @JoinColumn(name = "toPerson")
    @JsonIgnoreProperties(value = {"companies", "regions", "branches", "departments", "employees", "team"}, allowSetters = true)
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
