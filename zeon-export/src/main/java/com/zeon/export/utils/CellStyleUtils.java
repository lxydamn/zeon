package com.zeon.export.utils;

import org.apache.poi.ss.usermodel.*;

import cn.idev.excel.write.metadata.style.WriteCellStyle;
import cn.idev.excel.write.metadata.style.WriteFont;
import cn.idev.excel.write.style.HorizontalCellStyleStrategy;

/**
 * <p>
 * </p>
 *
 * @author lxy2914344878@163.com
 * @since 2025/9/3 20:31
 */
public class CellStyleUtils {

    public static HorizontalCellStyleStrategy getNormalCellStyle() {
        // Header
        WriteCellStyle headerWriteCellStyle = new WriteCellStyle();
        headerWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        headerWriteCellStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
        headerWriteCellStyle.setFillBackgroundColor(IndexedColors.WHITE1.index);
        setCellStyle(headerWriteCellStyle);
        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        // 这里需要指定FillPatternType 为FillPatternType.SOLID_FOREGROUND不然无法显示背景
        contentWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        contentWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        setCellStyle(contentWriteCellStyle);
        return new HorizontalCellStyleStrategy(headerWriteCellStyle, contentWriteCellStyle);
    }

    private static void setCellStyle(WriteCellStyle writeCellStyle) {
        writeCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        writeCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 边框
        writeCellStyle.setBorderBottom(BorderStyle.THIN);
        writeCellStyle.setBorderLeft(BorderStyle.THIN);
        writeCellStyle.setBorderRight(BorderStyle.THIN);
        writeCellStyle.setBorderTop(BorderStyle.THIN);
        WriteFont writeFont = new WriteFont();
        writeFont.setFontHeightInPoints((short) 12);
        writeFont.setFontName("宋体");
        writeCellStyle.setWriteFont(writeFont);
        writeCellStyle.setWrapped(true);
    }
}
