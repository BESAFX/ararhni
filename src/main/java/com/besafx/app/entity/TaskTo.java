package com.besafx.app.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    private Long id;

    private Integer progress;

    private Boolean closed;

    @Temporal(TemporalType.TIMESTAMP)
    private Date closeDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "task")
    @JsonIgnoreProperties(value = {"taskTos", "taskOperations", "taskCloseRequests"}, allowSetters = true)
    private Task task;

    @ManyToOne
    @JoinColumn(name = "person")
    @JsonIgnoreProperties(value = {"companies", "regions", "branches", "departments", "employees"}, allowSetters = true)
    private Person person;

    @JsonCreator
    public static TaskTo Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TaskTo taskTo = mapper.readValue(jsonString, TaskTo.class);
        return taskTo;
    }
}
