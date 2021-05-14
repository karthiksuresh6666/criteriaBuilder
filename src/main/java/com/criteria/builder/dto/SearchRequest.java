package com.criteria.builder.dto;

import java.util.List;

/**
 * @author Karthik Suresh
 *
 */
public class SearchRequest {

	List<EmployeeFilter> empFilterRequest;

	public List<EmployeeFilter> getEmpFilterRequest() {
		return empFilterRequest;
	}

	public void setEmpFilterRequest(List<EmployeeFilter> empFilterRequest) {
		this.empFilterRequest = empFilterRequest;
	}

}
