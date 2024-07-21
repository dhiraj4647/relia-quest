package com.example.rqchallenge.helper;

import com.example.rqchallenge.exception.EmployeeDataNotFoundException;
import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.rqchallenge.constant.EmployeeConstant.*;

@Component
@Slf4j
public class EmployeeHelper {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    EmployeeRepository employeeRepository;

    private HttpEntity getEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(headers);
    }

    public List<Employee> fetchAllEmployeeData() {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(BASE_URL+FETCH_ALL_EMPLOYEES);
            ResponseEntity<List<Employee>> response = restTemplate
                    .exchange(builder.build().toUri(), HttpMethod.GET,getEntity(),new ParameterizedTypeReference<List<Employee>>() {
            });
            List<Employee> employeeList = response.getBody();
            saveEmployeeListIntoCacheDb(employeeList);
            return employeeList;
        } catch (HttpClientErrorException | HttpServerErrorException he) {
            log.error("Error occurred while fetching employee list from the external API, so now fetching from in-cache db ",he);
            return fetchEmployeeListFromInCacheDb();
        } catch (Exception e) {
            log.error("Error occurred while fetching data from the external API",e);
            throw e;
        }
    }

    private void saveEmployeeListIntoCacheDb(List<Employee> employeeList) {
        try{
            log.info("Saving the employee list in in-cache db");
            employeeRepository.saveAll(employeeList);
        } catch (Exception e) {
            log.error("Error occurred while saving the employee list into in-cache db ",e);
        }
    }

    private List<Employee> fetchEmployeeListFromInCacheDb() {
        try{
            log.info("Fetching all employee list from the in-cache db");
            return employeeRepository.findAll();
        } catch (Exception e) {
            log.error("Error occurred while fetching all employee list from the in-cache db ",e);
            throw e;
        }
    }

    public Employee fetchEmployeeDetailsById(Integer id) {
        try{
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(BASE_URL+FETCH_EMPLOYEE_DETAILS_BY_ID+id);
            ResponseEntity<Employee> response = restTemplate
                    .exchange(builder.build().toUri(), HttpMethod.GET,getEntity(),Employee.class);
            Employee employee = response.getBody();
            if(employee == null) {
                throw new EmployeeDataNotFoundException("Data Not Found");
            }
            saveEmployeeIntoCacheDb(employee);
            return employee;
        } catch (HttpClientErrorException | HttpServerErrorException he) {
            log.error("Error occurred while fetching employee details from the external API for Id {}, " +
                    "so now fetching from in-cache db: {} ",id,he);
            return fetchEmployeeDetailsFromInCacheDb(id);
        } catch (EmployeeDataNotFoundException ee) {
            log.error("Employee details for Id {} not found",id);
            throw ee;
        } catch (Exception e) {
            log.error("Error occurred while fetching employee details for Id {} ",id,e);
            throw new RuntimeException(e);
        }
    }

    private Employee fetchEmployeeDetailsFromInCacheDb(Integer id) {
        log.info("Retrieving employee details for employee Id {}", id);
        return employeeRepository.findById(id).orElseThrow(()->new EmployeeDataNotFoundException("Data Not Found"));
    }

    private void saveEmployeeIntoCacheDb(Employee employee) {
        try{
            employeeRepository.save(employee);
        } catch(Exception e) {
            log.error("Error occurred while saving the employee details for id into the in-cache db");
        }
    }

    public String deleteEmployeeDetailsById(Integer id) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(BASE_URL+DELETE_EMPLOYEE_DETAILS_BY_ID+id);
            ResponseEntity<Void> response = restTemplate
                    .exchange(builder.build().toUri(), HttpMethod.DELETE,getEntity(),Void.class);
            if(response.getStatusCode().is2xxSuccessful()) {
                log.info("Employee details for id {} deleted successfully.",id);
                deleteEmployeeDetailsFromCacheDb(id);
            }
            return "successfully! deleted Record";
        } catch (HttpClientErrorException ex) {
            log.error("Error occurred while connecting to API ",ex);
            throw ex;
        } catch (Exception e) {
            log.error("Employee details not deleted for id {}",id,e);
            throw e;
        }
    }

    private void deleteEmployeeDetailsFromCacheDb(Integer id) {
        log.info("Retrieving employee details for employee Id {} from in-cache db", id);
        Optional<Employee> employeeOptional = employeeRepository.findById(id);
        if(employeeOptional.isPresent()) {
            employeeRepository.delete(employeeOptional.get());
        } else {
            log.info("Data not found in-memory cache for id {}",id);
        }
    }

    public Employee createEmployee(Map<String,Object> data) {
        try{
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(BASE_URL+CREATE_EMPLOYEE_RECORD);
            ResponseEntity<Employee> response = restTemplate
                    .exchange(builder.build().toUri(), HttpMethod.POST,new HttpEntity<>(data),Employee.class);
            Employee employee = response.getBody();
            saveEmployeeIntoCacheDb(employee);
            return employee;
        } catch (HttpClientErrorException ex) {
            log.error("Error occurred while connecting to API ",ex);
            throw ex;
        } catch (Exception e) {
            log.error("Error occurred while storing the employee details into db");
            throw e;
        }
    }

}
