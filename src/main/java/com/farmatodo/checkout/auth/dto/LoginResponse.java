package com.farmatodo.checkout.auth.dto;

public class LoginResponse {
  private boolean authenticated;
  private String token; // dummy token para la demo

  public LoginResponse(boolean authenticated, String token) {
    this.authenticated = authenticated;
    this.token = token;
  }

  public boolean isAuthenticated() { return authenticated; }
  public String getToken() { return token; }
}
