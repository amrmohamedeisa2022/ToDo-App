package com.example.myApp.repository;

import com.example.myApp.entity.Items;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Items, Long> {

    List<Items> findAllByTitleContaining(String title);

}
