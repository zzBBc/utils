package com.github.zzbbc.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;

public class ExcelUtils {
    public static String getString(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case BLANK:
                break;
            case BOOLEAN:
                break;
            case ERROR:
                break;
            case FORMULA:
                break;
            case NUMERIC:
                DataFormatter dataFormatter = new DataFormatter();
                return dataFormatter.formatCellValue(cell);
            case STRING:
                return cell.getStringCellValue();
            case _NONE:
                break;
            default:
                break;

        }

        return null;
    }
}
