package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    CartController cartController;

    UserRepository userRepository = mock(UserRepository.class);
    CartRepository cartRepository = mock(CartRepository.class);
    ItemRepository itemRepository = mock(ItemRepository.class);


    @Before
    public void setup() {
        cartController = new CartController();
        TestUtils.injectObject(cartController, "userRepository", userRepository);
        TestUtils.injectObject(cartController, "itemRepository", itemRepository);
        TestUtils.injectObject(cartController, "cartRepository", cartRepository);
    }


    @Test
    public void verifyAddToCart() {

        // Create Item
        Item item = items();

        //Create user
        User user = createUser();

        //Create modify cart request
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest(user.getUsername(), item.getId(), 2);

        Cart cart = user.getCart();

        //Add each item to cart
        IntStream.range(0, modifyCartRequest.getQuantity())
                .forEach(i -> cart.addItem(item));

        user.setCart(cart);
        cart.setUser(user);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(itemRepository.findById(item.getId())).thenReturn(java.util.Optional.of(item));


        ResponseEntity<Cart> cartResponseEntity = cartController.addToCart(modifyCartRequest);
        assertNotNull(cartResponseEntity);
        assertEquals(200, cartResponseEntity.getStatusCodeValue());

        assertNotNull(cartResponseEntity.getBody());

        Cart cartFound = cartResponseEntity.getBody();

        assertEquals(cart.getItems().size(), cartFound.getItems().size());
        assertEquals(user.getUsername(), cartFound.getUser().getUsername());


//        //User Not Found
//        ModifyCartRequest modifyCartRequest2 = new ModifyCartRequest("Adeolu", item.getId(), 2);


    }

    @Test
    public void verifyRemoveFromCart() {

        // Create Item
        Item item = items();

        //Create user
        User user = createUser();

        //Create modify cart request
        ModifyCartRequest cartToAddRequest = new ModifyCartRequest(user.getUsername(), item.getId(), 2);

        Cart userCart = user.getCart();

        //Add each item to cart

        IntStream.range(0, cartToAddRequest.getQuantity())
                .forEach(i -> userCart.addItem(item));

        user.setCart(userCart);
        userCart.setUser(user);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(itemRepository.findById(item.getId())).thenReturn(java.util.Optional.of(item));

        //Create cart to be removed
        ModifyCartRequest cartToRemove = new ModifyCartRequest(user.getUsername(), item.getId(), 1);

        ResponseEntity<Cart> cartResponseEntity = cartController.removeFromCart(cartToRemove);
        assertNotNull(cartResponseEntity);
        assertEquals(200, cartResponseEntity.getStatusCodeValue());

        assertNotNull(cartResponseEntity.getBody());

        Cart cartFound = cartResponseEntity.getBody();

        assertEquals(1, cartFound.getItems().size());
        assertEquals(user.getUsername(), cartFound.getUser().getUsername());
    }


    private User createUser() {
        return new User(0L, "Opeyemi", "hashedId", new Cart());
    }

    private Item items() {
        Item item = new Item(0L, "Round Widget", BigDecimal.valueOf(2.99), "A widget that is round");

        itemRepository.save(item);
        return item;
    }
}
