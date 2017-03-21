package com.besafx.app.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Column {
    private String name;
    private String expression;
    private String value;
    private String nestedValue;
    private String valueClassName;
    private String dataTextAlign;
    private boolean view;
    private boolean groupBy;
    private boolean sortBy;
}
