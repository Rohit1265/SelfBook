package dev.danvega.service;

import com.fasterxml.jackson.databind.JsonNode;
import dev.danvega.domain.DataMapping;
import dev.danvega.domain.Department;
import dev.danvega.domain.Employee;
import dev.danvega.domain.TargetTableName;
import dev.danvega.repository.DataMappingRepository;
import dev.danvega.repository.DepartmentRepository;
import dev.danvega.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Field;
import java.util.*;

import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;

import java.io.File;

@Service
public class JsonReaderServiceImpl implements JsonReaderService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JsonReaderService jsonReaderService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DataMappingRepository dataMappingRepository;

    @Value("${department.json}")
    private String departmentJson;

    @Value("${employee.json}")
    private String employeeJson;

    @Override
    public void createTableFromJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            InputStream inputStream = new ClassPathResource(departmentJson).getInputStream();
            JsonNode rootNode = objectMapper.readTree(inputStream);
            JsonNode fieldsNode = rootNode.path("fields");
            String createTableSql = generateCreateTableSql(fieldsNode);
            jdbcTemplate.execute(createTableSql);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateCreateTableSql(JsonNode fieldsNode) {
        StringBuilder sql = new StringBuilder("CREATE TABLE DEPARTMENT_TD (");
        for (int i = 0; i < fieldsNode.size(); i++) {
            JsonNode fieldNode = fieldsNode.get(i);
            String columnName = fieldNode.path("column_name").asText();
            String dataType = fieldNode.path("data_type").asText();
            String dataLength = fieldNode.path("data_length").asText();

            sql.append(columnName).append(" ").append(dataType);
            if (!dataLength.equals("0")) {
                sql.append("(").append(dataLength).append(")");
            }
            if (i < fieldsNode.size() - 1) {
                sql.append(", ");
            }
        }
        sql.append(")");
        return sql.toString();
    }



    @Override
    public void createTableAndInsertData() throws IOException {
        List<Department> departments = saveDataDepartment();
        createTableAndInsertDataSetup(TargetTableName.DEPARTMENT_TARGET.toString(),departmentJson,Department.class, departments);
        List<Employee> employees = saveDataEmployee();
        createTableAndInsertDataSetup(TargetTableName.EMPLOYEE_TARGET.toString(),employeeJson,Employee.class, employees);
    }




    public void createTableAndInsertDataSetup(String targetTableName, String jsonFilePath, Class<?> clazz,  Iterable<?> object) throws IOException {
        JsonNode jsonNode = readJsonFile(jsonFilePath);
        if (jsonNode.isArray() && jsonNode.elements().hasNext()) {
            JsonNode firstElement = jsonNode.elements().next();
            createTable(targetTableName, firstElement, clazz);
            insertDataMapping(targetTableName, jsonNode, clazz);
            insertData(targetTableName, jsonNode, clazz,object);
        }
    }

    private JsonNode readJsonFile(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource(filePath);
        InputStream inputStream = resource.getInputStream();
        return objectMapper.readTree(inputStream);
    }

    private void createTable(String targetTableName, JsonNode firstElement, Class<?> clazz) {
        StringBuilder createTableQuery = new StringBuilder("CREATE TABLE ").append(targetTableName).append(" (");

        List<Map.Entry<String, JsonNode>> fieldList = new ArrayList<>();
        firstElement.fields().forEachRemaining(fieldList::add);

        if (fieldList.size() == clazz.getDeclaredFields().length) {
            System.out.println("Field size matches " + clazz.getSimpleName() + " class fields.");
        } else {
            System.out.println("Field size does not match " + clazz.getSimpleName() + " class fields.");
        }

        for (Map.Entry<String, JsonNode> field : fieldList) {
            boolean verifyColumnName = isFieldExists(clazz, field.getKey());
            System.out.println(verifyColumnName ? "match" : "not match");

            createTableQuery.append(field.getValue().asText())
                    .append(" ")
                    .append(getColumnType(field.getValue()))
                    .append(", ");
        }

        createTableQuery.delete(createTableQuery.length() - 2, createTableQuery.length()).append(")");
        System.out.println("create query: " + createTableQuery);
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
    private void insertDataMapping(String tableName, JsonNode jsonNode, Class<?> clazz) {
        for (JsonNode element : jsonNode) {
            Iterator<Map.Entry<String, JsonNode>> fields = element.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                saveDataMapping(clazz.getSimpleName(), field.getKey(), tableName, field.getValue().asText());
            }
        }
    }

    private void saveDataMapping(String sourceTableName, String sourceColumnName, String targetTableName, String targetColumnName) {
        DataMapping dataMapping = new DataMapping();
        dataMapping.setSourceTableName(sourceTableName);
        dataMapping.setSourceColumnName(sourceColumnName);
        dataMapping.setTargetTableName(targetTableName);
        dataMapping.setTargetColumnName(targetColumnName);
        dataMappingRepository.save(dataMapping);
    }

    private boolean isFieldExists(Class<?> clazz, String columnName) {
        for (Field field : clazz.getDeclaredFields()) {
            if (columnName.equals(field.getName())) {
                return true;
            }
        }
        return false;
    }

    private void insertData(String tableName, JsonNode jsonNode, Class<?> clazz, Iterable<?> object) {
        for (Object entity : object) {
            for (JsonNode element : jsonNode) {
                StringBuilder insertQuery = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
                StringBuilder columns = new StringBuilder();
                StringBuilder values = new StringBuilder("VALUES (");

                Iterator<Map.Entry<String, JsonNode>> fields = element.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> field = fields.next();
                    Field[] entityFields = clazz.getDeclaredFields();

                    for (Field entityField : entityFields) {
                        if (field.getKey().equals(entityField.getName())) {
                            columns.append(field.getValue().asText()).append(", ");
                            try {
                                entityField.setAccessible(true);
                                values.append("'").append(entityField.get(entity)).append("', ");
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                columns.delete(columns.length() - 2, columns.length());
                values.delete(values.length() - 2, values.length()).append(")");

                insertQuery.append(columns).append(") ").append(values);

                System.out.println("insertQuery: " + insertQuery);
                jdbcTemplate.execute(insertQuery.toString());
            }
        }
    }

    private  List<Department>  saveDataDepartment() {
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

       return (List<Department>) departmentRepository.saveAll(departments);
    }

    private List<Employee> saveDataEmployee() {
        List<Employee> employees = new ArrayList<>();
        Employee employee = new Employee();
        employee.setName("Employee 1");
        employee.setNumber("EMP001");
        employees.add(employee);

        Employee employee1 = new Employee();
        employee1.setName("Employee 2");
        employee1.setNumber("EMP002");
        employees.add(employee1);

        return (List<Employee>) employeeRepository.saveAll(employees);
    }

    public Iterable<DataMapping> getDataMapping(){
        return dataMappingRepository.findAll();
    }

    public  List<Map<String, Object>>  getDepartmentTargetData(){
        String sql = "SELECT * FROM "+TargetTableName.DEPARTMENT_TARGET;
        return jdbcTemplate.queryForList(sql);
    }

    public  List<Map<String, Object>>  getEmployeeTargetData(){
        String sql = "SELECT * FROM "+TargetTableName.EMPLOYEE_TARGET;
        return jdbcTemplate.queryForList(sql);
    }




}
