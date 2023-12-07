package com.app.csvtool.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Set;

@Data
@Accessors(chain = true)
public class UniqueHeadersDto {
    private String fileName;
    private Set<String> headers;

}
