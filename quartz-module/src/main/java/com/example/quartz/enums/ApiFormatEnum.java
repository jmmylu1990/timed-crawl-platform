package com.example.quartz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.MediaType;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum ApiFormatEnum {
	
	JSON(MediaType.APPLICATION_JSON),
	XML(MediaType.APPLICATION_XML),
	CSV(MediaType.TEXT_PLAIN),
	TEXT(MediaType.TEXT_PLAIN),
	FILE(MediaType.APPLICATION_OCTET_STREAM),
	STREAM(MediaType.APPLICATION_OCTET_STREAM);
	
	private MediaType mediaType;
	
	public static ApiFormatEnum fromName(String name) {
		return Stream.of(ApiFormatEnum.values())
				.filter(a -> a.name().equalsIgnoreCase(name))
				.findAny()
				.orElse(TEXT);
	}
}
