package com.besafx.app.service;
import com.besafx.app.entity.Person;
import com.besafx.app.entity.Task;
import com.besafx.app.entity.TaskDeduction;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public interface TaskDeductionService extends PagingAndSortingRepository<TaskDeduction, Long>, JpaSpecificationExecutor<TaskDeduction> {

    TaskDeduction findTopByTaskAndToPersonOrderByCodeDesc(Task task, Person toPerson);
    List<TaskDeduction> findByTask(Task task);
    List<TaskDeduction> findByTaskIn(List<Task> tasks);
    List<TaskDeduction> findByTaskInAndDateBetween(List<Task> tasks, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    List<TaskDeduction> findByTaskAndType(Task task, TaskDeduction.TaskDeductionType type);
    List<TaskDeduction> findByTaskIdAndType(Long task, TaskDeduction.TaskDeductionType type);
    List<TaskDeduction> findByTaskAndToPerson(Task task, Person person);
    List<TaskDeduction> findByTaskAndToPersonAndDateBetween(Task task, Person person, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    List<TaskDeduction> findByTaskIdAndToPersonId(Long task, Long person);
    List<TaskDeduction> findByTaskIdAndToPersonIdAndType(Long task, Long person, TaskDeduction.TaskDeductionType type);
    List<TaskDeduction> findByTaskIdAndToPersonIdAndTypeIn(Long task, Long person, List<Integer> types);
    List<TaskDeduction> findByTaskAndDateBetween(Task task, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    List<TaskDeduction> findByTaskAndTypeAndDateBetween(Task task, TaskDeduction.TaskDeductionType type, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    List<TaskDeduction> findByTaskAndDateAfterAndDateBefore(Task task, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    long countByTaskAndToPersonAndDateBetween(Task task, Person person, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    long countByTaskAndToPersonAndDateAfterAndDateBefore(Task task, Person person, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    long countByTaskAndToPersonAndTypeAndDateAfterAndDateBefore(Task task, Person person, TaskDeduction.TaskDeductionType type, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    long countByTaskAndToPersonAndTypeAndDateBetween(Task task, Person person, TaskDeduction.TaskDeductionType type, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    long countByDateBetween(@Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    long countByTaskAndToPersonAndType(Task task, Person person, TaskDeduction.TaskDeductionType type);
    long countByTaskAndToPerson(Task task, Person person);
    long countByTaskAndType(Task task, TaskDeduction.TaskDeductionType type);
}
