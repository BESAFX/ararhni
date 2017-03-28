package com.besafx.app.util;

import com.besafx.app.entity.TaskTo;

import java.util.List;
import java.util.stream.Collectors;

public class UtilsFX {

    public static String getPersonNamesFromTaskTos(List<TaskTo> taskTos) {
        return taskTos.stream().map(to -> to.getPerson().getName()).collect(Collectors.toList()).toString();
    }
}
