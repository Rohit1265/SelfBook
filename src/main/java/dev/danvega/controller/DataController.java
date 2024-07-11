package dev.danvega.controller;

import dev.danvega.service.JsonReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
@RestController
@RequestMapping("")
public class DataController {

    @Autowired
    JsonReaderService jsonReaderService;

    @GetMapping("/import/{tableName}")
    public String importJson(@PathVariable(value = "tableName") String tableName) throws IOException {
        return jsonReaderService.verifyTableColumnNameAndJsonColumnName(tableName);
    }




}
