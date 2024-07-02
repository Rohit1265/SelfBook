package dev.danvega.service;

import dev.danvega.domain.DataMapping;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface JsonReaderService {

    public void createTableAndInsertData() throws IOException;

    Iterable<DataMapping> getDataMapping();

    List<Map<String, Object>> getDepartmentTargetData();

    List<Map<String, Object>> getEmployeeTargetData();

}
