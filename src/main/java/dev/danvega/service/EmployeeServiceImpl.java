package dev.danvega.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.danvega.domain.Employee;
import dev.danvega.domain.Model.RequestModel;
import dev.danvega.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public void saveEmployee(RequestModel employeeModel){
        Employee employee = null;
        if(employeeModel.getId() != null){
            employee = employeeRepository.findById(employeeModel.getId()).orElse(null);
        }else{
            employee = new Employee();
        }
        employee.setName(employeeModel.getName());
        employee.setNumber(employeeModel.getNumber());
        employeeRepository.save(employee);
        System.out.println("Employees Saved!");
    }

    public void employeeSave(){
        // read JSON and load json
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Employee>> typeReference = new TypeReference<List<Employee>>(){};
        InputStream inputStream = TypeReference.class.getResourceAsStream("/json/employee.json");
        try {
            List<Employee> employees = mapper.readValue(inputStream,typeReference);
            employeeRepository.saveAll(employees);
            System.out.println("Employees Saved!");
        } catch (IOException e){
            System.out.println("Unable to save employees: " + e.getMessage());
        }
    }
    @Override
    public Iterable<Employee> list() {
        return employeeRepository.findAll();
    }



}
