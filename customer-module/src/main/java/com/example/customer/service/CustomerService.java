package com.example.customer.service;

import com.example.customer.mysql.entity.CityAndInterCityBus;

import java.util.List;

public interface CustomerService {
    List<CityAndInterCityBus> queryCityAndInterCityBusList();

}
