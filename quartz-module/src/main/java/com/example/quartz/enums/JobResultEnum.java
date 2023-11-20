package com.example.quartz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JobResultEnum {

	AWAITING(0, "候隊執行"),
	SUCCESS(1, "執行成功"),
	EXECUTING(2, "執行中"),
	FAIL(3, "執行失敗"),
	PENDING(4, "重做等待中");

	private int code;

	private String desc;

}
