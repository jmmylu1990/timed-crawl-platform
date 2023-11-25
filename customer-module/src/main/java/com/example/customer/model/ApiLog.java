package com.example.customer.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

public @Data class ApiLog implements Serializable {

    public ApiLog() {
    }

    public ApiLog(String realIpAddress, String methodName, String clazzSimpleNamel, String packageName, Date executeDateTime) {
        this.realIpAddress = realIpAddress;
        this.methodName = methodName;
        this.clazzSimpleNamel = clazzSimpleNamel;
        this.packageName = packageName;
        this.executeDateTime = executeDateTime;
    }

    private String realIpAddress;

    private String methodName;

    private String clazzSimpleNamel;

    private String packageName;

    private Date executeDateTime;


}
