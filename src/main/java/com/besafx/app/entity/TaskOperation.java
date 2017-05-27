package com.besafx.app.entity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
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

    @GenericGenerator(
            name = "taskOperationSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "TASK_OPERATION_SEQUENCE"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "taskOperationSequenceGenerator")
    @JsonView(Views.Summery.class)
    private Long id;

    @JsonView(Views.Summery.class)
    private Integer code;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @JsonView(Views.Summery.class)
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonView(Views.Summery.class)
    private Date date;

    @Enumerated(EnumType.STRING)
    @Column(length = 32, columnDefinition = "varchar(32) default 'Comment'")
    @JsonView(Views.Summery.class)
    private OperationType type;

    @ManyToOne
    @JoinColumn(name = "task")
    @JsonIgnoreProperties(value = {"taskTos", "taskOperations", "taskWarns", "taskDeductions", "taskCloseRequests"}, allowSetters = true)
    private Task task;

    @ManyToOne
    @JoinColumn(name = "sender")
    @JsonIgnoreProperties(value = {"companies", "regions", "branches", "departments", "employees", "team"}, allowSetters = true)
    @JsonView(Views.Summery.class)
    private Person sender;

    @OneToMany(mappedBy = "taskOperation")
    @JsonIgnoreProperties(value = {"taskOperation"}, allowSetters = true)
    @JsonView(Views.Summery.class)
    private List<TaskOperationAttach> taskOperationAttaches = new ArrayList<>();

    @JsonCreator
    public static TaskOperation Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TaskOperation taskOperation = mapper.readValue(jsonString, TaskOperation.class);
        return taskOperation;
    }

    public enum OperationType {
        Comment,
        IncreaseEndDate,
        DecreaseEndDate,
        CloseTaskOnPerson,
        CloseTaskCompletely,
        CloseTaskAuto,
        AddPerson,
        RemovePerson,
        AcceptCloseRequest,
        AcceptIncreaseEndDateRequest,
        AcceptDecreaseEndDateRequest,
        DeclineCloseRequest,
        DeclineIncreaseEndDateRequest,
        DeclineDecreaseEndDateRequest,
        OpenTaskOnPersonAuto,
        OpenTaskOnPerson
    }
}
