package com.app.csvtool.payload;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ApiResponse<T> {
	
	private T data;
	
	public ApiResponse(T data) {
		this.data = data;
	}
	
}
