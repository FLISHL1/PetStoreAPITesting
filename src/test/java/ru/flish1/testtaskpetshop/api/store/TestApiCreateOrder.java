package ru.flish1.testtaskpetshop.api.store;

import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.flish1.testtaskpetshop.config.ApiProperty;
import ru.flish1.testtaskpetshop.config.TestPathJsonSchemeConfig;
import ru.flish1.testtaskpetshop.entity.ApiResponse;
import ru.flish1.testtaskpetshop.entity.Order;
import ru.flish1.testtaskpetshop.enums.CodeStatus;
import ru.flish1.testtaskpetshop.enums.OrderStatus;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static io.restassured.module.jsv.JsonSchemaValidatorSettings.settings;
import static org.hamcrest.Matchers.equalToObject;

@Slf4j
public class TestApiCreateOrder {
    private final String shipDate = "2005-01-06T15:02:51.516+0000";
    private final long petId = 1L;
    private final int quantity = 1;
    private final String baseUrlOrder = "/store/order";
    private final long incorrectOrderId = -123L;
    private final long correctOrderId = 123L;
    private final TestPathJsonSchemeConfig jsonSchemeConfig = new TestPathJsonSchemeConfig();
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


    private Order getOrder(long correctOrderId, long petId, int quantity, String shipDate) {
        return Order.builder()
                .id(correctOrderId)
                .petId(petId)
                .quantity(quantity)
                .shipDate(shipDate)
                .status(OrderStatus.placed)
                .complete(true)
                .build();
    }

    @Test
    @DisplayName("Cоздание корректного заказа")
    public void testCreateOrderSuccessful() {
        Order orderRequest = getOrder(correctOrderId, petId, quantity, shipDate);

        Order orderResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(orderRequest)
                .when()
                .post(baseUrlOrder)
                .then()
                .log().all()
                .body(matchesJsonSchemaInClasspath(jsonSchemeConfig.getPathJsonSchemeOrder()))
                .statusCode(CodeStatus.SUCCESS.getCode())
                .extract()
                .as(Order.class);
        Assertions.assertEquals(orderRequest, orderResponse);
        log.info(orderResponse.toString());
    }

    @Test
    @DisplayName("Cоздание не корректного заказа")
    public void testCreateOrderIncorrect() {
        Order orderRequest = getIncorrectOrder();


        ApiResponse response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(orderRequest)
                .when()
                .post(baseUrlOrder)
                .then()
                .log().all()
                .body(matchesJsonSchemaInClasspath(jsonSchemeConfig.getPathJsonSchemeOrder()))
                .statusCode(CodeStatus.INVALID_ID.getCode())
                .body("code", equalToObject(CodeStatus.INVALID_ID.getCode()))
                .extract()
                .as(ApiResponse.class);
        log.info(response.toString());
    }

    private Order getIncorrectOrder() {
        return getOrder(correctOrderId, petId, -quantity, shipDate);
    }
}
