package com.criteria.builder.dto;

import java.util.List;

/**
 * @author Karthik Suresh
 *
 */
public class SearchRequest {

	List<EmployeeFilter> employeeFilters;

	public List<EmployeeFilter> getEmployeeFilters() {
		return employeeFilters;
	}

	public void setEmployeeFilters(List<EmployeeFilter> employeeFilters) {
		this.employeeFilters = employeeFilters;
	}

}
