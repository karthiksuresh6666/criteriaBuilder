package com.criteria.builder.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.criteria.builder.dto.Employee;
import com.criteria.builder.dto.RestResponse;
import com.criteria.builder.services.EmployeeService;

/**
 * @author Karthik Suresh
 *
 */
@RestController
@RequestMapping("/v1/emp/")
public class EmployeeController {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeController.class);

	@Autowired
	private EmployeeService employeeService;

	@PostMapping("create")
	public ResponseEntity<RestResponse> createEmployee(@Valid @RequestBody final Employee employee) {
		LOGGER.trace(">>createEmployee()");
		var restResponse = this.employeeService.createEmployee(employee);
		return new ResponseEntity<>(restResponse, HttpStatus.OK);
	}

	@PostMapping("find")
	public ResponseEntity<RestResponse> findEmployee(@Valid @RequestBody final Employee employee) {
		LOGGER.trace(">>updateOrder()");
		var restResponse = this.employeeService.findEmployee(employee);
		return new ResponseEntity<>(restResponse, HttpStatus.OK);
	}

}