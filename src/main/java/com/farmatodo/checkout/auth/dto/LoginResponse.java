package com.farmatodo.checkout.auth.dto;

public class LoginResponse {
    private boolean authenticated;
    private String token;
    private String name;


  public LoginResponse(boolean authenticated, String token, String name) {
    this.authenticated = authenticated;
    this.token = token;
    this.name = name;
  }

  public boolean isAuthenticated() { return authenticated; }
  public String getToken() { return token; }
  public String getName() { return name; }
}
