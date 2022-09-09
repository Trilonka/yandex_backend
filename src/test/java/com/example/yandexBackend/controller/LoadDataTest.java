package com.example.yandexBackend.controller;

import com.example.yandexBackend.model.SystemItemImport;
import com.example.yandexBackend.model.SystemItemImportRequest;
import com.example.yandexBackend.model.constant.SystemItemType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class LoadDataTest {

    String URL = "https://localhost:8080/imports";
    private final MainController controller;

    @Autowired
    LoadDataTest(MainController controller) {
        this.controller = controller;
    }

    @Test
    void load() {
        SystemItemImportRequest request = new SystemItemImportRequest();
        SystemItemImport itemImport = new SystemItemImport();
        itemImport.setId("069cb8d7-bbdd-47d3-ad8f-82ef4c269df1");
        itemImport.setType(SystemItemType.FOLDER);
        request.setItems(
                List.of(
                        itemImport
                )
        );
        request.setUpdateDate("2022-02-01T12:00:00Z");

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<SystemItemImportRequest> httpRequest = new HttpEntity<>(request);
        ResponseEntity response = restTemplate.postForObject(URL, httpRequest, ResponseEntity.class);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
}