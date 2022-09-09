package com.example.yandexBackend.controller;

import com.example.yandexBackend.model.Error;
import com.example.yandexBackend.model.SystemItem;
import com.example.yandexBackend.model.SystemItemImport;
import com.example.yandexBackend.model.SystemItemImportRequest;
import com.example.yandexBackend.service.SystemItemService;
import com.example.yandexBackend.util.SystemItemImportRequestValid;
import com.example.yandexBackend.util.SystemItemNotFoundException;
import com.example.yandexBackend.util.SystemItemNotValidException;
import com.example.yandexBackend.util.SystemItemValid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;


@RestController
public class MainController {

    private final ModelMapper modelMapper;
    private final SystemItemService systemItemService;
    private final SystemItemValid systemItemValid;
    private final SystemItemImportRequestValid systemItemImportRequestValid;

    @Autowired
    public MainController(ModelMapper modelMapper, SystemItemService systemItemService, SystemItemValid systemItemValid, SystemItemImportRequestValid systemItemImportRequestValid) {
        this.modelMapper = modelMapper;
        this.systemItemService = systemItemService;
        this.systemItemValid = systemItemValid;
        this.systemItemImportRequestValid = systemItemImportRequestValid;
    }

    @PostMapping("/imports")
    public ResponseEntity<HttpStatus> load(@RequestBody @Valid SystemItemImportRequest request,
                                                 BindingResult result)
    {
        if (request==null)
            throw new SystemItemNotValidException("Validation Failed");

        systemItemImportRequestValid.validate(request, result);

        if (result.hasErrors())
            throw new SystemItemNotValidException("Validation Failed");

        for (SystemItemImport itemImport : request.getItems()) {
            systemItemValid.validate(convertToSystemItem(itemImport), result);
            if (result.hasErrors())
                throw new SystemItemNotValidException("Validation Failed");
        }

        systemItemService.saveList(request.getItems().stream().map(this::convertToSystemItem).collect(Collectors.toList()), request.getUpdateDate());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> remove(@PathVariable("id") String id) {
        boolean deleted = systemItemService.removeIfExists(id);
        if (!deleted)
            throw new SystemItemNotFoundException("Item not found");

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/nodes/{id}")
    public ResponseEntity<HttpStatus> getById(@PathVariable("id") String id) {
        return null;
    }

    private SystemItem convertToSystemItem(SystemItemImport itemImport) {
        return modelMapper.map(itemImport, SystemItem.class);
    }

    @ExceptionHandler
    private ResponseEntity<Error> handleException(SystemItemNotValidException exception) {
        Error error = new Error(400, exception.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<Error> handleException(SystemItemNotFoundException exception) {
        Error error = new Error(404, exception.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
