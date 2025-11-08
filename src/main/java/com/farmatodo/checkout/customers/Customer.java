package com.farmatodo.checkout.customers;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "customers",
       uniqueConstraints = {
         @UniqueConstraint(name="uk_customers_email", columnNames = "email"),
         @UniqueConstraint(name="uk_customers_phone", columnNames = "phone")
       })
public class Customer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false) private String name;
  @Column(nullable = false) private String email;
  @Column(nullable = false) private String phone;
  @Column(nullable = false) private String address;

  @Column(nullable = false) private String passwordHash;

  @Column(nullable = false, updatable = false)
  private Instant createdAt = Instant.now();

  // getters/setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getPhone() { return phone; }
  public void setPhone(String phone) { this.phone = phone; }
  public String getAddress() { return address; }
  public void setAddress(String address) { this.address = address; }
  public String getPasswordHash() { return passwordHash; }
  public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
