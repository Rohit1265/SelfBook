package dev.danvega.controller;

import dev.danvega.domain.DataMapping;
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

    @GetMapping("/import/{targetTableName}")
    public Iterable<DataMapping> importJson(@PathVariable String targetTableName) {
        try {
            jsonReaderService.createTableAndInsertData(targetTableName);
           // return "Data imported successfully";
            return jsonReaderService.getDataMapping();
        } catch (IOException e) {
            //return "Error importing data: " + e.getMessage();
            return null;
        }
    }

    @GetMapping("/list")
    public Iterable<DataMapping> getDataMapping() {
        return jsonReaderService.getDataMapping();
    }



//Left: Save source data to target datatable
    //Dynamic mapping
}
