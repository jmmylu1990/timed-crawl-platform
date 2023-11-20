package com.example.customer.service.impl;

import com.example.customer.mysql.entity.CityAndInterCityBus;
import com.example.customer.mysql.repository.CityAndInterCityBusRepository;
import com.example.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CityAndInterCityBusRepository cityAndInterCityBusRepository;

    @Override
    @Cacheable(value = "CityAndInterCityBus", keyGenerator = "wiselyKeyGenerator")
    public List<CityAndInterCityBus> queryCityAndInterCityBusList() {
        return cityAndInterCityBusRepository.findAll();
    }
}
