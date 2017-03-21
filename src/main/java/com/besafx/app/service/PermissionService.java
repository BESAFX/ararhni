package com.besafx.app.service;

import com.besafx.app.entity.Permission;
import com.besafx.app.entity.Screen;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface PermissionService extends PagingAndSortingRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {

    Permission findByCreateEntityAndUpdateEntityAndDeleteEntityAndReportEntityAndScreen
            (Boolean createEntity, Boolean updateEntity, Boolean deleteEntity, Boolean reportEntity, Screen screen);
}
