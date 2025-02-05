package com.example.project.template;

import jakarta.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.util.io.Streams;

@ApplicationScoped
class TemplateRepository {

  String getTemplate(String templateName) {
    String templatePath = "/templates/" + templateName + ".html";

    return getTemplateByPath(templatePath);
  }

  private String getTemplateByPath(String templatePath) {
    try {
      InputStream resource = TemplateRepository.class.getResourceAsStream(templatePath);
      return new String(Streams.readAll(resource));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
