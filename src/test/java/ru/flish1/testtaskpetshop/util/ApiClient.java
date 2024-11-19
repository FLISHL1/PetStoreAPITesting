package ru.flish1.testtaskpetshop.util;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import ru.flish1.testtaskpetshop.config.ApiProperty;

public class ApiClient {
    static {
        RestAssured.reset();
        RestAssured.baseURI = ApiProperty.getProperties("base_uri");
    }

    public static Response get(String endpoint){
        return RestAssured.get(endpoint);
    }

    public static Response post(String endpoint, Object o){
        return RestAssured
                .given()
                    .contentType("application/json")
                    .body(o)
                .when()
                .post(endpoint);

    }

}
