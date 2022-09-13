package com.example.yandexBackend.controller;

import com.example.yandexBackend.dto.request.SystemItemImport;
import com.example.yandexBackend.dto.request.SystemItemImportRequest;
import com.example.yandexBackend.model.SystemItem;
import com.example.yandexBackend.model.constant.SystemItemType;
import com.example.yandexBackend.repository.SystemItemRepository;
import com.example.yandexBackend.util.exception.SystemItemNotValidException;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LoadDataTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ModelMapper modelMapper;

    @LocalServerPort
    int randomServerPort;

    @MockBean
    private SystemItemRepository systemItemRepository;

    @Test
    public void testLoadItems() throws Exception {
        SystemItemImport item1 = SystemItemImport.builder()
                .id("1")
                .type(SystemItemType.FOLDER)
                .parentId(null)
                .build();

        Mockito.when(this.systemItemRepository.saveAll(List.of(convertToSystemItem(item1))))
                .thenThrow(SystemItemNotValidException.class);

        final String baseUrl = "http://localhost:"+randomServerPort+"/imports";
        URI uri = new URI(baseUrl);

        SystemItemImportRequest requestItem = SystemItemImportRequest.builder()
                .items(List.of(item1))
                .updateDate("2022-02-01T12:00:00Z")
                .build();

        HttpEntity<SystemItemImportRequest> request = new HttpEntity<>(requestItem);

        ResponseEntity<HttpStatus> result = this.restTemplate.postForEntity(uri, request, HttpStatus.class);

        assertEquals(200, result.getStatusCodeValue());
    }

    private SystemItem convertToSystemItem(SystemItemImport systemItemImport) {
        return modelMapper.map(systemItemImport, SystemItem.class);
    }
}