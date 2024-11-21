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
import ru.flish1.testtaskpetshop.entity.Order;
import ru.flish1.testtaskpetshop.enums.CodeStatus;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.module.jsv.JsonSchemaValidatorSettings.settings;

@Slf4j
public class TestApiDeleteOrder {
    private final long correctOrderId = 123L;
    private final long incorrectOrderId = -123L;
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

    }

    private void createDeletedOrder(long orderId) {
        Order order = Order.builder()
                .id(orderId)
                .build();
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(order)
                .when()
                .post(baseUrlOrder)
                .then()
                .log().all()
                .statusCode(CodeStatus.SUCCESS.getCode());
    }

    @Test
    @DisplayName("Успешное удаление заказа")
    public void testDeleteOrderSuccessful() {
        createDeletedOrder(correctOrderId);
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .pathParam("orderId", correctOrderId)
                .when()
                .delete(baseUrlOrderDelete)
                .then()
                .log().all()
                .statusCode(CodeStatus.SUCCESS.getCode());
        createDeletedOrder(correctOrderId);
    }

    @Test
    @DisplayName("Удаление не существующего заказа")
    public void testDeleteOrderNotFound() {
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .pathParam("orderId", incorrectOrderId)
                .when()
                .delete(baseUrlOrderDelete)
                .then()
                .log().all()
                .statusCode(CodeStatus.NOT_FOUND.getCode());
    }
}
