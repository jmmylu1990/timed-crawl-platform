package com.example.customer.mysql.repository;

import com.example.customer.mysql.entity.CityAndInterCityBus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityAndInterCityBusRepository extends JpaRepository<CityAndInterCityBus,String> {


}
