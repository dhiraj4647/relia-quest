package com.example.rqchallenge.service;

import com.example.rqchallenge.helper.EmployeeHelper;
import com.example.rqchallenge.model.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmployeeService {

    @Autowired
    EmployeeHelper employeeHelper;

    public List<Employee> getAllEmployees() {
        try{
            log.info("Fetching all employees data list");
            return employeeHelper.fetchAllEmployeeData();
        } catch (Exception e) {
            throw e;
        }
    }

    public List<Employee> filterEmpNameFromSearchString(String searchString) {
        try {
            List<Employee> employeeList = employeeHelper.fetchAllEmployeeData();

            log.info("Filtering the employee names according to search string {}",searchString);
            List<Employee> filterNameEmpList = employeeList.stream()
                    .filter(employee -> employee.getName().toLowerCase().contains(searchString.toLowerCase())
            ).collect(Collectors.toList());
            return filterNameEmpList;
        } catch (Exception e) {
            log.error("Error occurred while filtering employee names based on searchString : {}",searchString);
            throw e;
        }
    }

    public Integer getHighestSalaryOfEmployees(){
        try {

            List<Employee> employeeList = employeeHelper.fetchAllEmployeeData();
            log.info("Fetching highest salary from employee list");
            return employeeList.stream().mapToInt(Employee::getSalary).max().getAsInt();
        } catch (Exception e) {
            log.error("Error occurred while filtering highest salary for emp");
            throw e;
        }
    }

    public List<String> getTopTenHighestEarningEmployeeNames() {
        try {
            List<Employee> employeeList = employeeHelper.fetchAllEmployeeData();

            log.info("Filtering the top-10 highest salary details for employees");
            List<String> empNameList = employeeList.stream()
                    .sorted(Comparator.comparingInt(Employee::getSalary).reversed())
                    .limit(10)
                    .map(Employee::getName)
                    .collect(Collectors.toList());
            return empNameList;
        } catch (Exception e) {
            log.error("Error occurred while retrieving top 10 highest salary name");
            throw e;
        }
    }

    public Employee getEmployeeDetailsById(String id) {
        try {
            log.info("Fetching the data employee details for id: {}",id);
            //TODO check if id is null or not
            return employeeHelper.fetchEmployeeDetailsById(Integer.valueOf(id));
        } catch (NumberFormatException nfe) {
            log.error("Invalid input provided for field id: {}, expecting integer value",id);
            throw nfe;
        } catch (Exception e) {
            log.error("Error occurred while fetching the data for id {}",id);
            throw e;
        }
    }

    public String deleteEmployeeById(String id) {
        try {
            return employeeHelper.deleteEmployeeDetailsById(Integer.valueOf(id));
        } catch (NumberFormatException nfe) {
            log.error("Invalid input provided for field id: {}, expecting integer value",id);
            throw nfe;
        } catch (Exception e) {
            log.error("Error occurred while deleting the data for id {}",id);
            throw e;
        }
    }

    public Employee createEmployee(Map<String,Object> data) {
        try{
            log.info("Saving the employee details for employee {}",data.get("name"));
            return employeeHelper.createEmployee(data);
        } catch (Exception e) {
            throw e;
        }
    }

}
