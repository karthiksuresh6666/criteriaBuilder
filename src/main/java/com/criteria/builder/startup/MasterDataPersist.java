package com.criteria.builder.startup;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.CannotCreateTransactionException;

import com.criteria.builder.entities.Employee;
import com.criteria.builder.repository.EmployeeRepository;

/**
 * @author Karthik Suresh
 *
 */
@Configuration
public class MasterDataPersist {

	private static final Logger LOGGER = LoggerFactory.getLogger(MasterDataPersist.class);

	@Autowired
	private BuildProperties buildProperties;

	@Autowired
	private EmployeeRepository employeeRepository;

	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS";

	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

	@PostConstruct
	public void saveDefaultEmployees() {
		LOGGER.trace(">>saveDefaultEmployees()");
		try {
			List<Employee> empList = this.employeeRepository.findAll();
			String dbVersion = buildProperties.get("databaseVersion");
			String[] versions = dbVersion.split("\\.");
			var currentMajorDbVersion = Integer.parseInt(versions[0]);
			var currentMinorDbVersion = Integer.parseInt(versions[1]);

			LOGGER.debug("DB Version :{}", dbVersion);
			LOGGER.debug("DB Major Version :{}", currentMajorDbVersion);
			LOGGER.debug("DB Minor Version :{}", currentMinorDbVersion);

			if (empList.isEmpty()) {

				var emp1 = new Employee();
				emp1.setFirstName("Uttam");
				emp1.setLastName("Kumar");
				emp1.setDeptId(1);
				emp1.setSalary(10000);
				emp1.setAge((short) 29);
				emp1.setStartDate(getLocalDateTimeInUTC("2016-05-16 16:07:16.126"));
				emp1.setEndDate(getLocalDateTimeInUTC("2021-05-14 16:07:16.126"));

				var emp2 = new Employee();
				emp2.setFirstName("Karthik");
				emp2.setLastName("Suresh");
				emp2.setDeptId(1);
				emp2.setSalary(20000);
				emp2.setAge((short) 29);
				emp2.setStartDate(getLocalDateTimeInUTC("2013-12-16T16:07:16.126218700"));
				emp2.setEndDate(getLocalDateTimeInUTC("2021-05-09T16:07:16.126218700"));

				var emp3 = new Employee();
				emp3.setFirstName("Madhusudhan");
				emp3.setLastName("AB");
				emp3.setDeptId(2);
				emp3.setSalary(30000);
				emp3.setAge((short) 29);
				emp3.setStartDate(getLocalDateTimeInUTC("2014-03-01T16:07:16.126218700"));

				var emp4 = new Employee();
				emp4.setFirstName("Prasada");
				emp4.setLastName("Shetty");
				emp4.setDeptId(3);
				emp4.setSalary(40000);
				emp4.setAge((short) 29);
				emp4.setStartDate(getLocalDateTimeInUTC("2013-12-24T16:07:16.126218700"));

				var emp5 = new Employee();
				emp5.setFirstName("Prasad");
				emp5.setLastName("Upadhyaya");
				emp5.setDeptId(4);
				emp5.setSalary(20000);
				emp5.setAge((short) 29);
				emp5.setStartDate(getLocalDateTimeInUTC("2018-05-01T16:07:16.126218700"));

				List<Employee> employees = Arrays.asList(emp1, emp2, emp3, emp4, emp5);
				this.employeeRepository.saveAll(employees);

			} else {
				LOGGER.debug("Employees already present");
			}
		} catch (CannotCreateTransactionException | DataAccessResourceFailureException ex) {
			LOGGER.error("Transaction/DataAccessResourceFailure Exception occured in saveDefaultEmployees() :{}",
					ex.getMessage());
		} catch (JpaSystemException ex) {
			LOGGER.error("JpaSystemException occured in saveDefaultEmployees() :{}", ex.getMessage());
		} catch (DataAccessException ex) {
			LOGGER.error("DataAccessException occured in saveDefaultEmployees() :{}", ex.getMessage());
		}
		LOGGER.trace("<<saveDefaultEmployees()");
	}

	public static LocalDateTime getLocalDateTimeInUTC(String timestamp) {
		LocalDateTime localDateTime = null;
		try {
			localDateTime = LocalDateTime.parse(timestamp, dateFormatter);
		} catch (DateTimeParseException ex) {
			LOGGER.error("getLocalDateTimeInUTC() parsing local date time :{}", timestamp);
		}
		return localDateTime;
	}

}
