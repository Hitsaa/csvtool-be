package com.app.csvtool.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
public class UploadCsvDto {
    private MultipartFile file;
}
