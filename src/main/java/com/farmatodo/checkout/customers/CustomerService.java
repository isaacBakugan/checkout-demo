package com.farmatodo.checkout.customers;

import com.farmatodo.checkout.customers.dto.CustomerRegisterRequest;
import com.farmatodo.checkout.customers.dto.CustomerResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.farmatodo.checkout.audit.AuditService;
@Service
public class CustomerService {

  private final CustomerRepository repo;
  private final PasswordEncoder encoder;
  private final AuditService auditService;
  public CustomerService(CustomerRepository repo, PasswordEncoder encoder, AuditService auditService) {
    this.repo = repo;
    this.encoder = encoder;
    this.auditService = auditService;
  }

  public CustomerResponse register(CustomerRegisterRequest r) {
    if (repo.existsByEmail(r.getEmail())) {
      auditService.log("customers", "signup", "Email already exist " + r.getEmail());
      throw new IllegalArgumentException("Email ya registrado");
    }
    if (repo.existsByPhone(r.getPhone())) {
      auditService.log("customers", "signup", "phone already exist " + r.getPhone());
      throw new IllegalArgumentException("Teléfono ya registrado");
    }

    Customer c = new Customer();
    c.setName(r.getName());
    c.setEmail(r.getEmail().toLowerCase());
    c.setPhone(r.getPhone());
    c.setAddress(r.getAddress());
    c.setPasswordHash(encoder.encode(r.getPassword()));

    try {
      Customer saved = repo.save(c);
      auditService.log("customers", "signup", "New User:" + saved.getName()+ "("+saved.getEmail()+")");

      return new CustomerResponse(saved.getId(), saved.getName(), saved.getEmail(),
              saved.getPhone(), saved.getAddress());
    } catch (DataIntegrityViolationException ex) {
      auditService.log("customers", "signup", "email or phone already exist");
      throw new IllegalArgumentException("Email o teléfono ya registrado");
    }
  }

  public Customer findByEmailOrThrow(String email) {
    return repo.findByEmail(email.toLowerCase())
      .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));
  }
}
