package ru.flish1.testtaskpetshop.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class JsonSchemeProperty {
    private static Properties PROPERTIES;


    static {
        try(FileInputStream fileInputStream = new FileInputStream("src/test/resources/jsonScheme.properties")){
            PROPERTIES = new Properties();
            PROPERTIES.load(fileInputStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getProperties(String key) {
        return PROPERTIES.getProperty(key);
    }
}
