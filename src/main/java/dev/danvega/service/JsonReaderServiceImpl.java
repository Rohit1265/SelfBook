package dev.danvega.service;

import com.fasterxml.jackson.databind.JsonNode;
import dev.danvega.domain.DataMapping;
import dev.danvega.domain.Department;
import dev.danvega.repository.DataMappingRepository;
import dev.danvega.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;

@Service("JsonReaderService")
public class JsonReaderServiceImpl implements JsonReaderService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JsonReaderService jsonReaderService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DataMappingRepository dataMappingRepository;

    @Value("${department.json}")
    private String departmentJson;

    public JsonNode readJsonFile() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource(departmentJson);
        InputStream inputStream = resource.getInputStream();
        return objectMapper.readTree(inputStream);
    }

    @Override
    public void createTableAndInsertData(String targetTableName) throws IOException {
        saveDepartmentData();
        JsonNode jsonNode = readJsonFile();
        if (jsonNode.isArray() && jsonNode.elements().hasNext()) {
            JsonNode firstElement = jsonNode.elements().next();
            createTable(targetTableName, firstElement);
            insertDataMapping(targetTableName, jsonNode);
        }
    }

    private void createTable(String targetTableName, JsonNode firstElement) {
        StringBuilder createTableQuery = new StringBuilder("CREATE TABLE ").append(targetTableName).append(" (");

        List<Map.Entry<String, JsonNode>> fieldList = new ArrayList<>();
        firstElement.fields().forEachRemaining(fieldList::add);

        if (fieldList.size() == Department.class.getDeclaredFields().length) {
            System.out.println("Field size matches Department class fields.");
        } else {
            System.out.println("Field size does not match Department class fields.");
        }

        for (Map.Entry<String, JsonNode> field : fieldList) {
            boolean verifyColumnName = ifFieldExists(Department.class, field.getKey());
            System.out.println(verifyColumnName ? "match" : "not match");

            createTableQuery.append(field.getValue().asText())
                    .append(" ")
                    .append(getColumnType(field.getValue()))
                    .append(", ");
        }

        createTableQuery.delete(createTableQuery.length() - 2, createTableQuery.length()).append(")");
        System.out.println("query: " + createTableQuery);
        jdbcTemplate.execute(createTableQuery.toString());
    }

    private String getColumnType(JsonNode value) {
        if (value.isInt()) {
            return "INTEGER";
        } else if (value.isLong()) {
            return "BIGINT";
        } else if (value.isDouble()) {
            return "DOUBLE";
        } else if (value.isBoolean()) {
            return "BOOLEAN";
        } else {
            return "VARCHAR(255)";
        }
    }

    private void insertDataMapping(String tableName, JsonNode jsonNode) {
        for (JsonNode element : jsonNode) {
            Iterator<Map.Entry<String, JsonNode>> fields = element.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                saveDataMapping(Department.class.getSimpleName(),field.getKey(),tableName,field.getValue().asText());
            }
        }
    }
    private boolean ifFieldExists(Class<?> clazz, String columnName) {
        for (Field field : clazz.getDeclaredFields()) {
            if (columnName.equals(field.getName())) {
                return true;
            }
        }
        return false;
    }

    private void saveDataMapping(String sourceTableName,String sourceColumnName,String targetTableName,String targetColumnName){
        DataMapping dataMapping = new DataMapping();
        dataMapping.setSourceTableName(sourceTableName);
        dataMapping.setSourceColumnName(sourceColumnName);
        dataMapping.setTargetTableName(targetTableName);
        dataMapping.setTargetColumnName(targetColumnName);
        dataMappingRepository.save(dataMapping);
    }

 //Source table data
    private void saveDepartmentData(){

        List<Department> departments = new ArrayList<>();
        Department department = new Department();

        department.setName("Vishal");
        department.setNumber("123");
        department.setErrorMessage("error 1");

        departments.add(department);

        Department department1 = new Department();

        department1.setName("Deep");
        department1.setNumber("456");
        department1.setErrorMessage("error 2");


        departments.add(department1);

        departmentRepository.saveAll(departments);
    }
    public Iterable<DataMapping> getDataMapping(){
        return dataMappingRepository.findAll();
    }


}
