package com.example.yandexBackend.controller;

import com.example.yandexBackend.model.Error;
import com.example.yandexBackend.model.SystemItem;
import com.example.yandexBackend.dto.SystemItemImport;
import com.example.yandexBackend.dto.SystemItemImportRequest;
import com.example.yandexBackend.model.constant.ErrorMessages;
import com.example.yandexBackend.service.SystemItemService;
import com.example.yandexBackend.util.SystemItemImportRequestValidator;
import com.example.yandexBackend.util.SystemItemNotFoundException;
import com.example.yandexBackend.util.SystemItemNotValidException;
import com.example.yandexBackend.util.SystemItemValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class MainController {

    private final ModelMapper modelMapper;
    private final SystemItemService systemItemService;
    private final SystemItemValidator systemItemValidator;
    private final SystemItemImportRequestValidator systemItemImportRequestValidator;

    @Autowired
    public MainController(ModelMapper modelMapper,
                          SystemItemService systemItemService,
                          SystemItemValidator systemItemValidator,
                          SystemItemImportRequestValidator systemItemImportRequestValidator)
    {
        this.modelMapper = modelMapper;
        this.systemItemService = systemItemService;
        this.systemItemValidator = systemItemValidator;
        this.systemItemImportRequestValidator = systemItemImportRequestValidator;
    }

    @PostMapping("/imports")
    public ResponseEntity<HttpStatus> load(@RequestBody @Valid SystemItemImportRequest requestItems,
                                           BindingResult result)
    {
        if (requestItems==null || requestItems.getItems().size()<1)
            throw new SystemItemNotValidException();

        systemItemImportRequestValidator.validate(requestItems, result);

        if (result.hasErrors())
            throw new SystemItemNotValidException();

        for (SystemItemImport itemImport : requestItems.getItems()) {
            systemItemValidator.validate(convertToSystemItem(itemImport), result);
            if (result.hasErrors())
                throw new SystemItemNotValidException();
        }

        systemItemService.saveList(requestItems.getItems().stream()
                .map(this::convertToSystemItem)
                .collect(Collectors.toList()), requestItems.getUpdateDate());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> remove(@PathVariable("id") String id) {
        boolean deleted = systemItemService.removeIfExists(id);

        if (!deleted)
            throw new SystemItemNotFoundException();

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public void remove() {
        throw new SystemItemNotValidException();
    }

    @GetMapping("/nodes/{id}")
    public ResponseEntity<SystemItem> getById(@PathVariable("id") String id) {
        Optional<SystemItem> systemItem = systemItemService.findById(id);

        if (systemItem.isEmpty())
            throw new SystemItemNotFoundException();

        return new ResponseEntity<>(systemItem.get(), HttpStatus.OK);
    }

    @GetMapping("/nodes")
    public void getById() {
        throw new SystemItemNotValidException();
    }

    private SystemItem convertToSystemItem(SystemItemImport itemImport) {
        return modelMapper.map(itemImport, SystemItem.class);
    }

    @ExceptionHandler(value = {SystemItemNotValidException.class})
    private ResponseEntity<Error> handleNotValid() {
        return new ResponseEntity<>(
                new Error(400, ErrorMessages.NOT_VALID.getValue()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {SystemItemNotFoundException.class})
    private ResponseEntity<Error> handleNotFound() {
        return new ResponseEntity<>(
                new Error(404, ErrorMessages.NOT_FOUND.getValue()),
                HttpStatus.NOT_FOUND);
    }
}
