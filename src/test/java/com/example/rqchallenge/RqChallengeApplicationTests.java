package com.example.rqchallenge;

import com.example.rqchallenge.helper.EmployeeHelper;
import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.repository.EmployeeRepository;
import com.example.rqchallenge.service.EmployeeService;
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
import java.util.stream.Collectors;

import static com.example.rqchallenge.constant.EmployeeConstant.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeHelper employeeHelper;

    @InjectMocks
    private EmployeeService employeeService;
    public List<Employee> getMockListOfEmp(){
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

    private HttpEntity getEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(headers);
    }

    @Test
    void contextLoads() {
    }

    @Test
    public void filterEmpNameFromSearchStringTest() {
        String searchString = "raj";
        List<Employee> mockEmployees = getMockListOfEmp();
        List<Employee> filterListMockResponse = mockEmployees.stream()
                .filter(employee -> employee.getName().toLowerCase().contains(searchString.toLowerCase()))
                .collect(Collectors.toList());

        // Mock response for external API call
        when(employeeHelper.fetchAllEmployeeData()).thenReturn(mockEmployees);
        List<Employee> actualResponse = employeeService.filterEmpNameFromSearchString(searchString);

        // Verify results
        assertEquals(filterListMockResponse.size(), actualResponse.size());
    }


    @Test
    public void getHighestSalaryOfEmployeesTest() {

        List<Employee> mockEmployees = getMockListOfEmp();
        Integer mockHighestSal = mockEmployees.stream().mapToInt(Employee::getSalary).max().getAsInt();

        // Mock response for external API call
        when(employeeHelper.fetchAllEmployeeData()).thenReturn(mockEmployees);
        Integer actualHighestSal = employeeService.getHighestSalaryOfEmployees();

        // Verify results
        assertEquals(mockHighestSal, actualHighestSal);
    }

    @Test
    public void getTopTenHighestEarningEmployeeNamesTest() {
        // Mock response for external API call
        List<Employee> mockEmployees = getMockListOfEmp();
        List<String> mockEmpNameList = mockEmployees.stream()
                .sorted(Comparator.comparingInt(Employee::getSalary).reversed())
                .limit(10)
                .map(Employee::getName)
                .collect(Collectors.toList());

        when(employeeHelper.fetchAllEmployeeData()).thenReturn(mockEmployees);
        List<String> actualEmpNameList = employeeService.getTopTenHighestEarningEmployeeNames();

        // Verify results
        assertEquals(mockEmpNameList, actualEmpNameList);
    }

}
