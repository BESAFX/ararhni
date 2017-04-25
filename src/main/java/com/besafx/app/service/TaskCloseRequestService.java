package com.besafx.app.service;
import com.besafx.app.entity.Person;
import com.besafx.app.entity.Task;
import com.besafx.app.entity.TaskCloseRequest;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface TaskCloseRequestService extends PagingAndSortingRepository<TaskCloseRequest, Long>, JpaSpecificationExecutor<TaskCloseRequest> {

    long countByPersonAndTask(Person person, Task task);
    @Query("select max(code) from TaskCloseRequest c where (c.task.id) = (:id)")
    Integer findLastCodeByTask(@Param("id") Long id);
    List<TaskCloseRequest> findByIdIn(List<Long> listId);
    List<TaskCloseRequest> findByTaskAndType(Task task, Boolean type);
    List<TaskCloseRequest> findByTaskIdAndType(Long taskId, Boolean type);
    List<TaskCloseRequest> findByTaskIdAndTypeAndApprovedIsNull(Long taskId, Boolean type);
    List<TaskCloseRequest> findByTask(Task task);
    List<TaskCloseRequest> findByTaskAndPerson(Task task, Person person);
    List<TaskCloseRequest> findByTaskIdAndPersonIdAndApprovedIsNull(Long taskId, Long personId);
    List<TaskCloseRequest> findByTaskIdAndPersonIdAndTypeAndApprovedIsNull(Long taskId, Long personId, Boolean type);
    TaskCloseRequest findTopByTaskOrderByCodeDesc(Task task);
    TaskCloseRequest findTopByTaskIdOrderByCodeDesc(Long taskId);
}
