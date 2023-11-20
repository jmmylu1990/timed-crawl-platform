package com.example.customer.controller;

import com.example.customer.mysql.entity.CityAndInterCityBus;
import com.example.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/customer/api/query")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/queryCityAndInterCityBusList")
    public List<CityAndInterCityBus> cityAndInterCityBusJob()  {
        return customerService.queryCityAndInterCityBusList();
    }
}
