package com.app.csvtool.dto;

import com.app.csvtool.enums.DirectionType;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CsvStaticFieldsDto {
    private String name; // like dmaCode
    private DirectionType direction;
    private Integer startValue; // starting from 0 or 111 or 144 etc.
    private String value;
}
