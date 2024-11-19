package ru.flish1.testtaskpetshop.api.store;

import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.flish1.testtaskpetshop.config.ApiProperty;
import ru.flish1.testtaskpetshop.entity.ApiResponse;
import ru.flish1.testtaskpetshop.entity.Order;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.module.jsv.JsonSchemaValidatorSettings.settings;

@Slf4j
public class TestApiDeleteOrder {
    private final String baseUrlOrderDelete = "/store/order/{orderId}";
    private final String baseUrlOrder = "/store/order";

    @BeforeEach
    public void init() {

        RestAssured.reset();
        RestAssured.baseURI = ApiProperty.getProperties("base_uri");

        JsonSchemaValidator.settings = settings()
                .with()
                .jsonSchemaFactory(
                        JsonSchemaFactory
                                .newBuilder()
                                .setValidationConfiguration(ValidationConfiguration
                                        .newBuilder()
                                        .setDefaultVersion(DRAFTV4)
                                        .freeze())
                                .freeze()).
                and().with().checkedValidation(false);

        createDeletedOrder();
    }

    @BeforeEach
    public void before(){
        createDeletedOrder();
    }

    private void createDeletedOrder() {
        Order order = Order.builder()
                .id(123L)
                .build();
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(order)
                .when()
                .post(baseUrlOrder)
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("Успешное удаление заказа")
    public void testDeleteOrderSuccessful() {
        Long orderId = 123L;
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .pathParam("orderId", orderId)
                .when()
                .delete(baseUrlOrderDelete)
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("Удаление не существующего заказа")
    public void testDeleteOrderNotFound() {
        Long orderId = 123L;
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .pathParam("orderId", orderId)
                .when()
                .delete(baseUrlOrderDelete)
                .then()
                .log().all()
                .statusCode(404);
    }
}