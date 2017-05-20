package com.besafx.app.service;

import com.besafx.app.entity.TaskOperationAttach;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface TaskOperationAttachService extends PagingAndSortingRepository<TaskOperationAttach, Long>, JpaSpecificationExecutor<TaskOperationAttach> {

    List<TaskOperationAttach> findByLink(String link);
}
