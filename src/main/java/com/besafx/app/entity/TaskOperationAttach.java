package com.besafx.app.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;

@Data
@Entity
public class TaskOperationAttach implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String name;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String link;

    @ManyToOne
    @JoinColumn(name = "task_operation")
    @JsonIgnoreProperties(value = "taskOperationAttaches", allowSetters = true)
    private TaskOperation taskOperation;

    @JsonCreator
    public static TaskOperationAttach Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TaskOperationAttach attachment = mapper.readValue(jsonString, TaskOperationAttach.class);
        return attachment;
    }
}
