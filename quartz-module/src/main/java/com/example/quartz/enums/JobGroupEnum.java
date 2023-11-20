package com.example.quartz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum JobGroupEnum {

	ETL_GROUP("ETL-Group"),
	SQL_CALLER_GROUP("SQL-Caller-Group"),
	SHELL_CALLER_GROUP("Shell-Caller-Group"),
	API_TRIGGER_GROUP("API-Trigger-Group");
	
	private String name;

	public static JobGroupEnum fromName(String name) {
		return Stream.of(JobGroupEnum.values())
				.filter(j -> j.name.equals(name))
				.findAny().orElse(null);
	}

}
