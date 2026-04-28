package com.justorder.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.justorder.backend.dto.CustomerDTO;
import com.justorder.backend.model.Customer;
import com.justorder.backend.repository.CustomerRepository;
import com.justorder.backend.repository.OrderRepository;
import com.justorder.backend.security.JwtUtil;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomerRepository repository;

    @MockitoBean
    private OrderRepository orderRepository;

    // ==========================================
    // TESTS DE LA RAMA 'HEAD' (CRUD BÁSICO)
    // ==========================================

    @Test
    void testGetAll() throws Exception {
        Customer c = new Customer();
        c.setId(1L);
        c.setName("Juan Perez");
        
        when(repository.findAll()).thenReturn(Arrays.asList(c));

        mockMvc.perform(get("/api/customers"))
               .andExpect(status().isOk())
               .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("Juan Perez")));
    }

    @Test
    void testUpdate() throws Exception {
        CustomerDTO request = new CustomerDTO();
        request.setName("Ana Modificada");

        Customer existing = new Customer();
        existing.setId(2L);
        existing.setName("Ana Gomez");

        Customer updated = new Customer();
        updated.setId(2L);
        updated.setName("Ana Modificada");

        when(repository.findById(2L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Customer.class))).thenReturn(updated);

        mockMvc.perform(put("/api/customers/2")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("Ana Modificada")));
    }

    @Test
    void testDelete() throws Exception {
        when(repository.existsById(1L)).thenReturn(true);
        mockMvc.perform(delete("/api/customers/1"))
               .andExpect(status().isOk());
    }

    // ==========================================
    // TESTS DE LA RAMA 'MAIN' (VALIDACIONES EXHAUSTIVAS)
    // ==========================================

    @Test
    void testRegisterCustomer() throws Exception {
        Customer saved = new Customer();
        saved.setId(1L);
        saved.setName("John Doe Test");
        when(repository.save(any(Customer.class))).thenReturn(saved);

        String requestBody = """
            {
                "name": "John Doe Test",
                "email": "johndoTeste@example.com",
                "phone": "600123456",
                "password": "supersecurepassword123",
                "age": 30,
                "dni": "12345678ATest",
                "localizations": [
                    {
                        "city": "Bilbao",
                        "province": "Bizkaia",
                        "country": "Spain",
                        "postalCode": "48001",
                        "number": "5",
                        "longitude": -2.9253,
                        "latitude": 43.2630
                    }
                ],
                "allergenNames": ["Gluten", "Lactose"],
                "preferenceNames": ["Italian", "Japanese"]
            }
            """;
        mockMvc.perform(post("/api/customers/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk());
    }

    @Test
    void testRegisterHomelessCustomer() throws Exception {
        String requestBody = """
            {
                "name": "Homeless John Doe",
                "email": "johndoe@example.com",
                "phone": "600123456",
                "password": "supersecurepassword123",
                "age": 30,
                "dni": "12345678A",
                "localizations": [
                ],
                "allergenNames": ["Gluten", "Lactose"],
                "preferenceNames": ["Italian", "Japanese"]
            }
            """;
        mockMvc.perform(post("/api/customers/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void testRegisterFalseEmailCustomer() throws Exception {
        String requestBody = """
            {
                "name": "John Doe",
                "email": "johndoeexample.com",
                "phone": "600123456",
                "password": "supersecurepassword123",
                "age": 30,
                "dni": "12345678A",
                "localizations": [
                    {
                        "city": "Bilbao",
                        "province": "Bizkaia",
                        "country": "Spain",
                        "postalCode": "48001",
                        "number": "5",
                        "longitude": -2.9253,
                        "latitude": 43.2630
                    }
                ],
                "allergenNames": ["Gluten", "Lactose"],
                "preferenceNames": ["Italian", "Japanese"]
            }
            """;
        mockMvc.perform(post("/api/customers/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void testRegisterShortPasswordCustomer() throws Exception {
        String requestBody = """
            {
                "name": "John Doe",
                "email": "johndoeexample.com",
                "phone": "600123456",
                "password": "suword123",
                "age": 30,
                "dni": "12345678A",
                "localizations": [
                    {
                        "city": "Bilbao",
                        "province": "Bizkaia",
                        "country": "Spain",
                        "postalCode": "48001",
                        "number": "5",
                        "longitude": -2.9253,
                        "latitude": 43.2630
                    }
                ],
                "allergenNames": ["Gluten", "Lactose"],
                "preferenceNames": ["Italian", "Japanese"]
            }
            """;
        mockMvc.perform(post("/api/customers/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void testRegisterVoidCustomer() throws Exception {
        String requestBody = """
            {
            }
            """;
        mockMvc.perform(post("/api/customers/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void testRegisterNoAllergenCustomer() throws Exception {
        Customer saved = new Customer();
        saved.setId(2L);
        when(repository.save(any(Customer.class))).thenReturn(saved);

        String requestBody = """
            {
                "name": "John Doe Non Allergen",
                "email": "olik@example.com",
                "phone": "600123456",
                "password": "supersecurepassword123",
                "age": 30,
                "dni": "12345asd678A",
                "localizations": [
                    {
                        "city": "Bilbao",
                        "province": "Bizkaia",
                        "country": "Spain",
                        "postalCode": "48001",
                        "number": "5",
                        "longitude": -2.9253,
                        "latitude": 43.2630
                    }
                ],
                "allergenNames": [],
                "preferenceNames": ["Italian", "Japanese"]
            }
            """;
        mockMvc.perform(post("/api/customers/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk());
    }
    
    @Test
    void testRegisterNoPreferenceCustomer() throws Exception {
        Customer saved = new Customer();
        saved.setId(3L);
        when(repository.save(any(Customer.class))).thenReturn(saved);

        String requestBody = """
            {
                "name": "John Doe No Preference",
                "email": "johnadgdoe@example.com",
                "phone": "600123456",
                "password": "supersecurepassword123",
                "age": 30,
                "dni": "12345kiik678A",
                "localizations": [
                    {
                        "city": "Bilbao",
                        "province": "Bizkaia",
                        "country": "Spain",
                        "postalCode": "48001",
                        "number": "5",
                        "longitude": -2.9253,
                        "latitude": 43.2630
                    }
                ],
                "allergenNames": ["Gluten", "Lactose"],
                "preferenceNames": []
            }
            """;
        mockMvc.perform(post("/api/customers/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk());
    }

    @Test
    void testDeleteAllCustomer() throws Exception {
        mockMvc.perform(delete("/api/customers"))
            .andExpect(status().isOk());
    }

    @Test
    void testGetCustomerDashboard() throws Exception {
        // Create a mock Customer object
        Customer mockCustomer = new Customer();
        mockCustomer.setId(1L);
        mockCustomer.setName("Test Customer");
        
        // Configure the mocks to return proper values
        when(repository.findById(1L)).thenReturn(Optional.of(mockCustomer));
        when(orderRepository.findByCustomerId(1L)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/customers/1/dashboard"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.customerId").value(1))
            .andExpect(jsonPath("$.customerName").value("Test Customer"))
            .andExpect(jsonPath("$.totalOrders").value(greaterThanOrEqualTo(0)))
            .andExpect(jsonPath("$.activeOrders").value(greaterThanOrEqualTo(0)))
            .andExpect(jsonPath("$.cancelledOrders").value(greaterThanOrEqualTo(0)))
            .andExpect(jsonPath("$.deliveredOrders").value(greaterThanOrEqualTo(0)))
            .andExpect(jsonPath("$.totalSpent").isNumber())
            .andExpect(jsonPath("$.totalRefunded").isNumber())
            .andExpect(jsonPath("$.recentOrders").isArray());
    }

    @Test
    void testGetCustomerDashboardNotFound() throws Exception {
        when(repository.existsById(999999L)).thenReturn(false);
        mockMvc.perform(get("/api/customers/999999/dashboard"))
            .andExpect(status().isNotFound());
    }
}