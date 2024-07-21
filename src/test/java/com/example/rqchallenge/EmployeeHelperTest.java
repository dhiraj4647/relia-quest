package com.example.rqchallenge;


import com.example.rqchallenge.helper.EmployeeHelper;
import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

import static com.example.rqchallenge.constant.EmployeeConstant.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class EmployeeHelperTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeHelper employeeHelper;

    private HttpEntity getEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(headers);
    }

    public static List<Employee> getMockListOfEmp(){
        return Arrays.asList(
                new Employee(1,"Dhiraj",4500,23,""),
                new Employee(2,"Suraj",5500,26,""),
                new Employee(3,"Rajesh",4100,22,""),
                new Employee(4,"Ramesh",4500,23,""),
                new Employee(5,"Rajendra",4101,32,""),
                new Employee(6,"Pavan",6600,31,""),
                new Employee(7,"Shivam",7700,30,""),
                new Employee(8,"Shivraj",2700,50,""),
                new Employee(9,"Viraj",6000,19,""),
                new Employee(10,"Siraj",6235,20,""),
                new Employee(11,"Virat",4511,24,""),
                new Employee(12,"Rohit",8400,23,""),
                new Employee(13,"Rishabh",900,25,"")
        );
    }

    @Test
    public void createEmployee() {
        Map<String, Object> data = new HashMap()
        {{
            put("name", "Dhiraj");
            put("salary", 4512);
            put("age", 23);
        }};
        Employee mockedEmployee = new Employee(1,"Dhiraj",4512,23,"");
        ResponseEntity mockedResponse = new ResponseEntity(mockedEmployee, HttpStatus.OK);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(BASE_URL+CREATE_EMPLOYEE_RECORD);

        //Mock the external API call
        when(restTemplate.exchange(builder.build().toUri(), HttpMethod.POST,new HttpEntity<>(data),Employee.class))
                .thenReturn(mockedResponse);
        Employee actualEmployee = employeeHelper.createEmployee(data);

        assertEquals(actualEmployee,mockedEmployee);
    }

    @Test
    public void deleteEmployeeDetailsByIdTest() {

        String mockResponse = "successfully! deleted Record";
        ResponseEntity<Void> mockedResponseEntity = new ResponseEntity<>(HttpStatus.OK);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(BASE_URL+DELETE_EMPLOYEE_DETAILS_BY_ID+1);
        //Mock the external API call
        when(restTemplate.exchange(builder.build().toUri(), HttpMethod.DELETE,getEntity(),Void.class)).thenReturn(mockedResponseEntity);
        String actualResponse = employeeHelper.deleteEmployeeDetailsById(1);

        assertEquals(actualResponse,mockResponse);
    }

    @Test
    public void fetchEmployeeDetailsByIdTest(){

        Employee mockedEmployee = new Employee(1,"Dhiraj",4512,23,"");
        ResponseEntity mockedResponse = new ResponseEntity(mockedEmployee,HttpStatus.OK);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(BASE_URL+FETCH_EMPLOYEE_DETAILS_BY_ID+1);
        //Mock the external API call
        when(restTemplate.exchange(builder.build().toUri(), HttpMethod.GET,getEntity(),Employee.class))
                .thenReturn(mockedResponse);

        Employee actualEmployee = employeeHelper.fetchEmployeeDetailsById(1);

        assertEquals(mockedEmployee,actualEmployee);
    }

    @Test
    public void testFetchAllEmployeeDataTest() {
        // Mock response for external API call
        List<Employee> mockEmployees = getMockListOfEmp();
        ResponseEntity<List<Employee>> mockResponse = ResponseEntity.ok(mockEmployees);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(BASE_URL+FETCH_ALL_EMPLOYEES);

        //Mock the external API call
        when(restTemplate.exchange(builder.build().toUri(), HttpMethod.GET,getEntity(),new ParameterizedTypeReference<List<Employee>>() {
        })).thenReturn(mockResponse);
        when(employeeRepository.saveAll(Mockito.<List<Employee>>any()))
                .thenReturn(mockEmployees);

        List<Employee> result = employeeHelper.fetchAllEmployeeData();

        // Verify results
        assertEquals(mockEmployees.size(), result.size());
    }
}
