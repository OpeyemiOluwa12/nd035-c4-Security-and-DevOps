package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    ItemController itemController;

    ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup() {
        itemController = new ItemController();
        TestUtils.injectObject(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void verifyGetItemsById() {

        List<Item> items = createItems();

        when(itemRepository.findById(0L)).thenReturn(java.util.Optional.ofNullable(items.get(0)));

        ResponseEntity<Item> itemListResponseEntity = itemController.getItemById(0L);


        assertNotNull(itemListResponseEntity);
        assertEquals(200, itemListResponseEntity.getStatusCodeValue());

        Item itemFound = itemListResponseEntity.getBody();
        assertNotNull(itemFound);
        assertEquals("Round Widget", itemFound.getName());
        assertEquals("A widget that is round", itemFound.getDescription());
        assertEquals(BigDecimal.valueOf(2.99), itemFound.getPrice());


    }

    @Test
    public void verifyGetItemsByName() {

        List<Item> items = createItems();
        List<Item> itemsByName = new ArrayList<>(Collections.singletonList(items.get(0)));

        when(itemRepository.findByName("Round Widget")).thenReturn(itemsByName);

        ResponseEntity<List<Item>> itemListResponseEntity = itemController.getItemsByName("Round Widget");


        assertNotNull(itemListResponseEntity);
        assertEquals(200, itemListResponseEntity.getStatusCodeValue());

        List<Item> itemsFound = itemListResponseEntity.getBody();
        assertNotNull(itemsFound);
        assertEquals(1, itemsFound.size());
        assertEquals("A widget that is round", itemsFound.get(0).getDescription());


    }

    @Test
    public void verifyFindAllItems() {

        List<Item> items = createItems();

        when(itemRepository.findAll()).thenReturn(items);


        ResponseEntity<List<Item>> itemListResponseEntity = itemController.getItems();


        assertNotNull(itemListResponseEntity);
        assertEquals(200, itemListResponseEntity.getStatusCodeValue());

        List<Item> itemsFound = itemListResponseEntity.getBody();
        assertNotNull(itemsFound);
        assertEquals(2, itemsFound.size());
        assertEquals("A widget that is round", itemsFound.get(0).getDescription());

    }

    private List<Item> createItems() {
        Item item = new Item(0L, "Round Widget", BigDecimal.valueOf(2.99), "A widget that is round");
        Item item2 = new Item(1L, "Square Widget", BigDecimal.valueOf(1.99), "A widget that is square");


        itemRepository.save(item);
        itemRepository.save(item2);
        return new ArrayList<>(Arrays.asList(item, item2));
    }
}
