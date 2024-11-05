package com.petgroomer.service;


import com.petgroomer.dto.CustomerDTO;
import com.petgroomer.exceptions.InvalidCredentialsException;
import com.petgroomer.exceptions.ResourceAlreadyExistsException;
import com.petgroomer.exceptions.ResourceNotFoundException;
import com.petgroomer.model.Customer;
import com.petgroomer.payload.LoginDTO;
import com.petgroomer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private CustomerRepository customerRepository;
    private JWTService jwtService;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, JWTService jwtService) {
        this.customerRepository = customerRepository;
        this.jwtService = jwtService;
    }

    public List<CustomerDTO> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream().map(this::convertToDTO).toList();
    }

    public CustomerDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + id));
        return convertToDTO(customer);
    }

    public CustomerDTO getCustomerByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with email " + email));
        return convertToDTO(customer);
    }

//    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
//        Customer customer = convertToEntity(customerDTO);
//        Customer savedCustomer = customerRepository.save(customer);
//        return convertToDTO(savedCustomer);
//    }

    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        Optional<Customer> existingCustomer = customerRepository.findByEmail(customerDTO.getEmail());
        if (existingCustomer.isPresent()) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }

        Customer customer =   convertToEntity(customerDTO);
        customer.setPassword(BCrypt.hashpw(customer.getPassword(), BCrypt.gensalt(10)));
        Customer savedCustomer = customerRepository.save(customer);

        CustomerDTO responseDTO = convertToDTO(savedCustomer);

        return responseDTO;
    }

    public String customerLogin(LoginDTO loginDTO) {
        Customer customer = customerRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid Email"));

        if (!BCrypt.checkpw(loginDTO.getPassword(), customer.getPassword())) {
            throw new InvalidCredentialsException("Invalid Password");
        }

        return jwtService.generateCustomerToken(customer);
    }

    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + id));
        customer.setFullName(customerDTO.getFullName());
        customer.setPhoneNumber(customerDTO.getPhoneNumber());
        customer.setEmail(customerDTO.getEmail());
        customer.setPassword(customerDTO.getPassword());
        Customer updatedCustomer = customerRepository.save(customer);
        return convertToDTO(updatedCustomer);
    }

    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found with id " + id);
        }
        customerRepository.deleteById(id);
    }

    private CustomerDTO convertToDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setFullName(customer.getFullName());
        dto.setPhoneNumber(customer.getPhoneNumber());
        dto.setEmail(customer.getEmail());
        dto.setPassword(customer.getPassword());
        dto.setRole(customer.getRole());
        return dto;
    }

    private Customer convertToEntity(CustomerDTO dto) {
        Customer customer = new Customer();
        customer.setId(dto.getId());
        customer.setFullName(dto.getFullName());
        customer.setPhoneNumber(dto.getPhoneNumber());
        customer.setEmail(dto.getEmail());
        customer.setPassword(dto.getPassword());
        customer.setRole(dto.getRole());
        return customer;
    }
}
