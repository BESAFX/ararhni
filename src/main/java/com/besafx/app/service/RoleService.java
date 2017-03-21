package com.besafx.app.service;

import com.besafx.app.entity.Role;
import com.besafx.app.entity.Team;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface RoleService extends PagingAndSortingRepository<Role, Long>, JpaSpecificationExecutor<Role> {

    List<Role> findByTeam(Team team);
}
