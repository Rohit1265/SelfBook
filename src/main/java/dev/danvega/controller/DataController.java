package dev.danvega.controller;

import dev.danvega.domain.Model.RequestModel;
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


    @PostMapping("/save/{tableName}")
    public Map<String, Object> saveData(@PathVariable("tableName") String tableName,@RequestBody RequestModel model) {
        Map<String, Object> map = new HashMap<>();
        if(tableName.isEmpty()){
            map.put("error", "table name not enter");
            return map;
        }

        switch (tableName){
           case "department":
                    departmentService.saveDepartment(model);
                    map.put("departments",departmentService.list());
                    break;
            case "employee":
                    employeeService.saveEmployee(model);
                    map.put("employees",employeeService.list());
                    break;
            default:
                map.put("error", "table name wrong enter");
        }
        return map;
    }


    @GetMapping("/list")
    public  Map<String, Object> list() {
        Map<String, Object> map = new HashMap<>();

        map.put("departments",departmentService.list());
        map.put("employees",employeeService.list());
        return map;
    }

}
