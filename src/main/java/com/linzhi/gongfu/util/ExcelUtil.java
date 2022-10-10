package com.linzhi.gongfu.util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public abstract class ExcelUtil {
    /**
     * 将Excel内容转换list
     *
     * @param file 文件
     * @return 返回list
     */
    public static List<Map<String, Object>> excelToList(MultipartFile file) throws Exception {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        //行数
        int num = sheet.getLastRowNum();
        //列数
        int col = sheet.getRow(0).getLastCellNum();
        List<Map<String, Object>> list = new ArrayList<>();
        String[] colName = new String[col];
        //获取列名
        Row row = sheet.getRow(0);
        for (int i = 0; i < col; i++) {
            String[] s = row.getCell(i).getStringCellValue().split("-");
            colName[i] = s[0];
        }

        //将一行中每列数据放入一个map中,然后把map放入list
        for (int i = 1; i <= num; i++) {
            Map<String, Object> map = new HashMap<>();
            Row row1 = sheet.getRow(i);
            if (row1 != null) {
                for (int j = 0; j < col; j++) {
                    Cell cell = row1.getCell(j);
                    if (cell != null) {
                        CellType cellType = cell.getCellType();
                        if (cellType == CellType.NUMERIC) {
                            double numericCellValue = cell.getNumericCellValue();//获取数字类型的单元格中的数据NUMERIC
                            //stripTrailingZeros()：去除末尾多余的0，toPlainString()：输出时不用科学计数法
                            String s = new BigDecimal(String.valueOf(numericCellValue)).stripTrailingZeros().toPlainString();
                            map.put(colName[j], s);
                        } else if (cellType == CellType.STRING) {
                            map.put(colName[j], cell.getStringCellValue());
                        }
                    }
                }
            }
            list.add(map);
        }
        return list;
    }

    /**
     * 导出集合到excel
     *
     * @param name 文件名称
     * @param list 数据列表
     */
    public static void exportToExcel(HttpServletResponse response, String name, List<LinkedHashMap<String, Object>> list) {
        try {
            //文件名称
            String fileName = name + ".xls";
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
            HSSFSheet hssfSheet = hssfWorkbook.createSheet(name);

            //hssfWorkbook.createCellStyle();
            CellStyle cellStyle = hssfWorkbook.createCellStyle();//创建单元格样
            cellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex());
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            int rowNum = 0;
            //新建行
            HSSFRow hssfRow = hssfSheet.createRow(rowNum++);
            //列
            int j = 0;
            if (list.size() > 0) {
                for (String i : list.get(0).keySet()) {
                    //新建第一行
                    HSSFCell cell = hssfRow.createCell(j++);
                    cell.setCellValue(i);
                    cell.setCellStyle(cellStyle);
                    hssfSheet.autoSizeColumn(j);
                    hssfSheet.setColumnWidth(j, hssfSheet.getColumnWidth(j) * 17 / 10);
                }
                //将数据放入表中
                for (int i = 0; i < list.size(); i++) {
                    //新建一行
                    HSSFRow row = hssfSheet.createRow(rowNum++);

                    Map<String, Object> map = list.get(i);
                    j = 0;
                    for (Object obj : map.values()) {
                        if (obj != null) {
                            row.createCell(j++).setCellValue(obj.toString());
                        } else {
                            row.createCell(j++);
                        }
                    }
                }
            }
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/vnd.ms-excel");
            // 下载文件的默认名称
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
            hssfWorkbook.write(response.getOutputStream());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 解析Excel日期格式
     * @param strDate 日期
     * @return 字符串
     */
    public static String ExcelDoubleToDate(String strDate) {
        if (strDate.length() == 5) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date tDate = DoubleToDate(Double.parseDouble(strDate));
                return sdf.format(tDate);
            } catch (Exception e) {
                e.printStackTrace();
                return strDate;
            }
        }
        return strDate;
    }

    /**
     * 解析Excel日期格式
     *
     * @param dVal 日期
     * @return 日期
     */
    public static Date DoubleToDate(Double dVal) {
        Date tDate = new Date();
        //系统时区偏移 1900/1/1 到 1970/1/1 的 25569 天
        long localOffset = tDate.getTimezoneOffset() * 60000;
        tDate.setTime((long) ((dVal - 25569) * 24 * 3600 * 1000 + localOffset));
        return tDate;
    }
}
