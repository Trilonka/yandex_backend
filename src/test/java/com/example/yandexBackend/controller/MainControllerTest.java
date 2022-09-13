package com.example.yandexBackend.controller;

import com.example.yandexBackend.dto.request.SystemItemImport;
import com.example.yandexBackend.dto.request.SystemItemImportRequest;
import com.example.yandexBackend.model.SystemItem;
import com.example.yandexBackend.model.SystemItemHistoryUnit;
import com.example.yandexBackend.model.constant.SystemItemType;
import com.example.yandexBackend.repository.SystemItemHistoryUnitRepository;
import com.example.yandexBackend.repository.SystemItemRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MainControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    int randomServerPort;

    @MockBean
    private SystemItemRepository systemItemRepository;

    @MockBean
    private SystemItemHistoryUnitRepository systemItemHistoryUnitRepository;

    private final SystemItemImport RECORD_1_1 = SystemItemImport.builder()
            .id("record_1-1")
            .type(SystemItemType.FOLDER)
            .parentId(null).build();
    private final SystemItemImport RECORD_1_2 = SystemItemImport.builder()
            .id("record_1-2")
            .type(SystemItemType.FOLDER)
            .parentId(RECORD_1_1.getId()).build();
    private final SystemItemImport RECORD_1_3 = SystemItemImport.builder()
            .id("record_1-3")
            .type(SystemItemType.FILE)
            .parentId(RECORD_1_2.getId())
            .url("/file/url1")
            .size(128).build();
    private final SystemItemImport RECORD_1_4 = SystemItemImport.builder()
            .id("record_1-4")
            .type(SystemItemType.FILE)
            .parentId(RECORD_1_2.getId())
            .url("/file/url1")
            .size(256).build();

    @Test
    public void loadSuccess() throws Exception {
        SystemItemImportRequest requestItem = SystemItemImportRequest.builder()
                .items(List.of(RECORD_1_3, RECORD_1_4, RECORD_1_2, RECORD_1_1))
                .updateDate("2022-02-01T12:00:00Z")
                .build();

        Assertions.assertEquals(200, postRequestStatus(requestItem));
    }

    @Test
    public void loadHasRepeats() throws Exception {
        SystemItemImportRequest requestItem = SystemItemImportRequest.builder()
                .items(List.of(RECORD_1_3, RECORD_1_4, RECORD_1_1, RECORD_1_1))
                .updateDate("2022-03-01T12:00:00Z")
                .build();

        Assertions.assertEquals(400, postRequestStatus(requestItem));
    }

    @Test
    public void loadNoParent() throws Exception {
        Mockito.when(this.systemItemRepository.saveAll(anyList()))
                .thenReturn(null);
        Mockito.when(this.systemItemHistoryUnitRepository.saveAll(anyList()))
                .thenReturn(null);

        SystemItemImportRequest requestItem = SystemItemImportRequest.builder()
                .items(List.of(RECORD_1_3, RECORD_1_4, RECORD_1_2))
                .updateDate("2022-04-01T12:00:00Z")
                .build();

        Assertions.assertEquals(400, postRequestStatus(requestItem));
    }

    @Test
    public void loadNoElements() throws Exception {
        SystemItemImportRequest requestItem = SystemItemImportRequest.builder()
                .items(Collections.emptyList())
                .updateDate("2022-05-01T12:00:00Z")
                .build();

        Assertions.assertEquals(400, postRequestStatus(requestItem));
    }

    @Test
    public void loadNotValidDate() throws Exception {
        SystemItemImportRequest requestItem = SystemItemImportRequest.builder()
                .items(List.of(RECORD_1_3, RECORD_1_4, RECORD_1_2, RECORD_1_1))
                .updateDate("202-04-01T12:00:00Z")
                .build();

        Assertions.assertEquals(400, postRequestStatus(requestItem));
    }

    @Test
    @Timeout(value = 1)
    public void successLoad1000Items() throws Exception {
        LinkedList<SystemItemImport> itemsForRequest = new LinkedList<>();
        itemsForRequest.addLast(SystemItemImport.builder()
                        .id("item-0")
                        .type(SystemItemType.FOLDER)
                        .parentId(null).build());
        for (int i = 1; i<1000; i++) {
            itemsForRequest.addFirst(
                    SystemItemImport.builder()
                            .id("item-"+i)
                            .type(SystemItemType.FOLDER)
                            .parentId("item-"+(i-1)).build()
            );
        }

        SystemItemImportRequest requestItem = SystemItemImportRequest.builder()
                .items(itemsForRequest)
                .updateDate("2022-05-01T12:00:00Z")
                .build();

        Assertions.assertEquals(200, postRequestStatus(requestItem));
    }

    @Test
    public void removeSuccess() throws Exception {
        Mockito.when(this.systemItemRepository.findById("item-526"))
                .thenReturn(Optional.of(new SystemItem()));

        Assertions.assertEquals(200, deleteRequestStatus("item-526"));
    }

    @Test
    public void removeNotExist() throws Exception {
        Assertions.assertEquals(404, deleteRequestStatus("somenodefds"));
    }

    @Test
    public void getSuccess() throws Exception {
        Mockito.when(this.systemItemRepository.findById("hinode"))
                .thenReturn(Optional.of(new SystemItem()));

        Assertions.assertEquals(200, getRequestStatus("nodes/hinode"));
    }

    @Test
    public void getNotExists() throws Exception {
        Assertions.assertEquals(404, getRequestStatus("nodes/fjsdklfjsdklfjlsdk"));
    }

    @Test
    public void last24HoursUpdatesSuccess() throws Exception {
        Mockito.when(this.systemItemRepository.findByDateIsBetween(anyString(), anyString()))
                .thenReturn(List.of(new SystemItem()));

        Assertions.assertEquals(200, getRequestStatus("updates?date=2022-02-04T00:00:00Z"));
    }

    @Test
    public void last24HoursUpdatesNotValidDateFormat() throws Exception {
        Mockito.when(this.systemItemRepository.findByDateIsBetween(anyString(), anyString()))
                .thenReturn(List.of(new SystemItem()));

        Assertions.assertEquals(400, getRequestStatus("updates?date=2022.02-04T00:00:00Z"));
    }

    @Test
    public void itemHistoryValidId() throws Exception {
        Mockito.when(this.systemItemHistoryUnitRepository.findByIdAndDateIsBetween(anyString(), anyString(), anyString()))
                .thenReturn(List.of(new SystemItemHistoryUnit()));
        Mockito.when(this.systemItemRepository.findById(anyString()))
                .thenReturn(Optional.of(new SystemItem()));

        Assertions.assertEquals(200, getRequestStatus("node/somenode/history?dateStart=2020-02-04T00:00:00Z&dateEnd=2022-02-04T00:00:00Z"));
    }

    @Test
    public void itemHistoryNotValidDate() throws Exception {
        Mockito.when(this.systemItemHistoryUnitRepository.findByIdAndDateIsBetween(anyString(), anyString(), anyString()))
                .thenReturn(List.of(new SystemItemHistoryUnit()));
        Mockito.when(this.systemItemRepository.findById(anyString()))
                .thenReturn(Optional.of(new SystemItem()));

        Assertions.assertEquals(400, getRequestStatus("node/somenode/history?dateStart=2020-02-0d4T00:00:00Z&dateEnd=2022-02-04T00:00:00Z"));
    }

    @Test
    public void itemHistoryNotExists() throws Exception {
        Assertions.assertEquals(404, getRequestStatus("node/somenodeee/history?dateStart=2020-02-0d4T00:00:00Z&dateEnd=2022-02-04T00:00:00Z"));
    }

    private int postRequestStatus(SystemItemImportRequest requestItem) throws Exception {
        final String baseUrl = "http://localhost:"+randomServerPort+"/imports";
        URI uri = new URI(baseUrl);

        HttpEntity<SystemItemImportRequest> request = new HttpEntity<>(requestItem);

        ResponseEntity<Object> result = this.restTemplate.postForEntity(uri, request, Object.class);

        return result.getStatusCodeValue();
    }

    private int deleteRequestStatus(String id) throws Exception {
        final String baseUrl = "http://localhost:"+randomServerPort+"/delete/"+id;
        URI uri = new URI(baseUrl);

        ResponseEntity<Object> result = this.restTemplate.exchange(uri, HttpMethod.DELETE, null, Object.class);

        return result.getStatusCodeValue();
    }

    private int getRequestStatus(String address) throws Exception {
        final String baseUrl = "http://localhost:"+randomServerPort+"/"+address;
        URI uri = new URI(baseUrl);

        ResponseEntity<Object> result = this.restTemplate.getForEntity(uri, Object.class);

        return result.getStatusCodeValue();
    }
}