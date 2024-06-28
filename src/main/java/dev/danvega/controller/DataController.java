package dev.danvega.controller;

import dev.danvega.domain.Model.DepartmentModel;
import dev.danvega.domain.Model.EmployeeModel;
import dev.danvega.service.DepartmentService;
import dev.danvega.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("")
public class DataController {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private EmployeeService employeeService;


    @GetMapping("")
    public  String processData() {
        departmentService.departmentSave();
        employeeService.employeeSave();
        return "map";
    }

    @PostMapping("/save/department")
    public  String saveDepartment(@RequestBody DepartmentModel departmentModel) {
        departmentService.saveDepartment(departmentModel);
        employeeService.employeeSave();
        return "save";
    }
    @PostMapping("/save/employee")
    public String saveEmployee(@RequestBody EmployeeModel employeeModel) {
        employeeService.saveEmployee(employeeModel);
        return "save";
    }

    @GetMapping("/list")
    public  Map<String, Object> list() {
        Map<String, Object> map = new HashMap<>();

        map.put("departments",departmentService.list());
        map.put("employees",employeeService.list());
        return map;
    }

}
