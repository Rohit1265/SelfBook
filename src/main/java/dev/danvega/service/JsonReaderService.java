package dev.danvega.service;

import dev.danvega.domain.DataMapping;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface JsonReaderService {

    public void createTableAndInsertData(String targetTableName) throws IOException;

    Iterable<DataMapping> getDataMapping();

}
