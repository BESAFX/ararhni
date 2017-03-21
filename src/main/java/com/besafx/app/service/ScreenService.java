package com.besafx.app.service;

import com.besafx.app.entity.Screen;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface ScreenService extends PagingAndSortingRepository<Screen, Long>, JpaSpecificationExecutor<Screen> {

}
