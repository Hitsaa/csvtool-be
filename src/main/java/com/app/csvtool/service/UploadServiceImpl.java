package com.app.csvtool.service;

import com.app.csvtool.dto.CsvStaticFieldsDto;
import com.app.csvtool.dto.ProcessCsvDTO;
import com.app.csvtool.dto.UniqueHeadersDto;
import com.app.csvtool.dto.UploadCsvDto;
import com.app.csvtool.enums.DirectionType;
import com.app.csvtool.exception.AppException;
import com.app.csvtool.service.storage.StorageService;
import com.app.csvtool.utils.StringUtility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

@AllArgsConstructor
@Service
public class UploadServiceImpl implements UploadFileService {
        private final StorageService storageService;

    public List<Map<String,Object>> getDataFromCsv(Resource file, List<CsvStaticFieldsDto> staticFields, Map<String, String> headersMap) {
        try {
            Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withDelimiter(';').withHeader());
            Map<String, String> region2Region1Map = new HashMap<>();
            List<String> headers = getCsvHeaders(csvParser);
            Map<String, String> csvHeaders = getNormalizedCsvHeaders(headers);
            String countryHeader = csvHeaders.get("country");
            String languageHeader = csvHeaders.get("language");
            String isoHeader = csvHeaders.get("iso");
            String postcodeHeader = csvHeaders.get("postcode");
            String localityHeader = csvHeaders.get("locality");
            String region1Header = csvHeaders.get("region1");
            String region2Header = csvHeaders.get("region2");
            String region3Header = csvHeaders.get("region3");
            String region4Header = csvHeaders.get("region4");
            Map<String, Integer> staticFieldValues = new HashMap<>();
            List<Map<String, Object>> jsonList = new ArrayList<Map<String,Object>>();

            for (CSVRecord csvRecord : csvParser) {
                String region1Name = csvRecord.get(region1Header);
                String region2Name = csvRecord.get(region2Header);
                if(region2Region1Map.get(region2Name) == null || !region2Region1Map.get(region2Name).equals(region1Name)) {
                    Map<String, Object> countryZipcodes = new HashMap<>();
                    if (StringUtils.isNotBlank(region1Header) && StringUtils.isNotBlank(headersMap.get(region1Header))) {
                        countryZipcodes.put(headersMap.get(region1Header), region1Name);
                    }

                    if (StringUtils.isNotBlank(region2Header) && StringUtils.isNotBlank(headersMap.get(region2Header))) {
                        if (StringUtils.isBlank(region2Region1Map.get(region2Name))) {
                            region2Region1Map.put(region2Name, region1Name);
                        } else if (StringUtils.isNotBlank(region2Region1Map.get(region2Name)) && region2Region1Map.get(region2Name).equals(region1Name)) {
                            region2Name = region2Name + "_" + region1Name;
                        }
                        countryZipcodes.put(headersMap.get(region2Header), region2Name);
                    }

                    if (StringUtils.isNotBlank(countryHeader) && StringUtils.isNotBlank(headersMap.get(countryHeader))) {
                        // we get iso because in json we set iso code for country
                        String iso = csvRecord.get(isoHeader);
                        countryZipcodes.put(headersMap.get(countryHeader), iso);
                    }

                    if (StringUtils.isNotBlank(region3Header) && StringUtils.isNotBlank(headersMap.get(region3Header))) {
                        String region3Name = csvRecord.get(region3Header);
                        countryZipcodes.put(headersMap.get(region3Header), region3Name);
                    }

                    if (StringUtils.isNotBlank(region4Header) && StringUtils.isNotBlank(headersMap.get(region4Header))) {
                        String region4Name = csvRecord.get(region4Header);
                        countryZipcodes.put(headersMap.get(region4Header), region4Name);
                    }

                    if (StringUtils.isNotBlank(postcodeHeader) && StringUtils.isNotBlank(headersMap.get(postcodeHeader))) {
                        String postcode = csvRecord.get(postcodeHeader);
                        countryZipcodes.put(headersMap.get(postcodeHeader), postcode);
                    }

                    if (StringUtils.isNotBlank(localityHeader) && StringUtils.isNotBlank(headersMap.get(localityHeader))) {
                        String localityName = csvRecord.get(localityHeader);
                        countryZipcodes.put(headersMap.get(localityHeader), localityName);
                    }

                    if (StringUtils.isNotBlank(languageHeader) && StringUtils.isNotBlank(headersMap.get(languageHeader))) {
                        String language = csvRecord.get(languageHeader);
                        countryZipcodes.put(headersMap.get(languageHeader), language);
                    }

                    if (!Objects.isNull(staticFields) && staticFields.size() > 0) {
                        for (CsvStaticFieldsDto staticFieldsDto : staticFields) {
                            if (staticFieldValues.get(staticFieldsDto.getName()) != null) {
                                int value = staticFieldValues.get(staticFieldsDto.getName());
                                if (staticFieldsDto.getDirection() == DirectionType.INCREMENTAL) {
                                    ++value;
                                } else {
                                    --value;
                                }
                                staticFieldValues.put(staticFieldsDto.getName(), value);
                            } else {
                                staticFieldValues.put(staticFieldsDto.getName(), staticFieldsDto.getStartValue());
                            }
                            countryZipcodes.put(staticFieldsDto.getName(), staticFieldValues.get(staticFieldsDto.getName()));
                        }

                    }
                    jsonList.add(countryZipcodes);
                }

            }
            return jsonList;
        }
        catch (Exception e) {
            throw new AppException(e.getMessage(), 400, HttpStatus.BAD_REQUEST);
        }
    }

    private Map<String, String> getNormalizedCsvHeaders(List<String> headers) {
        Map<String, String> headerMap = new HashMap<>();
        for(int i=0; i<headers.size(); i++) {
            String header = StringUtility.removeSpecialCharacters(headers.get(i), "LOWER");
            if(StringUtils.isNotBlank(headerMap.get(header))) {
                throw new AppException("csv cannot have 2 duplicate headers: " + headerMap.get(header), 400, HttpStatus.BAD_REQUEST);
            }
            switch (header) {
                case "region1": {
                    headerMap.put("region1", headers.get(i));
                    break;
                }
                case "region2": {
                    headerMap.put("region2", headers.get(i));
                    break;
                }
                case "region3": {
                    headerMap.put("region3", headers.get(i));
                    break;
                }
                case "region4": {
                    headerMap.put("region4", headers.get(i));
                    break;
                }
                case "locality": {
                    headerMap.put("locality", headers.get(i));
                    break;
                }
                case "country": {
                    headerMap.put("country", headers.get(i));
                    break;
                }
                case "language": {
                    headerMap.put("language", headers.get(i));
                    break;
                }
                case "iso": {
                    headerMap.put("iso", headers.get(i));
                    break;
                }
                case "postcode": {
                    headerMap.put("postcode", headers.get(i));
                    break;
                }
            }
        }
        return headerMap;
    }


    @Override
    public List<String> uploadCsvFile(UploadCsvDto uploadCsvDto) {
        try {
            storageService.store(uploadCsvDto.getFile());
            Reader reader = new BufferedReader(new InputStreamReader(uploadCsvDto.getFile().getInputStream()));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader());
            List<String> allHeaders = getCsvHeaders(csvParser);

            return allHeaders;
        } catch (IOException e) {
            throw new AppException(e.getMessage(), 400, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public FileSystemResource processCsvFile(ProcessCsvDTO processCsvDTO) {
        Resource csvFile = storageService.loadAsResource(processCsvDTO.getFileName());
        List<Map<String,Object>> jsonObj = getDataFromCsv(csvFile, processCsvDTO.getStaticFields(), processCsvDTO.getJsonKeysMap());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String formattedJson = formatJsonString(objectMapper.writeValueAsString(jsonObj), objectMapper);
            String jsonFilePath = storageService.getRootLocationPath().toString() + "/"+processCsvDTO.getJsonFileName();
            File jsonFile = new File(jsonFilePath);
            FileUtils.writeStringToFile(jsonFile, formattedJson, StandardCharsets.UTF_8);
            return new FileSystemResource(jsonFile);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object getCsvData(ProcessCsvDTO processCsvDto) {
        Resource csvFile = storageService.loadAsResource(processCsvDto.getFileName());
        Map<String,Object> jsonKeysMap = new HashMap<>();
        try {
            Reader reader = new BufferedReader(new InputStreamReader(csvFile.getInputStream()));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withDelimiter(';').withHeader());
            List<String> headers = getCsvHeaders(csvParser);
            Map<String, String> csvHeaders = getNormalizedCsvHeaders(headers);
            String countryHeader = csvHeaders.get("country");
            String languageHeader = csvHeaders.get("language");
            String isoHeader = csvHeaders.get("iso");
            String postcodeHeader = csvHeaders.get("postcode");
            String localityHeader = csvHeaders.get("locality");
            String region1Header = csvHeaders.get("region1");
            String region2Header = csvHeaders.get("region2");
            String region3Header = csvHeaders.get("region3");
            String region4Header = csvHeaders.get("region4");
            Set<String> region1Set = new HashSet<>();
            for (CSVRecord csvRecord : csvParser) {
                String region1Name = csvRecord.get(region1Header);
                if(!region1Set.contains(region1Name)) {
                    region1Set.add(region1Name);
                }
            }
            jsonKeysMap.put("region1", region1Set);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return jsonKeysMap;
    }

    @Override
    public Set<Map<String, ArrayList<String>>> getUniqueCsvData(UniqueHeadersDto uniqueHeadersDto) {
        Resource csvFile = storageService.loadAsResource(uniqueHeadersDto.getFileName());
        Set<Map<String, ArrayList<String>>> listOfMap = new HashSet<>();

        try{
            Reader reader = new BufferedReader(new InputStreamReader(csvFile.getInputStream()));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withDelimiter(';').withHeader());
            Set<String> selectedHeaders = uniqueHeadersDto.getHeaders();
            List<String> headers = getCsvHeaders(csvParser);
            List<CSVRecord> allRecords = csvParser.getRecords();
            System.out.println(headers);
            System.out.println(selectedHeaders);

            if (allRecords != null && !allRecords.isEmpty()) {
                Map<String, ArrayList<String>> map = new HashMap<>();
                for(String header : selectedHeaders){
                    if(headers.contains(header)){
                        Set<String> uniqueValues = getUniqueValues(allRecords, header);
                        ArrayList<String> valuesArray = new ArrayList<>();
                        valuesArray.addAll(uniqueValues);
                        map.put(header, valuesArray);
                    }else {
                        throw new AppException("Header " + header + " not found", HttpStatus.BAD_REQUEST);
                    }
                }
                listOfMap.add(map);
            } else {
                throw new AppException("CSV file is empty", HttpStatus.BAD_REQUEST);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return listOfMap;
    }

    private List<String> getCsvHeaders(CSVParser csvParser) {
        List<String> allHeaders = new ArrayList<>();
        List<String> csvHeaders = csvParser.getHeaderNames();
        allHeaders.addAll(csvHeaders);
        Map<Integer, List<String>> removeHeaders = new HashMap<>();
        int i = 0;
        for(String header: allHeaders) {
            if(header.contains(";")) {
                List<String> newHeaders = Arrays.stream(header.split(";")).toList();
                removeHeaders.put(i, newHeaders);
            }
            ++i;
        }

        for (int key : removeHeaders.keySet()) {
            List<String> headers = removeHeaders.get(key);
            allHeaders.remove(key);
            allHeaders.addAll(headers);
        }
        return allHeaders;
    }

    private static String formatJsonString(String jsonString, ObjectMapper mapper) {
        try {
            // Parse JSON string
            Object json = mapper.readValue(jsonString, Object.class);

            // Serialize JSON node to a formatted string
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return jsonString; // Return the original string if an error occurs
        }
    }

    private static int getHeaderIndex(CSVRecord headerRecord, String headerName) {
        for (int i = 0; i < headerRecord.size(); i++) {
            if (headerRecord.get(i).equals(headerName)) {
                return i;
            }
        }
        return -1;
    }
    private static Set<String> getUniqueValues(List<CSVRecord> records, String headerName) {
        Set<String> uniqueValues = new HashSet<>();
        for (CSVRecord record : records) {
            if (record.isMapped(headerName)) {
                uniqueValues.add(record.get(headerName));
            }
        }
        return uniqueValues;
    }

    private static List<CSVRecord> readCSV(String filePath) throws IOException {
        try (FileReader fileReader = new FileReader(filePath);
             CSVParser csvParser = CSVFormat.DEFAULT.withHeader().parse(fileReader)) {
            return csvParser.getRecords();
        }
    }

}
