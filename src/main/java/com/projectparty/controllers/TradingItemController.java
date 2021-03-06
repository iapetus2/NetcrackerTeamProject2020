package com.projectparty.controllers;

import com.projectparty.entities.TradingItem;
import com.projectparty.service.TradingItemServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin(origins = "*")
public class TradingItemController {

    private final TradingItemServiceImpl tradingItemService; //todo interface

    @Autowired
    public TradingItemController(TradingItemServiceImpl tradingItemService) {
        this.tradingItemService = tradingItemService;
    }

    @PostMapping(value = "/api/item")
    public ResponseEntity<?> save(@RequestBody TradingItem item) {
        tradingItemService.save(item);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/api/item/{id}")
    public ResponseEntity<TradingItem> read(@PathVariable(name = "id") int id) {
        final TradingItem tradingItem = tradingItemService.read(id);

        return tradingItem != null
                ? new ResponseEntity<>(tradingItem, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/api/item")
    public ResponseEntity<List<TradingItem>> read() {
        final List<TradingItem> tradingItems = tradingItemService.readAll();

        return tradingItems != null &&  !tradingItems.isEmpty()
                ? new ResponseEntity<>(tradingItems, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping(value = "/api/item/{id}")
    public ResponseEntity<?> update(@PathVariable(name = "id") int id, @RequestBody TradingItem tradingItem) {
        final boolean updated = tradingItemService.update(tradingItem, id);

        return updated
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }

    @DeleteMapping(value = "/api/item/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") int id) {
        final boolean deleted = tradingItemService.delete(id);

        return deleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }
}

