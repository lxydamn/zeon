package com.zeon.export.handler;

import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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

    public static void handle(Object result, Export export) {
        LOGGER.info("<========== Starting handle sync export, file type :{}", export.type().getValue());
        switch (export.type()) {
            case XLSX:
                handleXlsx(result, export);
                break;
            default:
                break;
        }

    }

    public static void handleXlsx(Object result, Export export) {
        ExcelMetaInfo excelMetaInfo = ExcelMetaInfo.of(export);
        if (result instanceof ResponseEntity<?> response) {
            result = response.getBody();
        }
        if (!(result instanceof Collection<?> collection)) {
            throw new RuntimeException("Results is not support to transfer xlsx");
        }
        if (CollectionUtils.isEmpty(collection)) {
            return;
        }
        ServletRequestAttributes attributes =
                        (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletResponse response = attributes.getResponse();
        if (response == null) {
            throw new RuntimeException("HttpServletResponse is null");
        }
        ResponseUtils.constructFileResponse(response, excelMetaInfo);
        try {
            FastExcel.write(response.getOutputStream()).autoCloseStream(true)
                            .registerWriteHandler(CellStyleUtils.getNormalCellStyle())
                            .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                            .head(excelMetaInfo.getHeader()).sheet(excelMetaInfo.getFilename())
                            .doWrite(ResponseUtils.extractData(collection, excelMetaInfo));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
