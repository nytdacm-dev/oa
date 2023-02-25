package com.nytdacm.oa.utils

import com.nytdacm.oa.exception.OaBaseException
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

object ExcelUtil {
    const val TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"

    @JvmStatic
    fun createExcel(sheetName: String, fields: Array<String>, data: List<Array<String>>): ByteArrayInputStream {
        try {
            XSSFWorkbook().use { workbook ->
                ByteArrayOutputStream().use { out ->
                    val sheet = workbook.createSheet(sheetName)
                    var rowId = 0
                    var row = sheet.createRow(rowId++)
                    for (i in fields.indices) {
                        val cell = row.createCell(i)
                        cell.setCellValue(fields[i])
                    }
                    for (d in data) {
                        row = sheet.createRow(rowId++)
                        for (i in d.indices) {
                            val cell = row.createCell(i)
                            cell.setCellValue(d[i])
                        }
                    }
                    workbook.write(out)
                    return ByteArrayInputStream(out.toByteArray())
                }
            }
        } catch (e: IOException) {
            throw OaBaseException("Excel 创建失败", 500)
        }
    }
}
