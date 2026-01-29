package com.example.myApp.service;

import com.example.myApp.entity.Items;
import com.example.myApp.entity.ItemsDetails;
import com.example.myApp.entity.Priority;
import com.example.myApp.exceptions.ItemNotFoundException;
import com.example.myApp.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserClientService userClientService;

    @Override
    public Items addItem(String authorization, Items item) {

        userClientService.validateToken(authorization);

        if (item == null) {
            throw new ItemNotFoundException("Item data is missing");
        }

        if (item.getTitle() == null || item.getTitle().isBlank()) {
            throw new ItemNotFoundException("Title is required");
        }

        if (item.getUserId() == null) {
            throw new ItemNotFoundException("userId is required");
        }

        if (item.getItemsDetails() == null) {
            ItemsDetails details = ItemsDetails.builder()
                    .description("No description")
                    .createdAt(LocalDateTime.now())
                    .priority(Priority.LOW)
                    .status(false)
                    .build();

            item.setItemsDetails(details);
        }

        item.getItemsDetails().setItem(item);

        if (item.getItemsDetails().getCreatedAt() == null) {
            item.getItemsDetails().setCreatedAt(LocalDateTime.now());
        }
        if (item.getItemsDetails().getPriority() == null) {
            item.getItemsDetails().setPriority(Priority.LOW);
        }
        if (item.getItemsDetails().getStatus() == null) {
            item.getItemsDetails().setStatus(false);
        }

        return itemRepository.save(item);
    }

    @Override
    public Items updateItem(String authorization, Long id, Items item) {

        userClientService.validateToken(authorization);

        Items existingItem = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found with id: " + id));

        if (item.getTitle() != null && !item.getTitle().isBlank()) {
            existingItem.setTitle(item.getTitle());
        }

        if (existingItem.getItemsDetails() != null && item.getItemsDetails() != null) {

            ItemsDetails existingDetails = existingItem.getItemsDetails();
            ItemsDetails newDetails = item.getItemsDetails();

            if (newDetails.getDescription() != null && !newDetails.getDescription().isBlank()) {
                existingDetails.setDescription(newDetails.getDescription());
            }
            if (newDetails.getPriority() != null) {
                existingDetails.setPriority(newDetails.getPriority());
            }
            if (newDetails.getStatus() != null) {
                existingDetails.setStatus(newDetails.getStatus());
            }
        }

        return itemRepository.save(existingItem);
    }

    @Override
    public void deleteItem(String authorization, Long id) {

        userClientService.validateToken(authorization);

        Items existingItem = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found with id: " + id));

        itemRepository.delete(existingItem);
    }

    @Override
    public Items searchItemById(String authorization, Long id) {

        userClientService.validateToken(authorization);

        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found with id: " + id));
    }

    @Override
    public List<Items> searchItemByTitle(String authorization, String title) {
        
        userClientService.validateToken(authorization);

        if (title == null || title.isBlank()) {
            throw new ItemNotFoundException("Title is required");
        }

        return itemRepository.findAllByTitleContaining(title);
    }

    @Override
    public Page<Items> getItems(String authorization, int page, int size) {

        userClientService.validateToken(authorization);

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return itemRepository.findAll(pageable);
    }
}
