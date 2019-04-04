package com.github.exportexcel.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.exportexcel.constant.CommonConstant;
import com.github.exportexcel.service.ExportService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;


@Controller
public class ExportExcelsController {

    private static final Log logger = LogFactory.getLog(ExportExcelsController.class);

    @Autowired
    private ExportService exportService;

    @PostMapping("/excel")
    @ResponseBody
    public ResponseEntity<byte[]> exportExcelBySql(MultipartFile file) throws IOException {

        Calendar calendar = Calendar.getInstance();
        String fileName = UUID.randomUUID() + "-" + CommonConstant.DATE_TIME_FORMAT_WITHOUT_SPACE.format(calendar.getTime()) + ".xls";
        File excelFile = new File("export" + File.separator + fileName);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment", fileName);
        String sql = IOUtils.toString(file.getInputStream(), "UTF-8");
        logger.info("sql=" + sql);
        List<JSONObject> jsonObjectList = this.exportService.getListBySql(sql);
        Workbook workbook = new HSSFWorkbook();
        int sheetCount = 0;
        Sheet sheet = null;
        Row row = null;
        int sheetLength = 0;
        OutputStream outputStream = null;
        JSONObject jsonObject = null;

        try {
            outputStream = new FileOutputStream(excelFile);
            if (null != jsonObjectList && !jsonObjectList.isEmpty()) {
                sheetCount = jsonObjectList.size() % CommonConstant.XLS_MAX_LINE_NUMBER == 0 ?
                        jsonObjectList.size() / CommonConstant.XLS_MAX_LINE_NUMBER :
                        jsonObjectList.size() / CommonConstant.XLS_MAX_LINE_NUMBER + 1;

                jsonObject = jsonObjectList.get(0);

                for (int i = 0; i < sheetCount; i++) {
                    sheet = workbook.createSheet("sheet-" + i);
                    if (i == sheetCount - 1) {
                        sheetLength = jsonObjectList.size() - i * CommonConstant.XLS_MAX_LINE_NUMBER;
                    } else {
                        sheetLength = CommonConstant.XLS_MAX_LINE_NUMBER;
                    }

                    row = sheet.createRow(0);
                    for (int j = 1; j <= jsonObject.getIntValue("count"); j++) {
                        row.createCell(j - 1).setCellValue(jsonObject.getString(j + ""));
                    }

                    for (int k = 0; k < sheetLength; k++) {
                        jsonObject = jsonObjectList.get(i * CommonConstant.XLS_MAX_LINE_NUMBER + k);
                        row = sheet.createRow(k + 1);

                        for (int z = 1; z <= jsonObject.getIntValue("count"); z++) {
                            row.createCell(z - 1).setCellValue(jsonObject.getString(jsonObject.getString(z + "")));
                        }
                    }
                }
            }

            workbook.write(outputStream);

        } catch (Exception e) {
            logger.error(e);
        } finally {
            outputStream.close();
        }

        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(excelFile), httpHeaders, HttpStatus.CREATED);
    }

    @GetMapping("/index")
    public ModelAndView goExportExcel() {
        return new ModelAndView("export");
    }
}
