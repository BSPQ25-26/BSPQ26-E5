package com.justorder.backend.config;

import com.justorder.backend.model.Admin;
import com.justorder.backend.model.Alergen;
import com.justorder.backend.model.CuisineCategory;
import com.justorder.backend.model.OrderStatus;
import com.justorder.backend.repository.AdminRepository;
import com.justorder.backend.repository.AlergenRepository;
import com.justorder.backend.repository.CuisineCategoryRepository;
import com.justorder.backend.repository.OrderStatusRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CuisineCategoryRepository cuisineCategoryRepository;

    @Autowired
    private AlergenRepository alergenRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Override
    public void run(String... args) throws Exception {

        // Initialización de los datos de prueba
        if (adminRepository.count() == 0) {
            Admin superAdmin = new Admin();
            superAdmin.setName("Super Admin");
            superAdmin.setEmail("admin@justorder.com");
            superAdmin.setPassword(passwordEncoder.encode("admin123")); 
            adminRepository.save(superAdmin);
        }

        // Inicialización de categorías de cocina
        if (cuisineCategoryRepository.count() == 0) {
            cuisineCategoryRepository.save(new CuisineCategory("Italian"));
            cuisineCategoryRepository.save(new CuisineCategory("Chinese"));
            cuisineCategoryRepository.save(new CuisineCategory("Mexican"));
            cuisineCategoryRepository.save(new CuisineCategory("Indian"));
            cuisineCategoryRepository.save(new CuisineCategory("Japanese"));
            cuisineCategoryRepository.save(new CuisineCategory("Mediterranean"));
        }

        // Inicialización de alérgenos
        if (alergenRepository.count() == 0) {
            alergenRepository.save(new Alergen("Gluten"));
            alergenRepository.save(new Alergen("Lactose"));
            alergenRepository.save(new Alergen("Peanuts"));
            alergenRepository.save(new Alergen("Shellfish"));
            alergenRepository.save(new Alergen("Soy"));
            alergenRepository.save(new Alergen("Eggs"));
        }

        // Inicialización de estados de pedido
        if (orderStatusRepository.count() == 0) {
            orderStatusRepository.save(new OrderStatus("Pending"));
            orderStatusRepository.save(new OrderStatus("Confirmed"));
            orderStatusRepository.save(new OrderStatus("Preparing"));
            orderStatusRepository.save(new OrderStatus("Out for Delivery"));
            orderStatusRepository.save(new OrderStatus("Delivered"));
            orderStatusRepository.save(new OrderStatus("Cancelled"));
        }
    }
}