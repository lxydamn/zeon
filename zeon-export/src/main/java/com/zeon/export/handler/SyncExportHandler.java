package com.zeon.export.handler;

import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
        Collection<?> collection = getCollectionFromResult(result);
        if (CollectionUtils.isEmpty(collection)) {
            return;
        }
        HttpServletResponse response = getHttpServletResponse();
        ResponseUtils.constructFileResponse(response, excelMetaInfo);
        try {
            FastExcel.write(response.getOutputStream()).autoCloseStream(true).csv().head(excelMetaInfo.getHeader())
                            .doWrite(ResponseUtils.extractData(collection, excelMetaInfo));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ResponseUtils.constructFileResponse(response, excelMetaInfo);
    }

    public static void handleXlsx(JSONArray result, Export export) {
        ExcelMetaInfo excelMetaInfo = ExcelMetaInfo.of(export);
        HttpServletResponse response = getHttpServletResponse();
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

    private static Collection<?> getCollectionFromResult(Object result) {
        if (result instanceof ResponseEntity<?> response) {
            result = response.getBody();
        }
        if (!(result instanceof Collection<?> collection)) {
            throw new RuntimeException("Results is not support to transfer xlsx");
        }
        return collection;
    }

    private static HttpServletResponse getHttpServletResponse() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new RuntimeException("ServletRequestAttributes is null");
        }
        HttpServletResponse response = attributes.getResponse();
        if (response == null) {
            throw new RuntimeException("HttpServletResponse is null");
        }
        return response;
    }
}
