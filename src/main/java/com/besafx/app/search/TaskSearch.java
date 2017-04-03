package com.besafx.app.search;

import com.besafx.app.entity.Task;
import com.besafx.app.entity.TaskTo;
import com.besafx.app.service.TaskService;
import com.besafx.app.service.TaskToService;
import com.besafx.app.util.DateConverter;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TaskSearch {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskToService taskToService;

    public List<Task> search(
            final String title,
            final Long codeFrom,
            final Long codeTo,
            final Long startDateFrom,
            final Long startDateTo,
            final Long endDateFrom,
            final Long endDateTo,
            final Boolean taskType,
            final Boolean isTaskOpen,
            final String timeType,
            final Long person
    ) {

        if (!taskType) {
            //Search in Task Table (Outgoing Tasks)
            List<Specification> predicates = new ArrayList<>();
            Optional.ofNullable(title).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("title"), "%" + value + "%")));
            Optional.ofNullable(codeFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("code"), value)));
            Optional.ofNullable(codeTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("code"), value)));
            Optional.ofNullable(startDateFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("startDate"), new Date(value))));
            Optional.ofNullable(startDateTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("startDate"), new Date(value))));
            Optional.ofNullable(endDateFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("endDate"), new Date(value))));
            Optional.ofNullable(endDateTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("endDate"), new Date(value))));
            Optional.ofNullable(person).ifPresent(value -> predicates.add((root, cq, cb) -> cb.equal(root.get("person").get("id"), value)));
            Optional.ofNullable(isTaskOpen).ifPresent(value -> {
                if (value) {
                    LocalDate today = new DateTime().withTimeAtStartOfDay().toLocalDate();
                    LocalDate tomorrow = new DateTime().plusDays(1).withTimeAtStartOfDay().toLocalDate();
                    switch (timeType) {
                        case "Day":
                            predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("endDate"), today.toDate()));
                            predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("endDate"), tomorrow.toDate()));
                            break;
                        case "Week":
                            Date weekStart = DateConverter.getCurrentWeekStart();
                            Date weekEnd = DateConverter.getCurrentWeekEnd();
                            predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("endDate"), weekStart));
                            predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("endDate"), weekEnd));
                            break;
                        case "Month":
                            LocalDate monthStart = today.withDayOfMonth(1);
                            LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
                            predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("endDate"), monthStart.toDate()));
                            predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("endDate"), monthEnd.toDate()));
                            break;
                        case "Year":
                            LocalDate yearStart = today.withDayOfYear(1);
                            LocalDate yearEnd = yearStart.plusYears(1).minusDays(1);
                            predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("endDate"), yearStart.toDate()));
                            predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("endDate"), yearEnd.toDate()));
                            break;
                        case "All":
                            predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("endDate"), new Date()));
                            break;
                    }
                } else {
                    predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("endDate"), new Date()));
                }
            });

            if (!predicates.isEmpty()) {
                Specification result = predicates.get(0);
                for (int i = 1; i < predicates.size(); i++) {
                    result = Specifications.where(result).and(predicates.get(i));
                }
                return taskService.findAll(result);
            } else {
                return null;
            }

        } else {
            //Search in TaskTo Table (Incoming Tasks)
            List<Specification> predicates = new ArrayList<>();
            Optional.ofNullable(title).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("task").get("title"), "%" + value + "%")));
            Optional.ofNullable(codeFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("task").get("code"), value)));
            Optional.ofNullable(codeTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("task").get("code"), value)));
            Optional.ofNullable(startDateFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("task").get("startDate"), new Date(value))));
            Optional.ofNullable(startDateTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("task").get("startDate"), new Date(value))));
            Optional.ofNullable(endDateFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("task").get("endDate"), new Date(value))));
            Optional.ofNullable(endDateTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("task").get("endDate"), new Date(value))));
            Optional.ofNullable(person).ifPresent(value -> predicates.add((root, cq, cb) -> cb.equal(root.get("person").get("id"), value)));
            Optional.ofNullable(isTaskOpen).ifPresent(value -> {
                if (value) {
                    LocalDate today = new DateTime().withTimeAtStartOfDay().toLocalDate();
                    LocalDate tomorrow = new DateTime().plusDays(1).withTimeAtStartOfDay().toLocalDate();
                    switch (timeType) {
                        case "Day":
                            predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("task").get("endDate"), today.toDate()));
                            predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("task").get("endDate"), tomorrow.toDate()));
                            break;
                        case "Week":
                            Date weekStart = DateConverter.getCurrentWeekStart();
                            Date weekEnd = DateConverter.getCurrentWeekEnd();
                            predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("task").get("endDate"), weekStart));
                            predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("task").get("endDate"), weekEnd));
                            break;
                        case "Month":
                            LocalDate monthStart = today.withDayOfMonth(1);
                            LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
                            predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("task").get("endDate"), monthStart.toDate()));
                            predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("task").get("endDate"), monthEnd.toDate()));
                            break;
                        case "Year":
                            LocalDate yearStart = today.withDayOfYear(1);
                            LocalDate yearEnd = yearStart.plusYears(1).minusDays(1);
                            predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("task").get("endDate"), yearStart.toDate()));
                            predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("task").get("endDate"), yearEnd.toDate()));
                            break;
                        case "All":
                            predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("task").get("endDate"), new Date()));
                            break;
                    }
                } else {
                    predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("task").get("endDate"), new Date()));
                }
            });

            if (!predicates.isEmpty()) {
                Specification result = predicates.get(0);
                for (int i = 1; i < predicates.size(); i++) {
                    result = Specifications.where(result).and(predicates.get(i));
                }
                return (List<Task>) taskToService.findAll(result).stream().map(taskTo -> ((TaskTo) taskTo).getTask()).collect(Collectors.toList());
            } else {
                return null;
            }
        }

    }

    public List<Task> getIncomingTasks(String timeType, Long personId) {
        return search(null, null, null, null, null, null, null, true, true, timeType, personId);
    }

    public List<Task> getOutgoingTasks(String timeType, Long personId) {
        return search(null, null, null, null, null, null, null, false, true, timeType, personId);
    }
}
