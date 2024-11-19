package ru.flish1.testtaskpetshop.config;

public class TestPathJsonSchemeConfig {
    private final String baseDir = JsonSchemeProperty.getProperties("base_dir") + "/";


    public String getPathJsonSchemePet(){
        return baseDir + JsonSchemeProperty.getProperties("file_pet");
    }

    public String getPathJsonSchemeApiResponse(){
        return baseDir + JsonSchemeProperty.getProperties("file_api_response");
    }

    public String getPathJsonSchemeOrder() {
        return baseDir + JsonSchemeProperty.getProperties("file_order");
    }

    public String getPathJsonSchemeUser() {
        return baseDir + JsonSchemeProperty.getProperties("file_user");
    }

    public String getPathJsonSchemeInventory() {
        return baseDir + JsonSchemeProperty.getProperties("file_inventory");
    }
}
