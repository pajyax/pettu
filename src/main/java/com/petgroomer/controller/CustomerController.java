package com.petgroomer.controller;


import com.petgroomer.dto.CustomerDTO;
import com.petgroomer.payload.LoginDTO;
import com.petgroomer.repository.CustomerRepository;
import com.petgroomer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;


    // GET ALL CUSTOMERS
    @GetMapping
    public List<CustomerDTO> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    // GET CUSTOMER BY ID
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
        try {
            CustomerDTO customerDTO = customerService.getCustomerById(id);
            return ResponseEntity.ok(customerDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET CUSTOMER BY EMAIL
    @GetMapping("/email")
    public ResponseEntity<CustomerDTO> getCustomerByEmail(@RequestParam String email) {
        try {
            CustomerDTO customerDTO = customerService.getCustomerByEmail(email);
            return ResponseEntity.ok(customerDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Register Customer
    @PostMapping("/customer-signup")
    public ResponseEntity<?> createCustomer(@RequestBody CustomerDTO customerDTO) {
        if (customerRepository.existsByEmail(customerDTO.getEmail())) {
            return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
        }
        CustomerDTO createdCustomerDTO = customerService.createCustomer(customerDTO);

        return new ResponseEntity<>(createdCustomerDTO, HttpStatus.CREATED);
    }

    // Login Customer
    @PostMapping("/customer-login")
    public ResponseEntity<?> customerLogin(@RequestBody LoginDTO loginDTO) {
        String token = customerService.customerLogin(loginDTO);
        if (token != null) {
            return ResponseEntity.ok(token);
        }
        return new ResponseEntity<>("Invalid username/password", HttpStatus.UNAUTHORIZED);
    }

    // UPDATE CUSTOMER
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long id, @RequestBody CustomerDTO customerDTO) {
        try {
            CustomerDTO updatedCustomer = customerService.updateCustomer(id, customerDTO);
            return ResponseEntity.ok(updatedCustomer);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE CUSTOMER
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
