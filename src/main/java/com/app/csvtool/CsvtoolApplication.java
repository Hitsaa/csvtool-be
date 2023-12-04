package com.app.csvtool;

import com.app.csvtool.customProperties.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class CsvtoolApplication {

	public static void main(String[] args) {
		SpringApplication.run(CsvtoolApplication.class, args);
	}

}
