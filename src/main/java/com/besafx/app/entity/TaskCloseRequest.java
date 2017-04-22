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
public class TaskCloseRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JsonView(Views.Summery.class)
    private Long id;

    @JsonView(Views.Summery.class)
    private Integer code;

    //True  --- Close Request
    //False --- Extension Request
    @JsonView(Views.Summery.class)
    private Boolean type;

    @JsonView(Views.Summery.class)
    private Boolean approved;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonView(Views.Summery.class)
    private Date approvedDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "task")
    @JsonIgnoreProperties(value = {"taskTos", "taskOperations", "taskWarns", "taskDeductions", "taskCloseRequests"}, allowSetters = true)
    private Task task;

    @ManyToOne
    @JoinColumn(name = "person")
    @JsonIgnoreProperties(value = {"companies", "regions", "branches", "departments", "employees", "team"}, allowSetters = true)
    @JsonView(Views.Summery.class)
    private Person person;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonView(Views.Summery.class)
    private Date date;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @JsonView(Views.Summery.class)
    private String note;

    @JsonCreator
    public static TaskCloseRequest Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TaskCloseRequest taskTo = mapper.readValue(jsonString, TaskCloseRequest.class);
        return taskTo;
    }
}
