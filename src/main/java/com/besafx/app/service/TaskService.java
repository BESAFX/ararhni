package com.besafx.app.service;
import com.besafx.app.entity.Person;
import com.besafx.app.entity.Task;
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
public interface TaskService extends PagingAndSortingRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    Task findTopByOrderByCodeDesc();
    List<Task> findByPerson(Person person);
    List<Task> findByEndDateBetween(@Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
    List<Task> findByCloseTypeAndEndDateBetween(Task.CloseType closeType, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
}
