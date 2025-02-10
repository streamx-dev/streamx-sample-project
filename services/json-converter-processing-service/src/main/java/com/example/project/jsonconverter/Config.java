package com.example.project.jsonconverter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;

@Dependent
public class Config {

  @ApplicationScoped
  ObjectMapper objectMapper() {
    return new ObjectMapper();
  }
}
