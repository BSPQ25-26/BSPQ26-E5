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
import com.justorder.backend.repository.AlergenRepository;
import com.justorder.backend.repository.CustomerRepository;
import com.justorder.backend.repository.RestaurantRepository;
import com.justorder.backend.repository.RiderRepository;
import com.justorder.backend.repository.LocalizationRepository;
import com.justorder.backend.repository.CuisineCategoryRepository;
import com.justorder.backend.model.Alergen;
import com.justorder.backend.model.CuisineCategory;

@Service
public class RegisterService {

	private final CustomerRepository customerRepository;
	private final RiderRepository riderRepository;
	private final RestaurantRepository restaurantRepository;
	private final AdminRepository adminRepository;
	private final PasswordEncoder passwordEncoder;
	private final CuisineCategoryRepository cuisineCategoryRepository;
	private final AlergenRepository alergenRepository;
	private final LocalizationRepository localizationRepository;

	public RegisterService(
		CustomerRepository customerRepository,
		RiderRepository riderRepository,
		RestaurantRepository restaurantRepository,
		AdminRepository adminRepository,
		PasswordEncoder passwordEncoder,
		CuisineCategoryRepository cuisineCategoryRepository,
		AlergenRepository alergenRepository,
		LocalizationRepository localizationRepository
	) {
		this.customerRepository = customerRepository;
		this.riderRepository = riderRepository;
		this.restaurantRepository = restaurantRepository;
		this.adminRepository = adminRepository;
		this.passwordEncoder = passwordEncoder;
		this.cuisineCategoryRepository = cuisineCategoryRepository;
		this.alergenRepository = alergenRepository;
		this.localizationRepository = localizationRepository;
	}
	
	@Transactional
	public Customer registerCustomer(CustomerDTO request) {
		validateDniNotInUse(request.getDni(), "customer");
		validateEmailTypo(request.getEmail());
		validateEmailNotInUse(request.getEmail(), "customer");
		validatePasswordTypo(request.getPassword());

		Customer customer = new Customer(
			request.getName(),
			normalizeEmail(request.getEmail()),
			request.getPhone(),
			passwordEncoder.encode(request.getPassword()),
			request.getAge(),
			request.getDni(),
			this.toLocalizationEntities(request.getLocalizations()),
			this.toCuisineCategoryEntities(request.getPreferenceNames()),
			this.toAlergenEntities(request.getAlergenNames())
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
		if (request.getStarterPoint() == null) {
			throw new IllegalArgumentException("Starter point is required");
		}

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
		verifyWorkingHours(request.getMondayWorkingHours());
		verifyWorkingHours(request.getTuesdayWorkingHours());
		verifyWorkingHours(request.getWednesdayWorkingHours());
		verifyWorkingHours(request.getThursdayWorkingHours());
		verifyWorkingHours(request.getFridayWorkingHours());
		verifyWorkingHours(request.getSaturdayWorkingHours());
		verifyWorkingHours(request.getSundayWorkingHours());

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
		return cuisineCategories.stream()
			.map(name -> cuisineCategoryRepository.findByName(name).get())
			.toList();
	}

	private List<Alergen> toAlergenEntities(List<String> alergenNames) {
		return alergenNames.stream()
			.map(name -> alergenRepository.findByName(name).get())
			.toList();
	}

	private void verifyWorkingHours(String workingHours) {
		if (workingHours == null || workingHours.isBlank()) {
			throw new IllegalArgumentException("Working hours is required");
		}
		// Verify format "HH:mm-HH:mm"
		String[] parts = workingHours.split("-");
		if (parts.length != 2) {
			throw new IllegalArgumentException("Working hours must be in format HH:mm-HH:mm");
		}
		for (String part : parts) {
			if (!part.matches("\\d{2}:\\d{2}")) {
				throw new IllegalArgumentException("Working hours must be in format HH:mm-HH:mm");
			}
		}
	}
}
