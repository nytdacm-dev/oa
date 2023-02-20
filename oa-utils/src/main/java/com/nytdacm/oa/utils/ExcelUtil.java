package com.nytdacm.oa.utils;

import com.nytdacm.oa.exception.OaBaseException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelUtil {
    public static final String TYPE =
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public static ByteArrayInputStream createExcel(String sheetName, String[] fields, List<String[]> data) {
        try (
            var workbook = new XSSFWorkbook();
            var out = new ByteArrayOutputStream()
        ) {
            var sheet = workbook.createSheet(sheetName);
            var rowId = 0;
            var row = sheet.createRow(rowId++);
            for (var i = 0; i < fields.length; i++) {
                var cell = row.createCell(i);
                cell.setCellValue(fields[i]);
            }
            for (var d : data) {
                row = sheet.createRow(rowId++);
                for (var i = 0; i < d.length; i++) {
                    var cell = row.createCell(i);
                    cell.setCellValue(d[i]);
                }
            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new OaBaseException("Excel 创建失败", 500);
        }
    }
}
