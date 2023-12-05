package com.kse.wmsv2.oc_cs_007.controller;

import com.kse.wmsv2.oc_cs_007.dto.OCCS007ResultDto;
import com.kse.wmsv2.oc_cs_007.service.OCCS007Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/api/occs007")
public class OCCS007Controller {

    @Autowired
    private OCCS007Service OCCS007Service;

    @PostMapping("/pdfUpload")
    public OCCS007ResultDto pdfUpload(@RequestHeader HttpHeaders headers, MultipartHttpServletRequest request) {
        OCCS007ResultDto resultDto = new OCCS007ResultDto();
        try {

            log.info("PDFアップロード処理を開始します。");
            boolean rst = OCCS007Service.pdfUpload(headers, request);

            if(rst){

                String message = "PDFアップロード処理が正常に終了しました。";
                log.info(message);
                resultDto.setResult(true);
                resultDto.setMessage(message);

            } else {

                String errMsg = "PDFアップロード処理が失敗しました。";
                log.warn(errMsg);
                resultDto.setResult(false);
                resultDto.setMessage(errMsg);

            }

        } catch(Exception e) {

            String errMsg = "PDFアップロード処理でエラーが発生しました。";
            log.error(errMsg + e.getMessage());
            resultDto.setResult(false);
            resultDto.setMessage(errMsg);
            resultDto.setErrorMessage(e.getMessage());

        } finally {

            return resultDto;

        }
    }

}


