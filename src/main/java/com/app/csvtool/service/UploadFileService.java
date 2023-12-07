package com.app.csvtool.service;

import com.app.csvtool.dto.ProcessCsvDTO;
import com.app.csvtool.dto.UniqueHeadersDto;
import com.app.csvtool.dto.UploadCsvDto;
import org.springframework.core.io.FileSystemResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UploadFileService {
    List<String> uploadCsvFile(UploadCsvDto uploadCsvDto);

    FileSystemResource processCsvFile(ProcessCsvDTO processCsvDTO);

    Object getCsvData(ProcessCsvDTO processCsvDTO);

    Set<Map<String, ArrayList<String>>> getUniqueCsvData(UniqueHeadersDto uniqueHeadersDto);
}
