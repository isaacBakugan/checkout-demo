package com.farmatodo.checkout.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import com.farmatodo.checkout.tokenization.TokenizationService.TokenizationRejectedException;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleValidation(MethodArgumentNotValidException ex) {
    var errors = ex.getBindingResult().getFieldErrors().stream()
        .collect(Collectors.toMap(e -> e.getField(), e -> e.getDefaultMessage(), (a, b) -> a));
    return Map.of("message", "Validación fallida", "errors", errors);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleIllegalArgument(IllegalArgumentException ex) {
    return Map.of("message", ex.getMessage());
  }

  @ExceptionHandler(TokenizationRejectedException.class)
  public ResponseEntity<?> handleReject(TokenizationRejectedException ex) {
    return ResponseEntity.status(422).body(Map.of(
        "error", "TOKENIZATION_REJECTED",
        "message", ex.getMessage()));
  }
}
