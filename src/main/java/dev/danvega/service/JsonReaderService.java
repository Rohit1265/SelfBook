package dev.danvega.service;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public interface JsonReaderService {
    String verifyTableColumnNameAndJsonColumnName(String tableName) throws IOException;


}
