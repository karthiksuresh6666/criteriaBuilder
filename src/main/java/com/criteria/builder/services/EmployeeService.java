package com.criteria.builder.services;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.criteria.builder.dto.Employee;
import com.criteria.builder.dto.RestResponse;
import com.criteria.builder.repository.EmployeeRepository;
import com.siemens.rtls.entities.NotificationEntity;

/**
 * @author Karthik Suresh
 *
 */
@Service
public class EmployeeService {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeService.class);

	@Autowired
	private EmployeeRepository employeeRepository;
	
	@PersistenceContext
	private EntityManager entityManager;

	public RestResponse createEmployee(@Valid Employee employee) {
		LOGGER.trace(">>createEmployee()");
		RestResponse restResponse = new RestResponse();
		com.criteria.builder.entities.Employee emp = employeeRepository.save(dtoToEntityMapper(employee));
		restResponse.setData(emp.getId());
		return restResponse;
	}

	public RestResponse findEmployee(@Valid Employee employee) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<com.criteria.builder.entities.Employee> criteriaQuery = criteriaBuilder.createQuery(com.criteria.builder.entities.Employee.class);
		CriteriaQuery<Long> criteriaQueryCount = criteriaBuilder.createQuery(Long.class);
		
		Root<com.criteria.builder.entities.Employee> employeeEntityRoot = criteriaQuery.from(com.criteria.builder.entities.Employee.class);
		Root<com.criteria.builder.entities.Employee> employeeEntityRootCount = criteriaQueryCount.from(criteriaQuery.getResultType());
		
		employeeEntityRootCount.alias("notification_id");

		criteriaQuery.select(employeeEntityRoot);
		criteriaQueryCount.select(criteriaBuilder.count(employeeEntityRootCount));
		
		
		criteriaQuery.where(criteriaBuilder.and(
				employeeEntityRoot.get("deptId").in(employee.getDeptId()),
				criteriaBuilder.greaterThanOrEqualTo(employeeEntityRoot.get("startDate"),
						employee.getStartDate())));
		
		return null;
	}

	private com.criteria.builder.entities.Employee dtoToEntityMapper(Employee employee) {
		com.criteria.builder.entities.Employee emp = new com.criteria.builder.entities.Employee();
		emp.setName(employee.getName());
		emp.setDeptId(employee.getDeptId());
		emp.setAge(employee.getAge());
		emp.setSalary(employee.getSalary());
		return emp;

	}

}
