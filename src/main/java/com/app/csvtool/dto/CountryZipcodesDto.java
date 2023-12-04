package com.app.csvtool.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CountryZipcodesDto {
    private String iso;
    private String country;
    private String language;
    private String region1;
    private String region2;
    private String region3;
    private String region4;
    private String locality;
    private String postcode;
}
