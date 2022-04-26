package com.github.zzbbc.utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleDriver;

public class CommonUtils {
    public static void ensureDirExists(String string) throws IOException {
        Path path = Paths.get(string);

        ensureDirExists(path);
    }

    public static Path ensureDirExists(Path path) throws IOException {
        Path dir = path.toAbsolutePath();

        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        return dir;
    }

    public static OracleConnection connect(String sURL, String sUserName, String sPassword)
            throws SQLException {
        OracleDriver dr = new OracleDriver();
        Properties prop = new Properties();
        prop.setProperty("user", sUserName);
        prop.setProperty("password", sPassword);

        return (OracleConnection) dr.connect(sURL, prop);
    }

    public static List<Field> getNonStaticFields(Object baseObject) {
        return Arrays.stream(baseObject.getClass().getDeclaredFields()).parallel()
                .filter(f -> !Modifier.isStatic(f.getModifiers())).collect(Collectors.toList());
    }

    public static void close(Statement stmt) throws SQLException {
        if (stmt != null) {
            stmt.close();
        }
    }

    public static void close(ResultSet resultSet) throws SQLException {
        if (resultSet != null) {
            resultSet.close();
        }
    }

    public static void close(Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
