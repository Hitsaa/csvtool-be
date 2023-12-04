package com.app.csvtool.service;

import com.app.csvtool.dto.ProcessCsvDTO;
import com.app.csvtool.dto.UploadCsvDto;
import org.springframework.core.io.FileSystemResource;

import java.util.List;

public interface UploadFileService {
    List<String> uploadCsvFile(UploadCsvDto uploadCsvDto);

    FileSystemResource processCsvFile(ProcessCsvDTO processCsvDTO);
}
