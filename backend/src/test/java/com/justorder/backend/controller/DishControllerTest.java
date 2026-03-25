package com.justorder.backend.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.justorder.backend.dto.DishDTO;
import com.justorder.backend.repository.AllergenRepository;
import com.justorder.backend.repository.DishRepository;
import com.justorder.backend.repository.RestaurantRepository;
import com.justorder.backend.security.JwtUtil;
import com.justorder.backend.service.MenuService;

@WebMvcTest(DishController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private MenuService menuService;

    @MockitoBean
    private DishRepository dishRepository;
    
    @MockitoBean
    private RestaurantRepository restaurantRepository;
    
    @MockitoBean
    private AllergenRepository allergenRepository;

    @Test
    public void testGetAll() throws Exception {

        DishDTO dto = new DishDTO();
        dto.setName("Macarrones");
        
        when(menuService.getAllDishes()).thenReturn(Arrays.asList(dto));

        mockMvc.perform(get("/api/dishes"))
               .andExpect(status().isOk())
               .andExpect(result -> {
                   String content = result.getResponse().getContentAsString();
                   assertTrue(content.contains("Macarrones"));
               });
    }

    @Test
    public void testCreate() throws Exception {
        DishDTO requestDto = new DishDTO();
        requestDto.setName("Sopa");
        requestDto.setPrice(5.0);

        DishDTO responseDto = new DishDTO();
        responseDto.setName("Sopa");

        when(menuService.createDish(eq(1L), any(DishDTO.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/dishes/1")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(requestDto)))
               .andExpect(status().isCreated());
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete("/api/dishes/1"))
               .andExpect(status().isNoContent()); 
    }

    @Test
    public void testCreateDishWithInvalidAllergen() throws Exception {
        DishDTO badDish = new DishDTO();
        badDish.setName("Plato Invalido");
        badDish.setAllergenNames(Arrays.asList("UnknownAllergen"));

        when(menuService.createDish(eq(1L), any(DishDTO.class)))
            .thenThrow(new com.justorder.backend.exception.InvalidDishDataException("Allergen not found"));

        mockMvc.perform(post("/api/dishes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badDish)))
                .andExpect(status().isBadRequest());
    }
}