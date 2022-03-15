package com.github.zzbbc.utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleDriver;

public class CommonUtils {
    public static Path ensureDirExists(String directory) {
        Path path = Paths.get(directory);
        Path dir = path.toAbsolutePath();

        if (!Files.exists(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
}
