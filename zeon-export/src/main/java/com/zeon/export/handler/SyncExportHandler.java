package com.zeon.export.handler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson2.JSONArray;
import com.zeon.export.annotations.Export;
import com.zeon.export.entity.ExcelMetaInfo;
import com.zeon.export.utils.CellStyleUtils;
import com.zeon.export.utils.ResponseUtils;

import cn.idev.excel.FastExcel;
import cn.idev.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>
 * </p>
 *
 * @author lxy2914344878@163.com
 * @since 2025/9/3 10:45
 */
public class SyncExportHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncExportHandler.class);

    public static void handle(JSONArray result, Export export) {
        LOGGER.info("<========== Starting handle sync export, file type :{}", export.type().getValue());
        switch (export.type()) {
            case XLSX:
                handleXlsx(result, export);
                break;
            case CSV:
                handleCsv(result, export);
                break;
            default:
                break;
        }
    }

    public static void handleCsv(JSONArray result, Export export) {
        ExcelMetaInfo excelMetaInfo = ExcelMetaInfo.of(export);
        HttpServletResponse response = ResponseUtils.getHttpServletResponse();
        ResponseUtils.constructFileResponse(response, excelMetaInfo);
        try {
            FastExcel.write(response.getOutputStream()).autoCloseStream(true).csv().head(excelMetaInfo.getHeader())
                            .doWrite(ResponseUtils.extractData(result, excelMetaInfo));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ResponseUtils.constructFileResponse(response, excelMetaInfo);
    }

    public static void handleXlsx(JSONArray result, Export export) {
        ExcelMetaInfo excelMetaInfo = ExcelMetaInfo.of(export);
        HttpServletResponse response = ResponseUtils.getHttpServletResponse();
        ResponseUtils.constructFileResponse(response, excelMetaInfo);
        try {
            FastExcel.write(response.getOutputStream()).autoCloseStream(true)
                            .registerWriteHandler(CellStyleUtils.getNormalCellStyle())
                            .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                            .head(excelMetaInfo.getHeader()).sheet(excelMetaInfo.getFilename())
                            .doWrite(ResponseUtils.extractData(result, excelMetaInfo));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
