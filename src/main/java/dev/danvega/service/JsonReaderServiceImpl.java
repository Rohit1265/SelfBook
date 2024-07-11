package dev.danvega.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
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
    @Override
    public String verifyTableColumnNameAndJsonColumnName(String tableName) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Reading Json File
            InputStream inputStream = new ClassPathResource(departmentJson).getInputStream();
            JsonNode rootNode = objectMapper.readTree(inputStream);
            // Getting the fields from Json File
            JsonNode fieldsNode = rootNode.path("fields");
            // Creating a table table in the database.
            //And If there's a table already in the DB then there's no need for the below method "createTableFromJson".
            createTableFromJson(tableName, fieldsNode);
            // Getting the source column name from the source table in the database. (With MetaData)
            Map<String, String> tableColumns = getColumnNamesWithMetaData(tableName);
            // Verifing the Source columnName and JsonColumn name.
            for (JsonNode field : fieldsNode) {
                String columnName = field.get("column_name").asText();
                if (!tableColumns.containsKey(columnName)) {
                    System.out.println("Column does not match.");
                    return "Column does not match.";
                }
            }
            System.out.println("Columns Match.");
            return "Columns Match.";
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Columns Match.");
        return "Columns Match.";
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
        String createTableSql = generateCreateTableSql(tableName, fieldsNode);
        jdbcTemplate.execute(createTableSql);
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



}
