package dev.danvega.service;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface JsonReaderService {
    List<Map<String, Object>> verifyTableColumnNameAndJsonColumnName(String targetTable) throws IOException;


}
