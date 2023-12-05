package com.app.csvtool.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
public class ProcessCsvDTO {
    private String fileName;
    private String jsonFileName;
    private List<String> csvHeaders;
    private Map<String,String> jsonKeysMap;
    private List<CsvStaticFieldsDto> staticFields;
}
