package com.besafx.app.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportProp {

    private String title;
    private String orientation;
    private String exportType;
    private List<Column> columns = new ArrayList<>();
    private List<Variable> groupVariables = new ArrayList<>();
    private List<Variable> tableVariables = new ArrayList<>();
}
