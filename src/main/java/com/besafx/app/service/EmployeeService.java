package com.besafx.app.service;

import com.besafx.app.entity.Department;
import com.besafx.app.entity.Employee;
import com.besafx.app.entity.Person;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface EmployeeService extends PagingAndSortingRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    @Query("select max(code) from Employee")
    Integer findMaxCode();

    Employee findByPersonAndDepartment(Person person, Department department);
}
