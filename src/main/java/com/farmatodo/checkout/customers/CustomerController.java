package com.farmatodo.checkout.customers;

import com.farmatodo.checkout.customers.dto.CustomerRegisterRequest;
import com.farmatodo.checkout.customers.dto.CustomerResponse;
import jakarta.validation.Valid;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomerController {

  private final CustomerService service;

  public CustomerController(CustomerService service) {
    this.service = service;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CustomerResponse register(@Valid @RequestBody CustomerRegisterRequest request) {
    // ejemplo de MDC si quieres correlación por UUID en logs
    MDC.put("txId", java.util.UUID.randomUUID().toString());
    try {
      return service.register(request);
    } finally {
      MDC.clear();
    }
  }
}
