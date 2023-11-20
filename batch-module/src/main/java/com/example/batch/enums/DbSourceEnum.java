package com.example.batch.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum DbSourceEnum {

	MY_SQL("mysql"), MS_SQL("mssql"), ORACLE("oracle"), IMPALA("impala"), 
	JSON("json"), XML("xml"), IGNITE("ignite");

	private String name;

	public static DbSourceEnum fromName(String name) {
		return Stream.of(DbSourceEnum.values())
			.filter(dbSource -> dbSource.getName().equals(name))
			.findFirst()
			.orElseThrow(IllegalArgumentException::new);
	}
}
