package com.besafx.app.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;

@Data
@Entity
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Team")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "Permission")
    private Permission permission;

    @JsonCreator
    public static Role Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Role role = mapper.readValue(jsonString, Role.class);
        return role;
    }
}
