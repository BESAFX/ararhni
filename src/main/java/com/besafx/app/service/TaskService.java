package com.besafx.app.service;

import com.besafx.app.entity.Person;
import com.besafx.app.entity.Task;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface TaskService extends PagingAndSortingRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    @Query("select max(code) from Task")
    Integer findMaxCode();

    List<Task> findByPerson(Person person);
}
