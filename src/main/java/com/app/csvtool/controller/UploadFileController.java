package com.app.csvtool.controller;

import com.app.csvtool.dto.ProcessCsvDTO;
import com.app.csvtool.dto.UploadCsvDto;
import com.app.csvtool.service.UploadFileService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class UploadFileController {
    private final UploadFileService uploadFileService;


    @PostMapping("process/csv")
    public ResponseEntity<FileSystemResource> handleFileUpload(@RequestBody ProcessCsvDTO processCsvDTO) {
        try {
            // Process the CSV file and extract required columns
            FileSystemResource resource = uploadFileService.processCsvFile(processCsvDTO);

            // Set up response headers
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+processCsvDTO.getJsonFileName());
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            // Return the file as a downloadable response
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/upload/csv")
    public ResponseEntity<List<String>> uploadCsvFile(@ModelAttribute UploadCsvDto uploadCsvDto) {
        return ResponseEntity.ok(uploadFileService.uploadCsvFile(uploadCsvDto));
    }

    @GetMapping("/getdata")
    public ResponseEntity<String> getUserData() {
        return ResponseEntity.ok("You are great");
    }
}
