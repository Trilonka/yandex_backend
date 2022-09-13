package com.example.yandexBackend.controller;

import com.example.yandexBackend.dto.response.SystemItemHistoryResponse;
import com.example.yandexBackend.dto.response.Error;
import com.example.yandexBackend.dto.response.SystemItemResponse;
import com.example.yandexBackend.model.SystemItem;
import com.example.yandexBackend.dto.request.SystemItemImport;
import com.example.yandexBackend.dto.request.SystemItemImportRequest;
import com.example.yandexBackend.model.SystemItemHistoryUnit;
import com.example.yandexBackend.model.constant.ErrorMessages;
import com.example.yandexBackend.service.SystemItemHistoryUnitService;
import com.example.yandexBackend.service.SystemItemService;
import com.example.yandexBackend.util.exception.SystemItemNotFoundException;
import com.example.yandexBackend.util.exception.SystemItemNotValidException;
import com.example.yandexBackend.util.validator.SystemItemValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class MainController {

    private final ModelMapper modelMapper;
    private final SystemItemService systemItemService;
    private final SystemItemHistoryUnitService systemItemHistoryUnitService;
    private final SystemItemValidator systemItemValidator;

    @Autowired
    public MainController(ModelMapper modelMapper,
                          SystemItemService systemItemService,
                          SystemItemHistoryUnitService systemItemHistoryUnitService, SystemItemValidator systemItemValidator)
    {
        this.modelMapper = modelMapper;
        this.systemItemService = systemItemService;
        this.systemItemHistoryUnitService = systemItemHistoryUnitService;
        this.systemItemValidator = systemItemValidator;
    }

    @PostMapping("/imports")
    public ResponseEntity<HttpStatus> load(@RequestBody @Valid SystemItemImportRequest requestItems,
                                           BindingResult result)
    {
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
        boolean deleted = systemItemService.deleteIfExists(id);

        if (!deleted)
            throw new SystemItemNotFoundException();

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/nodes/{id}")
    public ResponseEntity<SystemItemResponse> getById(@PathVariable("id") String id) {
        Optional<SystemItem> systemItem = systemItemService.findById(id);

        if (systemItem.isEmpty())
            throw new SystemItemNotFoundException();

        return new ResponseEntity<>(convertToSystemItemResponse(systemItem.get()), HttpStatus.OK);
    }

    @GetMapping("/updates")
    public ResponseEntity<SystemItemHistoryResponse> getUpdates(@RequestParam("date") String date)
    {
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(date);
        return new ResponseEntity<>(
                new SystemItemHistoryResponse(systemItemService.getByDateBefore(zonedDateTime).stream()
                        .map(this::convertToSystemItemHistoryUnit)
                        .collect(Collectors.toList())),
                HttpStatus.OK);
    }

    @GetMapping("/node/{id}/history")
    public ResponseEntity<SystemItemHistoryResponse> getItemHistory(@PathVariable("id") String id,
                                                                    @RequestParam("dateStart") String dateStart,
                                                                    @RequestParam("dateEnd") String dateEnd)
    {
        if (systemItemService.findById(id).isEmpty())
            throw new SystemItemNotFoundException();

        List<SystemItemHistoryUnit> itemHistory = systemItemHistoryUnitService.getItemHistory(id, dateStart, dateEnd);

        return new ResponseEntity<>(new SystemItemHistoryResponse(itemHistory), HttpStatus.OK);
    }

    private SystemItem convertToSystemItem(SystemItemImport itemImport) {
        return modelMapper.map(itemImport, SystemItem.class);
    }

    private SystemItemHistoryUnit convertToSystemItemHistoryUnit(SystemItem systemItem) {
        return modelMapper.map(systemItem, SystemItemHistoryUnit.class);
    }

    private SystemItemResponse convertToSystemItemResponse(SystemItem systemItem) {
        return modelMapper.map(systemItem, SystemItemResponse.class);
    }

    @ExceptionHandler(value =
            {SystemItemNotValidException.class,
            DateTimeParseException.class,
            MissingServletRequestParameterException.class})
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
