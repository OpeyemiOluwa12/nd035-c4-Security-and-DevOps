package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    OrderController orderController;
    UserRepository userRepository = mock(UserRepository.class);
    OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObject(orderController, "userRepository", userRepository);
        TestUtils.injectObject(orderController, "orderRepository", orderRepository);
    }

    @Test()
    public void verifySubmit() {

        User user = createUser();

        Item item = items();

        Cart cart = user.getCart();
        cart.addItem(item);
        cart.setUser(user);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        ResponseEntity<UserOrder> submitOrderResponseEntity = orderController.submit(user.getUsername());

        assertNotNull(submitOrderResponseEntity);
        assertEquals(200, submitOrderResponseEntity.getStatusCodeValue());

        UserOrder userOrderFound = submitOrderResponseEntity.getBody();

        assertNotNull(userOrderFound);

        assertEquals(item.getId(), userOrderFound.getItems().get(0).getId());
        assertEquals(user.getUsername(), userOrderFound.getUser().getUsername());

    }

    @Test()
    public void verifyGetOrdersForUsers() {
        User user = createUser();
        Item item = items();

        UserOrder userOrder = new UserOrder();
        userOrder.setItems(Collections.singletonList(item));
        userOrder.setTotal(BigDecimal.valueOf(2.99));
        userOrder.setUser(user);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(Collections.singletonList(userOrder));

        ResponseEntity<List<UserOrder>> ordersForUser = orderController.getOrdersForUser(user.getUsername());

        assertNotNull(ordersForUser);
        assertEquals(200, ordersForUser.getStatusCodeValue());

        List<UserOrder> userOrderFound = ordersForUser.getBody();

        assertNotNull(userOrderFound);
        assertEquals(1, userOrderFound.size());


    }


    private User createUser() {
        return new User(0L, "Opeyemi", "hashedId", new Cart());
    }

    private Item items() {
        return new Item(0L, "Round Widget", BigDecimal.valueOf(2.99), "A widget that is round");
    }

}
