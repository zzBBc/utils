package com.github.zzbbc.utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtils {
    static Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);

    private static final String PROPERTY_STATUS = "status";
    private static final String PROPERTY_ERROR = "error";

    public static void add(JsonArray destination, JsonArray... other) {
        Objects.requireNonNull(destination);
        Objects.requireNonNull(other);

        for (JsonArray jsonArray : other) {
            for (JsonElement element : jsonArray) {
                destination.add(element);
            }
        }
    }

    public static Integer getInt(JsonObject jsonObject, String name) {
        JsonElement element = jsonObject.get(name);

        if (element != null && !element.isJsonNull()) {
            return element.getAsInt();
        }

        return null;
    }

    public static JsonObject getJsonObject(Map<String, String> map) {
        Objects.requireNonNull(map);

        JsonObject jsonObject = new JsonObject();
        map.forEach((k, v) -> {
            jsonObject.addProperty(k, v);
        });

        return jsonObject;
    }

    public static JsonObject getJsonObject(JsonObject jsonObject, String name) {
        JsonElement element = jsonObject.get(name);

        JsonObject value = new JsonObject();
        if (element != null && !element.isJsonNull()) {
            value = element.getAsJsonObject();
        }

        return value;
    }

    public static JsonObject getJsonObject(JsonArray jsonArray, int index) {
        JsonElement element = jsonArray.get(index);

        if (element != null && !element.isJsonNull()) {
            return element.getAsJsonObject();
        }

        return new JsonObject();
    }

    public static String getString(JsonArray array, int i) {
        JsonElement element = array.get(i);

        String value = "";
        if (element != null && !element.isJsonNull()) {
            value = element.getAsString();
        }

        return value.trim();
    }

    public static String getString(JsonObject jsonObject, String name) {
        JsonElement element = jsonObject.get(name);

        String value = "";
        if (element != null && !element.isJsonNull()) {
            value = element.getAsString();
        }

        return value.trim();
    }

    public static String getStringObject(JsonObject jsonObject, String name) {
        return getJsonObject(jsonObject, name).toString();
    }

    public static JsonArray toJsonArray(ResultSet resultSet) throws SQLException {
        JsonArray jsonArray = new JsonArray();


        if (resultSet == null) {
            return jsonArray;
        }

        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

        int columnCount = resultSetMetaData.getColumnCount();
        int[] columnTypes = new int[columnCount];
        String[] columnNames = new String[columnCount];

        for (int i = 0; i < columnCount; i++) {
            columnTypes[i] = resultSetMetaData.getColumnType(i + 1);
            columnNames[i] = resultSetMetaData.getColumnName(i + 1);
        }

        while (resultSet.next()) {
            JsonObject jsonObject = new JsonObject();

            for (int i = 0; i < columnCount; i++) {
                int columnIndex = i + 1;
                switch (columnTypes[i]) {
                    case Types.NULL:
                        jsonObject.addProperty(columnNames[i], "null");
                        break;
                    case Types.CHAR:
                    case Types.VARCHAR:
                    case Types.DATE:
                        String sTemp = resultSet.getString(columnIndex);
                        if (sTemp != null) {
                            sTemp = sTemp.trim();
                        } else {
                            sTemp = "";
                        }
                        jsonObject.addProperty(columnNames[i], sTemp);
                        break;
                    case Types.TINYINT:
                    case Types.INTEGER:
                    case Types.SMALLINT:
                    case Types.BIGINT:
                        jsonObject.addProperty(columnNames[i], resultSet.getLong(columnIndex));
                        break;
                    case Types.BOOLEAN:
                        jsonObject.addProperty(columnNames[i], resultSet.getBoolean(columnIndex));

                        break;
                    case Types.NUMERIC:
                    case Types.DOUBLE:
                        jsonObject.addProperty(columnNames[i], resultSet.getDouble(columnIndex));
                        break;
                    case Types.FLOAT:
                        jsonObject.addProperty(columnNames[i], resultSet.getFloat(columnIndex));
                        break;
                    case Types.NCLOB:
                        String clobString = JsonUtils.getClob(resultSet, columnIndex);

                        jsonObject.addProperty(columnNames[i], clobString);
                        break;
                    case Types.TIMESTAMP:
                        String dateTime = JsonUtils.getTimestamp(resultSet, columnIndex);

                        jsonObject.addProperty(columnNames[i], dateTime);
                        break;
                    case Types.BLOB:
                        // Không xử lý blob type column
                        // byte[] blobByte = JsonUtils.getBlob(resultSet, columnIndex);

                        // jsonObject.addProperty(columnNames[i], blobByte);
                        break;
                    default:
                        jsonObject.addProperty(columnNames[i], resultSet.getString(columnIndex));
                        break;
                }
            }
            jsonArray.add(jsonObject);
        }

        resultSet.close();

        return jsonArray;
    }


    private static JsonArray toJsonArray(List<String> values) {
        return toJsonArray(values.toArray(new String[0]));

    }

    public static JsonArray toJsonArray(String... values) {
        JsonArray array = new JsonArray(values.length);

        for (String value : values) {
            array.add(value);
        }

        return array;
    }

    private static String getTimestamp(ResultSet resultSet, int columnIndex) throws SQLException {
        LocalDateTime dateTime = resultSet.getObject(columnIndex, LocalDateTime.class);

        return TimeUtils.parse(dateTime);

    }


    private static String getClob(ResultSet resultSet, int columnIndex) throws SQLException {
        Clob clob;
        clob = resultSet.getClob(columnIndex);

        if (clob != null) {
            return clob.getSubString(1, (int) clob.length());
        }

        return "";
    }


    public static JsonObject toJsonObject(Map<String, String> map) {
        Objects.requireNonNull(map);

        JsonObject jsonObject = new JsonObject();
        map.forEach((k, v) -> {
            jsonObject.addProperty(k, v);
        });

        return jsonObject;
    }

    public static JsonObject toJsonObject(ResultSet resultSet) {
        JsonObject object = new JsonObject();

        return object;
    }


    public static JsonObject toJsonObject(HttpResponse response)
            throws ParseException, IOException {
        Objects.requireNonNull(response);

        HttpEntity entity = response.getEntity();

        String result;
        result = EntityUtils.toString(entity);

        String jsonString = StringEscapeUtils.unescapeJava(result);
        LOGGER.info("Parse response to Json: {}", jsonString);

        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        String status = JsonUtils.getString(jsonObject, PROPERTY_STATUS);
        if (StringUtils.isEmpty(status)) {
            Integer error = JsonUtils.getInt(jsonObject, PROPERTY_ERROR);

            if (error != null) {
                jsonObject.addProperty(PROPERTY_STATUS, error);
            } else {
                jsonObject.addProperty(PROPERTY_STATUS, response.getStatusLine().getStatusCode());
            }
        }

        return jsonObject;
    }

    public static JsonObject toJsonObject(Object baseObject, String... excludeFields) {
        JsonObject object = new JsonObject();

        List<String> excludeFieldsList = Arrays.asList(excludeFields);

        List<Field> nonStaticFields = CommonUtils.getNonStaticFields(baseObject);

        nonStaticFields.parallelStream().filter(f -> !excludeFieldsList.contains(f.getName()))
                .forEach(field -> {
                    String fieldName = field.getName();

                    field.setAccessible(true);

                    try {
                        Object value = field.get(baseObject);
                        if (value == null) {
                            return;
                        }

                        Class<?> type = field.getType();

                        if (type.equals(String[].class)) {
                            JsonArray array = JsonUtils.toJsonArray((String[]) value);

                            object.add(fieldName, array);
                        } else if (type.equals(List.class)) {
                            JsonArray array = JsonUtils.toJsonArray((List<String>) value);

                            object.add(fieldName, array);
                        } else if (type.equals(JsonArray.class)) {
                            JsonArray array = (JsonArray) value;

                            object.add(fieldName, array);
                        } else if (type.equals(JsonObject.class)) {
                            JsonObject jsonObject = (JsonObject) value;

                            object.add(fieldName, jsonObject);
                        } else if (type.equals(Long.class)) {
                            object.addProperty(fieldName, (Long) value);
                        } else if (type.equals(Integer.class)) {
                            object.addProperty(fieldName, (Integer) value);
                        } else {
                            object.addProperty(fieldName, value.toString());
                        }
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });

        return object;
    }

    public static JsonObject toJsonObject(String value) {
        JsonElement jsonElement = JsonParser.parseString(value);

        if (jsonElement.isJsonObject()) {
            return jsonElement.getAsJsonObject();
        }

        return null;
    }
}
