package dev.danvega.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.danvega.domain.Department;
import dev.danvega.domain.Model.DepartmentModel;
import dev.danvega.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void saveDepartment(DepartmentModel departmentModel){
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
    public void departmentSave(){
        // read JSON and load json
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Department>> typeReference = new TypeReference<List<Department>>(){};
        InputStream inputStream = TypeReference.class.getResourceAsStream("/json/department.json");
        try {
            List<Department> departments = mapper.readValue(inputStream,typeReference);
            departmentRepository.saveAll(departments);
            System.out.println("Departments Saved!");
        } catch (IOException e){
            System.out.println("Unable to save departments: " + e.getMessage());
        }
    }

    @Override
    public Iterable<Department> list() {
        return departmentRepository.findAll();
    }

}

