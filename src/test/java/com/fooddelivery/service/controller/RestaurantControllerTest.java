package com.fooddelivery.service.controller;

import com.fooddelivery.service.controller.RestaurantController;
import com.fooddelivery.service.entity.Restaurant;
import com.fooddelivery.service.service.RestaurantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestaurantController.class)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RestaurantService restaurantService;

    @Test
    void getRestaurantsReturnsRestaurantList() throws Exception {
        given(restaurantService.getRestaurants()).willReturn(
                List.of(new Restaurant("Anand Food Corner", "Lucknow", "Indian"))
        );

        mockMvc.perform(get("/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Anand Food Corner"))
                .andExpect(jsonPath("$[0].location").value("Lucknow"))
                .andExpect(jsonPath("$[0].cuisine").value("Indian"));
    }
}
