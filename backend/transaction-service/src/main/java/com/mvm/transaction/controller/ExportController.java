package com.mvm.transaction.controller;

import com.mvm.transaction.service.ExportService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/export")
public class ExportController {
    @Autowired
    private ExportService exportService;

    @GetMapping("/expenses/csv")
    public ResponseEntity<byte[]> exportExpensesToCsv(HttpServletRequest request){
        Long userId = (Long) request.getAttribute("userId"); //Get the userId
        byte[] csvData = exportService.exportExpensesToCsv(userId); //Call the service
        //Dynamic name, depends on the date
        String fileName = String.format("expenses_filtered_%s.csv", LocalDate.now());
        //Build our response
        return ResponseEntity.ok() // Status 200 OK
                //Header indicates that is an attachment named "expenses.csv"
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+fileName)
                //Header to indicates type, Spring needs the MediaTYpe and
                // the .parse read the string, creates an object with type (text), subtype (csv) and param (charset = utf-8)
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csvData);
    }

    @GetMapping("/expenses/filtered/csv")
    public ResponseEntity<byte[]> exportFilteredExpensesToCsv(
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");

        byte[] csvData = exportService.exportFilteredExpensesToCsv(
                userId, description, category, startDate, endDate, minAmount, maxAmount);

        String fileName = String.format("expenses_filtered_%s.csv", LocalDate.now());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csvData);
    }


}
