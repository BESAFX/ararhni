package com.besafx.app.service;
import com.besafx.app.entity.Branch;
import com.besafx.app.entity.Department;
import com.besafx.app.entity.Person;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface DepartmentService extends PagingAndSortingRepository<Department, Long>, JpaSpecificationExecutor<Department> {

    @Query("select max(code) from Department")
    Integer findMaxCode();
    List<Department> findByManager(Person manager);
    List<Department> findByBranch(Branch branch);
}
