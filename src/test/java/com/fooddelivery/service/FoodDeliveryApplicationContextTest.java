package com.fooddelivery.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class FoodDeliveryApplicationContextTest {

    @Test
    void applicationContextLoads() {
        // The test passes when Spring can create the application context.
    }
}
