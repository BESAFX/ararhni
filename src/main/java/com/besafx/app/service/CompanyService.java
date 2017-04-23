package com.besafx.app.service;
import com.besafx.app.entity.Company;
import com.besafx.app.entity.Person;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface CompanyService extends PagingAndSortingRepository<Company, Long>, JpaSpecificationExecutor<Company> {

    @Query("select max(code) from Company")
    Integer findMaxCode();
    List<Company> findByManager(Person manager);
}
