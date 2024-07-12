package dev.danvega.controller;

import dev.danvega.service.JsonReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("")
public class DataController {

    @Autowired
    JsonReaderService jsonReaderService;

    @GetMapping("/import")
    public List<Map<String, Object>> importJson( @RequestParam(value = "targetTable", defaultValue = "", required = false) String targetTable) throws IOException {
        return jsonReaderService.verifyTableColumnNameAndJsonColumnName(targetTable);
    }




}
