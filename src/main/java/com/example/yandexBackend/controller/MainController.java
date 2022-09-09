package com.example.yandexBackend.controller;

import com.example.yandexBackend.model.Folder;
import com.example.yandexBackend.model.SystemItemImportRequest;
import com.example.yandexBackend.repository.SystemItemRepository;
import com.example.yandexBackend.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class MainController {

    private final SystemItemRepository itemRepository;
    private final FolderService folderService;

    @Autowired
    public MainController(SystemItemRepository itemRepository, FolderService folderService) {
        this.itemRepository = itemRepository;
        this.folderService = folderService;
    }

    @PostMapping("/imports")
    public ResponseEntity<HttpStatus> getImports(@RequestBody SystemItemImportRequest request) {
        return null;
    } // сохранить данные

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> remove(@PathVariable("id") String id) {
        return null;
    }

    @GetMapping("/nodes/{id}")
    public ResponseEntity<HttpStatus> getById(@PathVariable("id") String id) {
        return null;
    }

    @GetMapping
    public String ex() {

        Folder folder = new Folder();
        folder.setName("first");

        folderService.save(folder);

        Folder folder1 = folderService.get(1);

        Folder folder2 = new Folder();
        folder2.setName("second");
        folder2.setParent(List.of(folder1));

        folderService.save(folder2);

        return "all ok";
    }

    // get updates
    // get node/{id}/history
}
