package com.justorder.backend.config;

import com.justorder.backend.model.Admin;
import com.justorder.backend.repository.AdminRepository;
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

    @Override
    public void run(String... args) throws Exception {

        if (adminRepository.count() == 0) {
            Admin superAdmin = new Admin();
            superAdmin.setName("Super Admin");
            superAdmin.setEmail("admin@justorder.com");

            superAdmin.setPassword(passwordEncoder.encode("admin123")); 
            
            adminRepository.save(superAdmin);
            System.out.println("✅ Administrador por defecto creado: admin@justorder.com / admin123");
        }
    }
}