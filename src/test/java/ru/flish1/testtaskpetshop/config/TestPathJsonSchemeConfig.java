package ru.flish1.testtaskpetshop.config;

public class TestPathJsonSchemeConfig {
    public String getPathJsonSchemePet(){
        return JsonSchemeProperty.getProperties("base_dir") + "/" + JsonSchemeProperty.getProperties("file_pet");
    }

    public String getPathJsonSchemeApiResponse(){
        return JsonSchemeProperty.getProperties("base_dir") + "/" + JsonSchemeProperty.getProperties("file_api_response");
    }
}
