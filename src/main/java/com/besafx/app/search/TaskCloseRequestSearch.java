package com.besafx.app.search;

import com.besafx.app.entity.TaskCloseRequest;
import com.besafx.app.service.TaskCloseRequestService;
import com.besafx.app.util.DateConverter;
import com.google.common.collect.Lists;
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

@Component
public class TaskCloseRequestSearch {

    @Autowired
    private TaskCloseRequestService taskCloseRequestService;

    public List<TaskCloseRequest> search(
            final String timeType,
            final Long dateFrom,
            final Long dateTo,
            final Long taskId,
            final Long taskPersonId,
            final Long personId
    ) {
        List<Specification> predicates = new ArrayList<>();
        if (timeType == null) {
            Optional.ofNullable(dateFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("date"), new Date(value))));
            Optional.ofNullable(dateTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("date"), new Date(value))));
        } else {
            LocalDate today = new DateTime().withTimeAtStartOfDay().toLocalDate();
            LocalDate tomorrow = new DateTime().plusDays(1).withTimeAtStartOfDay().toLocalDate();
            switch (timeType) {
                case "Day":
                    predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("date"), today.toDate()));
                    predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("date"), tomorrow.toDate()));
                    break;
                case "Week":
                    Date weekStart = DateConverter.getCurrentWeekStart();
                    Date weekEnd = DateConverter.getCurrentWeekEnd();
                    predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("date"), weekStart));
                    predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("date"), weekEnd));
                    break;
                case "Month":
                    LocalDate monthStart = today.withDayOfMonth(1);
                    LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
                    predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("date"), monthStart.toDate()));
                    predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("date"), monthEnd.toDate()));
                    break;
                case "Year":
                    LocalDate yearStart = today.withDayOfYear(1);
                    LocalDate yearEnd = yearStart.plusYears(1).minusDays(1);
                    predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("date"), yearStart.toDate()));
                    predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("date"), yearEnd.toDate()));
                    break;
                case "All":
                    break;
            }
        }
        Optional.ofNullable(taskId).ifPresent(value -> predicates.add((root, cq, cb) -> cb.equal(root.get("task").get("id"), value)));
        Optional.ofNullable(taskPersonId).ifPresent(value -> predicates.add((root, cq, cb) -> cb.equal(root.get("task").get("person").get("id"), value)));
        Optional.ofNullable(personId).ifPresent(value -> predicates.add((root, cq, cb) -> cb.equal(root.get("person").get("id"), value)));


        if (!predicates.isEmpty()) {
            Specification result = predicates.get(0);
            for (int i = 1; i < predicates.size(); i++) {
                result = Specifications.where(result).and(predicates.get(i));
            }
            return Lists.newArrayList(taskCloseRequestService.findAll(result));
        } else {
            return Lists.newArrayList(taskCloseRequestService.findAll());
        }
    }
}
