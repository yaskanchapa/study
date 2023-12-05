package com.kse.wmsv2.common.controller;

import com.kse.wmsv2.common.error.Error;
import com.kse.wmsv2.common.exception.exceptions.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ErrorSampleController {

    // エラー強制発生
    @GetMapping("/errortest")
    public ResponseEntity<?> testException () {
        throw new BadRequestException( new Error("E001", "error test"));
    }
}
