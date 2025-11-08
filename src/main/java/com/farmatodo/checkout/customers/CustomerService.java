package com.farmatodo.checkout.customers;

import com.farmatodo.checkout.customers.dto.CustomerRegisterRequest;
import com.farmatodo.checkout.customers.dto.CustomerResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

  private final CustomerRepository repo;
  private final PasswordEncoder encoder;

  public CustomerService(CustomerRepository repo, PasswordEncoder encoder) {
    this.repo = repo;
    this.encoder = encoder;
  }

  public CustomerResponse register(CustomerRegisterRequest r) {
    if (repo.existsByEmail(r.getEmail()))
      throw new IllegalArgumentException("Email ya registrado");
    if (repo.existsByPhone(r.getPhone()))
      throw new IllegalArgumentException("Teléfono ya registrado");

    Customer c = new Customer();
    c.setName(r.getName());
    c.setEmail(r.getEmail().toLowerCase());
    c.setPhone(r.getPhone());
    c.setAddress(r.getAddress());
    c.setPasswordHash(encoder.encode(r.getPassword()));

    try {
      Customer saved = repo.save(c);
      return new CustomerResponse(saved.getId(), saved.getName(), saved.getEmail(),
              saved.getPhone(), saved.getAddress());
    } catch (DataIntegrityViolationException ex) {
      // por si acaso se cuela una carrera y lo pilla el unique constraint
      throw new IllegalArgumentException("Email o teléfono ya registrado");
    }
  }

  public Customer findByEmailOrThrow(String email) {
    return repo.findByEmail(email.toLowerCase())
      .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));
  }
}
