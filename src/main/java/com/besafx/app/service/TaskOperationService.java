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

    TaskOperation findTopByTaskOrderByCodeDesc(Task task);

    TaskOperation findTopByTaskIdOrderByCodeDesc(Long taskId);

    List<TaskOperation> findByTask(Task task);

    List<TaskOperation> findByTaskAndDateBetween(Task task, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);

    long countByTaskAndSenderAndDateBetween(Task task, Person person, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
}
