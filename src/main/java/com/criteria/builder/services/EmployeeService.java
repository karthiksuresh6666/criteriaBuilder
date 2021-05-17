package com.criteria.builder.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.criteria.builder.constants.BuilderConstants;
import com.criteria.builder.dto.Employee;
import com.criteria.builder.dto.EmployeeFilter;
import com.criteria.builder.dto.RestResponse;
import com.criteria.builder.dto.SearchRequest;
import com.criteria.builder.repository.EmployeeRepository;
import com.criteria.builder.startup.MasterDataPersist;

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
		var restResponse = new RestResponse();
		var emp = employeeRepository.save(dtoToEntityMapper(employee));
		restResponse.setData(emp.getId());
		LOGGER.trace("<<createEmployee()");
		return restResponse;
	}

	public RestResponse findEmployees(@Valid SearchRequest searchRequest) {
		LOGGER.trace(">>findEmployees()");
		RestResponse restResponse = new RestResponse();
		var criteriaBuilder = entityManager.getCriteriaBuilder();
		List<Predicate> predicates = null;
		Set<com.criteria.builder.entities.Employee> responseList = new HashSet<>();
		var maxResultLimit = 10;
		if (CollectionUtils.isNotEmpty(searchRequest.getEmployeeFilters())) {
			for (EmployeeFilter employeeFilter : searchRequest.getEmployeeFilters()) {
				List<com.criteria.builder.entities.Employee> filteredList = null;
				predicates = new ArrayList<>();
				CriteriaQuery<com.criteria.builder.entities.Employee> criteriaQuery = criteriaBuilder
						.createQuery(com.criteria.builder.entities.Employee.class);
				CriteriaQuery<Long> criteriaQueryCount = criteriaBuilder.createQuery(Long.class);

				Root<com.criteria.builder.entities.Employee> employeeEntityRoot = criteriaQuery
						.from(com.criteria.builder.entities.Employee.class);
				Root<com.criteria.builder.entities.Employee> employeeEntityRootCount = criteriaQueryCount
						.from(com.criteria.builder.entities.Employee.class);

				employeeEntityRootCount.alias("emp_id");

				switch (employeeFilter.getKey()) {
				case BuilderConstants.FIRST_NAME:
					createCriteriaQueryForString(employeeEntityRoot, predicates, criteriaBuilder, employeeFilter);
					break;
				case BuilderConstants.LAST_NAME:
					createCriteriaQueryForString(employeeEntityRoot, predicates, criteriaBuilder, employeeFilter);
					break;
				case BuilderConstants.DEPARTMENT:
					predicates.add(employeeEntityRoot.get(BuilderConstants.DEPARTMENT).in(employeeFilter.getValue()));
					break;
				case BuilderConstants.SALARY:
					createCriteriaQueryForNumbers(employeeEntityRoot, predicates, criteriaBuilder, employeeFilter);
					break;
				case BuilderConstants.AGE:
					createCriteriaQueryForNumbers(employeeEntityRoot, predicates, criteriaBuilder, employeeFilter);
					break;
				case BuilderConstants.START_DATE:
					createCriteriaQueryForDate(employeeEntityRoot, predicates, criteriaBuilder, employeeFilter);
					break;
				case BuilderConstants.END_DATE:
					createCriteriaQueryForDate(employeeEntityRoot, predicates, criteriaBuilder, employeeFilter);
					break;
				default:
					break;
				}
				criteriaQuery.select(employeeEntityRoot);
				criteriaQueryCount.select(criteriaBuilder.count(employeeEntityRootCount));
				try {
					criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
					criteriaQuery.orderBy(criteriaBuilder.desc(employeeEntityRoot.get("firstName")),
							criteriaBuilder.desc(employeeEntityRoot.get("lastName")));

					Long count = entityManager.createQuery(criteriaQueryCount).getSingleResult();
					var totalElements = count.intValue();
					int remainder = totalElements % 10;
					int quotient = totalElements / 10;
					if (remainder > 0) {
						quotient++;
					}
					LOGGER.debug("total results :{},total pages :{} ", totalElements, quotient);

					TypedQuery<com.criteria.builder.entities.Employee> typedQuery = entityManager
							.createQuery(criteriaQuery);
					typedQuery.setMaxResults(maxResultLimit);
					filteredList = typedQuery.getResultList();
				} catch (PersistenceException ex) {
					LOGGER.error("findEmployees() :{}", ex.getMessage());
				}
				responseList.addAll(filteredList);
			}
		}
		restResponse.setData(responseList);
		LOGGER.trace("<<findEmployees()");
		return restResponse;
	}

	private com.criteria.builder.entities.Employee dtoToEntityMapper(Employee employee) {
		var emp = new com.criteria.builder.entities.Employee();
		emp.setFirstName(employee.getFirstName());
		emp.setLastName(employee.getLastName());
		emp.setDeptId(employee.getDeptId());
		emp.setAge(employee.getAge());
		emp.setSalary(employee.getSalary());
		emp.setStartDate(employee.getStartDate());
		emp.setEndDate(employee.getEndDate());
		return emp;
	}

	private void createCriteriaQueryForString(Root<com.criteria.builder.entities.Employee> root,
			List<Predicate> predicates, CriteriaBuilder criteriaBuilder, EmployeeFilter employeeFilter) {
		Predicate empFilterForString = null;
		switch (employeeFilter.getOperator().toLowerCase()) {
		case BuilderConstants.CONTAINS:
			empFilterForString = criteriaBuilder.and(
					criteriaBuilder.like(root.get(employeeFilter.getKey()), "%" + employeeFilter.getValue() + "%"));
			break;
		case BuilderConstants.DO_NOT_CONTAIN:
			// criteriaBuilder.lower()
			empFilterForString = criteriaBuilder.and(
					criteriaBuilder.notLike(root.get(employeeFilter.getKey()), "%" + employeeFilter.getValue() + "%"));
			break;
		case BuilderConstants.EQUAL:
			empFilterForString = criteriaBuilder
					.and(criteriaBuilder.like(root.get(employeeFilter.getKey()), employeeFilter.getValue()));
			break;
		case BuilderConstants.NOT_EQUAL:
			empFilterForString = criteriaBuilder
					.and(criteriaBuilder.notLike(root.get(employeeFilter.getKey()), employeeFilter.getValue()));
			break;
		default:
			LOGGER.error("invalid operator filter :{}", employeeFilter.getOperator());
		}
		predicates.add(empFilterForString);
	}

	private void createCriteriaQueryForNumbers(Root<com.criteria.builder.entities.Employee> employeeEntityRoot,
			List<Predicate> predicates, CriteriaBuilder criteriaBuilder, EmployeeFilter employeeFilter) {
		Predicate empFilterForNumber = null;
		switch (employeeFilter.getOperator().toLowerCase()) {
		case BuilderConstants.GREATER:
			empFilterForNumber = criteriaBuilder
					.and(criteriaBuilder.greaterThan(employeeEntityRoot.get(employeeFilter.getKey()).as(Integer.class),
							Integer.valueOf(employeeFilter.getValue())));
			break;
		case BuilderConstants.LESSER:
			empFilterForNumber = criteriaBuilder
					.and(criteriaBuilder.lessThan(employeeEntityRoot.get(employeeFilter.getKey()).as(Integer.class),
							Integer.valueOf(employeeFilter.getValue())));
			break;
		case BuilderConstants.EQUAL:
			empFilterForNumber = criteriaBuilder
					.and(criteriaBuilder.equal(employeeEntityRoot.get(employeeFilter.getKey()).as(Integer.class),
							Integer.valueOf(employeeFilter.getValue())));
			break;
		case BuilderConstants.NOT_EQUAL:
			empFilterForNumber = criteriaBuilder
					.and(criteriaBuilder.notEqual(employeeEntityRoot.get(employeeFilter.getKey()).as(Integer.class),
							Integer.valueOf(employeeFilter.getValue())));
			break;
		default:
			LOGGER.error("invalid operator filter :{}", employeeFilter.getOperator());
		}
		predicates.add(empFilterForNumber);
	}

	private void createCriteriaQueryForDate(Root<com.criteria.builder.entities.Employee> employeeEntityRoot,
			List<Predicate> predicates, CriteriaBuilder criteriaBuilder, EmployeeFilter employeeFilter) {
		Predicate empFilterForDate = null;
		switch (employeeFilter.getOperator().toLowerCase()) {
		case BuilderConstants.GREATER:
			empFilterForDate = criteriaBuilder
					.and(criteriaBuilder.greaterThanOrEqualTo(employeeEntityRoot.get(employeeFilter.getKey()),
							MasterDataPersist.getLocalDateTimeInUTC(employeeFilter.getValue())));
			break;
		case BuilderConstants.LESSER:
			empFilterForDate = criteriaBuilder
					.and(criteriaBuilder.lessThanOrEqualTo(employeeEntityRoot.get(employeeFilter.getKey()),
							MasterDataPersist.getLocalDateTimeInUTC(employeeFilter.getValue())));
			break;
		default:
			LOGGER.error("invalid operator filter : {}", employeeFilter.getOperator());
		}
		predicates.add(empFilterForDate);
	}

}
