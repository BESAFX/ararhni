package com.besafx.app.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;

@Data
@Entity
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Boolean createEntity;

    private Boolean updateEntity;

    private Boolean deleteEntity;

    private Boolean reportEntity;

    @ManyToOne
    @JoinColumn(name = "Screen")
    private Screen screen;

    @JsonCreator
    public static Permission Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Permission permission = mapper.readValue(jsonString, Permission.class);
        return permission;
    }
}
