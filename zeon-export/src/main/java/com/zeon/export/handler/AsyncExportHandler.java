package com.zeon.export.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.zeon.export.annotations.Export;
import com.zeon.export.entity.ExcelMetaInfo;
import com.zeon.export.utils.CellStyleUtils;
import com.zeon.export.utils.ResponseUtils;

import cn.idev.excel.FastExcel;
import cn.idev.excel.util.FileUtils;
import cn.idev.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>
 * </p>
 *
 * @author lxy2914344878@163.com
 * @since 2025/9/9 19:51
 */
public class AsyncExportHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncExportHandler.class);

    /**
     * write async response to client
     * 
     * @return taskId
     */
    public static String writeAsyncResponse() {
        HttpServletResponse response = ResponseUtils.getHttpServletResponse();
        response.setStatus(200);
        String taskId = UUID.randomUUID().toString();
        ServletOutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            Map<String, Object> result = new HashMap<>();
            result.put("message", "Async export starting, waiting notification");
            result.put("taskId", taskId);
            result.put("code", 200);
            JSON.toJSONString(result);
            outputStream.print(JSON.toJSONString(result));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return taskId;
    }

    public static void handle(JSONArray result, Export export, String taskId) {
        LOGGER.info("<========== Starting handle async export, file type :{}", export.type().getValue());
        ExcelMetaInfo metaInfo = ExcelMetaInfo.of(export);
        List<List<String>> results = ResponseUtils.extractData(result, metaInfo);

        File file = FileUtils.createTmpFile(
                        String.format("%s-%s.%s", metaInfo.getFilename(), taskId, export.type().getValue()));
        try (FileOutputStream fos = new FileOutputStream(file)) {
            switch (export.type()) {
                case XLSX -> {
                    FastExcel.write(fos).registerWriteHandler(CellStyleUtils.getNormalCellStyle())
                                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                                    .head(metaInfo.getHeader()).sheet(metaInfo.getFilename())
                                    .doWrite(ResponseUtils.extractData(result, metaInfo));
                }
                case CSV -> {
                    FastExcel.write(fos).csv().head(metaInfo.getHeader()).doWrite(results);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            LOGGER.info("Async export task finish!, result file path: {}", file.getAbsolutePath());
        }
    }

}
