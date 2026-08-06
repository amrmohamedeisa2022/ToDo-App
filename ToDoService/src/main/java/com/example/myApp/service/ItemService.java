package com.example.myApp.service;

import com.example.myApp.entity.Items;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ItemService {

    Items addItem(String authorization, Items item);

    Items updateItem(String authorization, Long id, Items item);

    void deleteItem(String authorization, Long id);

    Items searchItemById(String authorization, Long id);

    List<Items> searchItemByTitle(String authorization, String title);

    Page<Items> getItems(String authorization, int page, int size);
}
