package com.justorder.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.justorder.backend.dto.AlergenDTO;
import com.justorder.backend.model.Alergen;
import com.justorder.backend.repository.AlergenRepository;

@WebMvcTest(AlergenController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AlergenControllerTest {

    @Autowired private MockMvc mockMvc; @org.springframework.test.context.bean.override.mockito.MockitoBean private com.justorder.backend.security.JwtUtil jwtUtil;
    private ObjectMapper objectMapper = new ObjectMapper();
    @MockitoBean private AlergenRepository alergenRepository;

    @Test
    public void testGetAllAlergens() throws Exception {
        Alergen a1 = new Alergen();
        a1.setId(1L);
        a1.setName("Gluten");
        
        when(alergenRepository.findAll()).thenReturn(Arrays.asList(a1));

        mockMvc.perform(get("/api/alergens/all"))
               .andExpect(status().isOk())

               .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("Gluten")));
    }

    @Test
    public void testCreateAlergen() throws Exception {
        AlergenDTO request = new AlergenDTO();
        request.setName("Lácteos");

        Alergen guardado = new Alergen();
        guardado.setId(2L);
        guardado.setName("Lácteos");

        when(alergenRepository.save(any(Alergen.class))).thenReturn(guardado);

        mockMvc.perform(post("/api/alergens/create")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())

               .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("Lácteos")));
    }
}