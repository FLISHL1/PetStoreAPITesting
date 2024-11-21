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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.flish1.testtaskpetshop.config.ApiProperty;
import ru.flish1.testtaskpetshop.config.TestPathJsonSchemeConfig;
import ru.flish1.testtaskpetshop.entity.ApiResponse;
import ru.flish1.testtaskpetshop.entity.Order;
import ru.flish1.testtaskpetshop.enums.OrderStatus;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static io.restassured.module.jsv.JsonSchemaValidatorSettings.settings;
import static org.hamcrest.Matchers.equalToObject;

@Slf4j
public class TestApiCreateOrder {
    private final String baseUrlOrder = "/store/order";
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


    @ParameterizedTest
    @ValueSource(longs = {7878787878787L})
    @DisplayName("Cоздание корректного заказа")
    public void testCreateOrderSuccessful(Long orderId) {
        Order orderRequest = Order.builder()
                .id(orderId)
                .petId(1L)
                .quantity(1)
                .shipDate("2005-01-06T15:02:51.516+0000")
                .status(OrderStatus.placed)
                .complete(true)
                .build();

        Order orderResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(orderRequest)
                .when()
                .post(baseUrlOrder)
                .then()
                .log().all()
                .body(matchesJsonSchemaInClasspath(jsonSchemeConfig.getPathJsonSchemeOrder()))
                .statusCode(200)
                .extract()
                .as(Order.class);
        Assertions.assertEquals(orderRequest, orderResponse);
        log.info(orderResponse.toString());
    }

    @Test
    @DisplayName("Cоздание не корректного заказа")
    public void testCreateOrderIncorrect() {
        Order orderRequest = Order.builder()
                .id(7878787878787L)
                .petId(1L)
                .quantity(-1)
                .shipDate("2005-01-06T15:02:51.516Z")
                .status(OrderStatus.placed)
                .complete(true)
                .build();


        ApiResponse response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(orderRequest)
                .when()
                .post(baseUrlOrder)
                .then()
                .log().all()
                .body(matchesJsonSchemaInClasspath(jsonSchemeConfig.getPathJsonSchemeOrder()))
                .statusCode(400)
                .body("code", equalToObject(400))
                .extract()
                .as(ApiResponse.class);
        log.info(response.toString());
    }
}
