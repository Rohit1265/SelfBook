package dev.danvega.controller;

import dev.danvega.domain.DataMapping;
import dev.danvega.domain.TargetTableName;
import dev.danvega.service.JsonReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("")
public class DataController {

    @Autowired
    JsonReaderService jsonReaderService;

    @GetMapping("/import")
    public String importJson() {
        Map<String, Object> map = new HashMap<>();
        jsonReaderService.createTableFromJson();
//        map.put(TargetTableName.DEPARTMENT_TARGET.toString(), jsonReaderService.getDepartmentTargetData());
//        map.put(TargetTableName.EMPLOYEE_TARGET.toString(), jsonReaderService.getEmployeeTargetData());
        return "Table Created Successfully.";
    }

    @GetMapping("/list")
    public Iterable<DataMapping> getDataMapping() {
        return jsonReaderService.getDataMapping();
    }






}
