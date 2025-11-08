package com.farmatodo.checkout.customers.dto;

import jakarta.validation.constraints.*;

public class CustomerRegisterRequest {
  @NotBlank @Size(min=2, max=100)
  private String name;

  @NotBlank @Email @Size(max=200)
  private String email;

  @NotBlank @Size(min=7, max=30)
  private String phone;

  @NotBlank @Size(min=5, max=200)
  private String address;

  @NotBlank @Size(min=6, max=100)
  private String password;

  // getters/setters
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getPhone() { return phone; }
  public void setPhone(String phone) { this.phone = phone; }
  public String getAddress() { return address; }
  public void setAddress(String address) { this.address = address; }
  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }
}
