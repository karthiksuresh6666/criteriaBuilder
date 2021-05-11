package com.criteria.builder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.criteria.builder.entities.Employee;

/**
 * @author Karthik Suresh
 *
 */
@Repository
@Transactional
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}



