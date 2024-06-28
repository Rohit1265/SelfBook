package dev.danvega.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.danvega.domain.Department;
import dev.danvega.domain.Employee;
import dev.danvega.domain.Model.RequestModel;
import dev.danvega.repository.DepartmentRepository;
import dev.danvega.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public void saveDepartment(RequestModel departmentModel){
        Department department = null;
        if(departmentModel.getId() != null){
            department = departmentRepository.findById(departmentModel.getId()).orElse(null);
        }else{
            department = new Department();
        }
        department.setName(departmentModel.getName());
        department.setNumber(departmentModel.getNumber());
        departmentRepository.save(department);
        System.out.println("Department Saved!");
    }

    @Override
    public void saveJsonData(){
        saveData("/json/department.json", new TypeReference<List<Department>>(){}, departmentRepository);
        saveData("/json/employee.json", new TypeReference<List<Employee>>(){}, employeeRepository);
    }


    private <T> void saveData(String jsonPath, TypeReference<List<T>> typeReference, CrudRepository<T, ?> repository) {
        ObjectMapper mapper = new ObjectMapper();
        InputStream inputStream = TypeReference.class.getResourceAsStream(jsonPath);

        try {
            List<T> data = mapper.readValue(inputStream, typeReference);
            repository.saveAll(data);
            System.out.println(typeReference.getType().getTypeName() + " Saved!");
        } catch (IOException e){
            System.out.println("Unable to save " + typeReference.getType().getTypeName() + ": " + e.getMessage());
        }
    }
//
//    @Override
//    public void saveJsonData(){
//        // read JSON and load json
//        ObjectMapper mapper = new ObjectMapper();
//        TypeReference<List<Department>> typeReference = new TypeReference<List<Department>>(){};
//        TypeReference<List<Employee>> employeeTypeReference = new TypeReference<List<Employee>>(){};
//
//        InputStream inputStream = TypeReference.class.getResourceAsStream("/json/department.json");
//        InputStream employeeInputStream = TypeReference.class.getResourceAsStream("/json/employee.json");
//
//        try {
//            List<Department> departments = mapper.readValue(inputStream,typeReference);
//            departmentRepository.saveAll(departments);
//            System.out.println("Departments Saved!");
//        } catch (IOException e){
//            System.out.println("Unable to save departments: " + e.getMessage());
//        }
//
//        try {
//            List<Employee> employees = mapper.readValue(employeeInputStream,employeeTypeReference);
//            employeeRepository.saveAll(employees);
//            System.out.println("Employees Saved!");
//        } catch (IOException e){
//            System.out.println("Unable to save employees: " + e.getMessage());
//        }
//    }

    @Override
    public Iterable<Department> list() {
        return departmentRepository.findAll();
    }

}
