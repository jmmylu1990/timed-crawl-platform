package com.example.batch.mapper;

import com.example.batch.job.model.CityAndInterCityBus;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.item.json.JacksonJsonObjectReader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class CityAndInterCityBusReaderJsonReader extends JacksonJsonObjectReader<CityAndInterCityBus> {



    public CityAndInterCityBusReaderJsonReader(Class<? extends CityAndInterCityBus> itemType) {
        super(itemType);

    }


}
