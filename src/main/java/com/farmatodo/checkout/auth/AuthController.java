package com.farmatodo.checkout.auth;

import com.farmatodo.checkout.auth.dto.LoginRequest;
import com.farmatodo.checkout.auth.dto.LoginResponse;
import com.farmatodo.checkout.customers.Customer;
import com.farmatodo.checkout.customers.CustomerService;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final CustomerService customers;
  private final PasswordEncoder encoder;

  public AuthController(CustomerService customers, PasswordEncoder encoder) {
    this.customers = customers;
    this.encoder = encoder;
  }

  @PostMapping("/login")
  public LoginResponse login(@Valid @RequestBody LoginRequest req) {
    Customer c = customers.findByEmailOrThrow(req.getEmail());
    if (!encoder.matches(req.getPassword(), c.getPasswordHash())) {
      throw new IllegalArgumentException("Credenciales inválidas");
    }
    
    return new LoginResponse(true, UUID.randomUUID().toString(), c.getName());

  }
}
