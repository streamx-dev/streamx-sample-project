package com.example.project.template;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Named;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Dependent
public class TemplateConfig {

  public static final String LISTING_TEMPLATE_NAME = "listing";
  public static final String PAGE_TEMPLATE_NAME = "page";

  @ConfigProperty(name = "example.templates.directory")
  String templateDirectory;

  @ApplicationScoped
  MustacheFactory mustacheFactory() {
    return new DefaultMustacheFactory();
  }

  @ApplicationScoped
  @Named(LISTING_TEMPLATE_NAME)
  Mustache listingMustache(MustacheFactory mustacheFactory) {
    return createMustache(LISTING_TEMPLATE_NAME, mustacheFactory);
  }

  @ApplicationScoped
  @Named(PAGE_TEMPLATE_NAME)
  Mustache pageMustache(MustacheFactory mustacheFactory) {
    return createMustache(PAGE_TEMPLATE_NAME, mustacheFactory);
  }

  private Mustache createMustache(String pageTemplateName, MustacheFactory mustacheFactory) {
    StringReader templateReader = new StringReader(getTemplate(pageTemplateName));

    return mustacheFactory.compile(templateReader, pageTemplateName);
  }

  private String getTemplate(String templateName) {
    String templatePath = "/" + templateName + ".html";

    return getTemplateByPath(templatePath);
  }

  private String getTemplateByPath(String templatePath) {
    try {
      return Files.readString(Paths.get(templateDirectory, templatePath));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
