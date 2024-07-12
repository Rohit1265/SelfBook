package dev.danvega.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
@Service
public class JsonReaderServiceImpl implements JsonReaderService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    JsonReaderService jsonReaderService;
    @Value("${department.json}")
    String departmentJson;

    private final String SOURCE_TABLE= "department";
    @Override
    public List<Map<String, Object>>  verifyTableColumnNameAndJsonColumnName(String targetTable) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Reading Json File
            InputStream inputStream = new ClassPathResource(departmentJson).getInputStream();
            JsonNode rootNode = objectMapper.readTree(inputStream);
            // Getting the target table name from Json File
            String targetTableName= targetTable != null && !targetTable.isEmpty() ? targetTable : rootNode.get("target_table").asText();
            // Getting fields from Json File
            JsonNode fieldsNode = rootNode.path("fields");
            // Creating the table in the database.
            // If there's already exsisting in the DB then "createTableFromJson" and createTableFromJson
            // Create SOURCE Table
            createTableFromJson(SOURCE_TABLE, fieldsNode);
            // Data Insert SOURCE Table
            insertDataSourceTable(SOURCE_TABLE);
            // Creating the Target Table
            createTableFromJson(targetTableName, fieldsNode);
            // Getting the source column name from the source table in the database. (With MetaData)
            Map<String, String> tableColumns = getColumnNamesWithMetaData(targetTableName);
            // Verifing the sourceColumn Name and jsonColumn Name
            for (JsonNode field : fieldsNode) {
                String columnName = field.get("column_name").asText();
                if (!tableColumns.containsKey(columnName)) {
                    System.out.println("Column does not Match.");
                    return null;
                }
            }
            System.out.println("Columns Match.");
            // Insert Data SOURCE Table to Target Table
            String insertSql = generateInsertSql(SOURCE_TABLE, targetTableName, fieldsNode);
            System.out.println("insertSql-----"+insertSql);
            jdbcTemplate.execute(insertSql);

            // Getting Data from Target Table
            return getTargetTableData(targetTableName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Columns Match.");
        return null;
    }

    // Get the source column name With MetaData
    public Map<String, String> getColumnNamesWithMetaData(String tableName) throws SQLException {
        return jdbcTemplate.execute((ConnectionCallback<Map<String, String>>) (connection) -> {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getColumns(null, null, tableName.toUpperCase(), null);
            Map<String, String> columns = new HashMap<>();
            while (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                String dataType = resultSet.getString("TYPE_NAME");
                columns.put(columnName, dataType);
            }
            return columns;
        });
    }

    private String generateInsertSql(String sourceTable, String targetTable, JsonNode fieldsNode) {
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();

        for (JsonNode fieldNode : fieldsNode) {
            String columnName = fieldNode.get("column_name").asText();
            columns.append(columnName).append(", ");
            values.append("source.").append(columnName).append(", ");
        }

        // Remove the trailing comma and space
        columns.setLength(columns.length() - 2);
        values.setLength(values.length() - 2);

        return String.format("INSERT INTO %s (%s) SELECT %s FROM %s source", targetTable, columns.toString(), values.toString(), sourceTable);
    }

    // Get the source column name Without MetaData
    public Map<String, String> getColumnNameWithoutMetaData(String tableName) {
        String sql = "SELECT COLUMN_NAME, TYPE_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ?";
        return jdbcTemplate.query(sql, new Object[]{tableName.toUpperCase()}, rs -> {
            Map<String, String> columns = new HashMap<>();
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                String dataType = rs.getString("TYPE_NAME");
                columns.put(columnName, dataType);
            }
            return columns;
        });
    }

    public void createTableFromJson(String tableName, JsonNode fieldsNode) {
        try {
        String createTableSql = generateCreateTableSql(tableName, fieldsNode);
        jdbcTemplate.execute(createTableSql);
        } catch (Exception e) {
            System.out.println("Table "+tableName+" already exists, skipping table creation.");
        }
    }

    public void insertDataSourceTable(String tableName) {
        String insertSQL = "Insert into " + tableName + " (ID, NAME, NUMBER) values ('1','Bridging','R0001')";
        jdbcTemplate.execute(insertSQL);
    }

    private String generateCreateTableSql(String tableName, JsonNode fieldsNode) {
        StringBuilder sql = new StringBuilder("CREATE TABLE ").append(tableName).append(" (");
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

    private List<Map<String, Object>> getTargetTableData(String tableName){
        String sql = "select * from "+tableName;
        return jdbcTemplate.queryForList(sql);
    }

}
