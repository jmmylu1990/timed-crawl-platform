package com.example.customer.mysql.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
@Entity
@Table(name = "city_and_inter_city_bus")
public @Data class CityAndInterCityBus implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "plate_numb")
    private String plateNumb;
    @Column(name = "operator_id")
    private String operatorID;
    @Column(name = "operator_code")
    private String operatorCode;
    @Column(name = "operator_no")
    private String operatorNo;
    @Column(name = "vehicle_class")
    private Integer vehicleClass;
    @Column(name = "vehicle_type")
    private Integer vehicleType;
    @Column(name = "card_reader_layout")
    private Integer cardReaderLayout;
    @Column(name = "is_electric")
    private Boolean isElectric;
    @Column(name = "is_hybrid")
    private Boolean isHybrid;
    @Column(name = "is_low_floor")
    private Boolean isLowFloor;
    @Column(name = "has_lift_or_ramp")
    private Boolean hasLiftOrRamp;
    @Column(name = "has_wifi")
    private Boolean hasWifi;
    @Column(name = "in_box_id")
    private String inBoxID;
    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


}
