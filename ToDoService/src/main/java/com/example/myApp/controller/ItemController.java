package com.example.myApp.controller;

import com.example.myApp.entity.Items;
import com.example.myApp.exceptions.ItemNotFoundException;
import com.example.myApp.repository.ItemRepository;
import com.example.myApp.service.ItemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todo/item")
@Tag(name = "Item Controller")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;


    @PostMapping("/add")
    public ResponseEntity<Items> addItem(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Items item
    ) {
        return ResponseEntity.ok(itemService.addItem(authorization, item));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<Items> updateItem(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id,
            @RequestBody Items item
    ) {
        return ResponseEntity.ok(itemService.updateItem(authorization, id, item));
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteItem(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id) {

        itemService.deleteItem(authorization, id);
        return ResponseEntity.ok("Item deleted successfully");
    }


    @GetMapping("/search/{id}")
    public ResponseEntity<Items> searchItemById(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(itemService.searchItemById(authorization, id));
    }


    @GetMapping("/search")
    public ResponseEntity<List<Items>> searchItemByTitle(
            @RequestHeader("Authorization") String authorization,
            @RequestParam String title
    ) {

        return ResponseEntity.ok(itemService.searchItemByTitle(authorization, title));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<Items>> getAllItems(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(itemService.getItems(authorization, page, size));
    }
}
