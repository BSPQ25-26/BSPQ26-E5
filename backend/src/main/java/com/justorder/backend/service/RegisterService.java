package com.justorder.backend.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.justorder.backend.dto.CustomerDTO;
import com.justorder.backend.dto.LocalizationDTO;
import com.justorder.backend.dto.RestaurantDTO;
import com.justorder.backend.dto.RiderDTO;
import com.justorder.backend.model.Customer;
import com.justorder.backend.model.Localization;
import com.justorder.backend.model.Restaurant;
import com.justorder.backend.model.Rider;
import com.justorder.backend.repository.AdminRepository;
import com.justorder.backend.repository.CustomerRepository;
import com.justorder.backend.repository.RestaurantRepository;
import com.justorder.backend.repository.RiderRepository;
import com.justorder.backend.repository.LocalizationRepository;
import com.justorder.backend.repository.CuisineCategoryRepository;
import com.justorder.backend.model.CuisineCategory;

@Service
public class RegisterService {

	private final CustomerRepository customerRepository;
	private final RiderRepository riderRepository;
	private final RestaurantRepository restaurantRepository;
	private final AdminRepository adminRepository;
	private final PasswordEncoder passwordEncoder;
	private final CuisineCategoryRepository cuisineCategoryRepository;

	public RegisterService(
		CustomerRepository customerRepository,
		RiderRepository riderRepository,
		RestaurantRepository restaurantRepository,
		AdminRepository adminRepository,
		PasswordEncoder passwordEncoder,
		CuisineCategoryRepository cuisineCategoryRepository
	) {
		this.customerRepository = customerRepository;
		this.riderRepository = riderRepository;
		this.restaurantRepository = restaurantRepository;
		this.adminRepository = adminRepository;
		this.passwordEncoder = passwordEncoder;
		this.cuisineCategoryRepository = cuisineCategoryRepository;
	}
	@Transactional
	public Customer registerCustomer(CustomerDTO request) {
		validateDniNotInUse(request.getDni(), "customer");
		validateEmailNotInUse(request.getEmail(), "customer");
		validateEmailTypo(request.getEmail());
		validatePasswordTypo(request.getPassword());

		Customer customer = new Customer(
			request.getName(),
			normalizeEmail(request.getEmail()),
			request.getPhone(),
			passwordEncoder.encode(request.getPassword()),
			request.getAge(),
			request.getDni(),
			this.toLocalizationEntities(request.getLocalizations()),
			this.toCuisineCategoryEntities(request.getPreferenceNames())
		);
		Customer savedCustomer = customerRepository.save(customer);
		return savedCustomer;
	}

	@Transactional
	public Rider registerRider(RiderDTO request) {
		// TODO
		validateDniNotInUse(request.getDni(), "rider");
		validateEmailNotInUse(request.getEmail(), "rider");
		validateEmailTypo(request.getEmail());
		validatePasswordTypo(request.getPassword());

		Rider rider = new Rider(
			request.getName(),
			request.getDni(),
			request.getPhoneNumber(),
			normalizeEmail(request.getEmail()),
			passwordEncoder.encode(request.getPassword()),
			new Localization(request.getStarterPoint())
		);

		Rider savedRider = riderRepository.save(rider);
		return savedRider;
	}

	@Transactional
	public RestaurantDTO registerRestaurant(RestaurantDTO request) {
		// TODO
		validateEmailNotInUse(request.getEmail(), "restaurant");
		validateEmailTypo(request.getEmail());
		validatePasswordTypo(request.getPassword());

		Restaurant restaurant = new Restaurant(
			request.getName(),
			request.getDescription(),
			request.getPhone(),
			normalizeEmail(request.getEmail()),
			passwordEncoder.encode(request.getPassword()),
			request.getMondayWorkingHours(),
			request.getTuesdayWorkingHours(),
			request.getWednesdayWorkingHours(),
			request.getThursdayWorkingHours(),
			request.getFridayWorkingHours(),
			request.getSaturdayWorkingHours(),
			request.getSundayWorkingHours()
		);
		restaurant.setLocalizations(toLocalizationEntities(request.getLocalizations()));
		restaurant.setCuisineCategories(toCuisineCategoryEntities(request.getCuisineCategoryNames()));

		Restaurant savedRestaurant = restaurantRepository.save(restaurant);
		return savedRestaurant.toDTO();
	}

	private void validateEmailTypo(String email) {
		if (email == null || email.isBlank() || !email.contains("@") || !email.contains(".")) {
			throw new IllegalArgumentException("Email void or invalid");
		}
	}

	private void validatePasswordTypo(String password) {
		if (password == null || password.isBlank() || password.length() < 18) {
			throw new IllegalArgumentException("Password is required and must be at least 18 characters long");
		}
	}

	private void validateEmailNotInUse(String email, String type) {
		String normalizedEmail = normalizeEmail(email);
		HashMap<String, Boolean> emailsInUse = new HashMap<>();
		emailsInUse.put("customer", customerRepository.existsByEmail(normalizedEmail));
		emailsInUse.put("rider", riderRepository.existsByEmail(normalizedEmail));
		emailsInUse.put("restaurant", restaurantRepository.existsByEmail(normalizedEmail));

		if (emailsInUse.get(type)) {
			throw new IllegalArgumentException("Email already exist in " + type);
		}
	}

	private void validateDniNotInUse(String dni, String type) {
		HashMap<String, Boolean> dnisArray = new HashMap<>();
		dnisArray.put("customer", customerRepository.existsByDni(dni));
		dnisArray.put("rider", riderRepository.existsByDni(dni));

		if (dnisArray.get(type)) {
			throw new IllegalArgumentException("DNI already exist in " + type);
		}
	}

	private String normalizeEmail(String email) { return email.trim().toLowerCase(); }

	private List<Localization> toLocalizationEntities(List<LocalizationDTO> localizations) {
		if (localizations == null || localizations.isEmpty()) { 
            throw new IllegalArgumentException("At least one localization is required");
        }
		return localizations.stream().map(Localization::new).toList();
	}

    private List<CuisineCategory> toCuisineCategoryEntities(List<String> cuisineCategories) {
        if (cuisineCategories == null || cuisineCategories.isEmpty()) {
            throw new IllegalArgumentException("At least one cuisine category is required");
        }
		return cuisineCategories.stream()
			.map(name -> cuisineCategoryRepository.findByName(name).get())
			.toList();
	}
}
