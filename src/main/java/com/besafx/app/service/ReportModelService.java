package com.besafx.app.service;

import com.besafx.app.entity.ReportModel;
import com.besafx.app.entity.Screen;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface ReportModelService extends PagingAndSortingRepository<ReportModel, Long>, JpaSpecificationExecutor<ReportModel> {

    List<ReportModel> findByScreen(Screen screen);
}
