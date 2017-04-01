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
public class TaskOperation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Integer code;

    private Integer type;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @ManyToOne
    @JoinColumn(name = "task")
    @JsonIgnoreProperties(value = {"taskTos", "taskOperations", "taskCloseRequests"}, allowSetters = true)
    private Task task;

    @ManyToOne
    @JoinColumn(name = "sender")
    @JsonIgnoreProperties(value = {"companies", "regions", "branches", "departments", "employees"}, allowSetters = true)
    private Person sender;

    @OneToMany(mappedBy = "taskOperation")
    @JsonIgnoreProperties(value = {"taskOperation"}, allowSetters = true)
    private List<TaskOperationAttach> taskOperationAttaches = new ArrayList<>();

    @JsonCreator
    public static TaskOperation Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TaskOperation taskOperation = mapper.readValue(jsonString, TaskOperation.class);
        return taskOperation;
    }
}
