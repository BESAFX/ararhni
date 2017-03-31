package com.besafx.app.service;

import com.besafx.app.entity.Person;
import com.besafx.app.entity.Task;
import com.besafx.app.entity.TaskOperation;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public interface TaskOperationService extends PagingAndSortingRepository<TaskOperation, Long>, JpaSpecificationExecutor<TaskOperation> {

    @Query("select max(code) from TaskOperation c where (c.task.id) = (:id)")
    Integer findLastCodeByTask(@Param("id") Long id);

    List<TaskOperation> findByTask(Task task);

    List<TaskOperation> findByTaskAndDateBetween(Task task, @Temporal(TemporalType.DATE) Date startDate, @Temporal(TemporalType.DATE) Date endDate);

    List<TaskOperation> findByTaskAndDateAfterAndDateBefore(Task task, @Temporal(TemporalType.DATE) Date startDate, @Temporal(TemporalType.DATE) Date endDate);

    long countByTaskAndSenderAndDateBetween(Task task, Person person, @Temporal(TemporalType.DATE) Date startDate, @Temporal(TemporalType.DATE) Date endDate);

    long countByTaskAndSenderAndDateAfterAndDateBefore(Task task, Person person, @Temporal(TemporalType.DATE) Date startDate, @Temporal(TemporalType.DATE) Date endDate);

    long countByTaskAndSenderAndTypeAndDateAfterAndDateBefore(Task task, Person person, Integer type, @Temporal(TemporalType.DATE) Date startDate, @Temporal(TemporalType.DATE) Date endDate);
}
