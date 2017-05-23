package com.besafx.app.service;
import com.besafx.app.entity.Person;
import com.besafx.app.entity.Task;
import com.besafx.app.entity.TaskWarn;
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
public interface TaskWarnService extends PagingAndSortingRepository<TaskWarn, Long>, JpaSpecificationExecutor<TaskWarn> {

    TaskWarn findTopByTaskAndToPersonOrderByCodeDesc(Task task, Person toPerson);
    List<TaskWarn> findByTask(Task task);
    List<TaskWarn> findByTaskIn(List<Task> tasks);
    List<TaskWarn> findByTaskInAndDateBetween(List<Task> tasks, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    List<TaskWarn> findByTaskAndType(Task task, TaskWarn.TaskWarnType type);
    List<TaskWarn> findByTaskIdAndType(Long task, TaskWarn.TaskWarnType type);
    List<TaskWarn> findByTaskIdAndToPersonId(Long task, Long person);
    List<TaskWarn> findByTaskIdAndToPersonIdAndType(Long task, Long person, TaskWarn.TaskWarnType type);
    List<TaskWarn> findByTaskIdAndToPersonIdAndTypeIn(Long task, Long person, List<Integer> types);
    List<TaskWarn> findByTaskAndDateBetween(Task task, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    List<TaskWarn> findByTaskAndTypeAndDateBetween(Task task, TaskWarn.TaskWarnType type, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    List<TaskWarn> findByTaskAndDateAfterAndDateBefore(Task task, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    long countByTaskAndToPersonAndDateBetween(Task task, Person person, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    long countByTaskAndToPersonAndDateAfterAndDateBefore(Task task, Person person, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    long countByTaskAndToPersonAndTypeAndDateAfterAndDateBefore(Task task, Person person, TaskWarn.TaskWarnType type, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    long countByTaskAndToPersonAndTypeAndDateBetween(Task task, Person person, TaskWarn.TaskWarnType type, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    long countByDateBetween(@Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    long countByTaskAndToPersonAndType(Task task, Person person, TaskWarn.TaskWarnType type);
    long countByTaskAndToPerson(Task task, Person person);
    long countByTaskAndType(Task task, TaskWarn.TaskWarnType type);
}
