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
import ru.flish1.testtaskpetshop.config.TestPathJsonSchemeConfig;
import ru.flish1.testtaskpetshop.entity.ApiResponse;
import ru.flish1.testtaskpetshop.entity.Order;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static io.restassured.module.jsv.JsonSchemaValidatorSettings.settings;
import static org.hamcrest.Matchers.equalToObject;

@Slf4j
public class TestApiGetOrder {
    private final String baseUrlOrder = "/store/order/{orderId}";
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

    @Test
    @DisplayName("Успешное получение заказа")
    public void testGetOrderSuccessful() {
        Long orderId = 10L;
        Order orderResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .pathParam("orderId", orderId)
                .when()
                .get(baseUrlOrder)
                .then()
                .log().all()
                .body(matchesJsonSchemaInClasspath(jsonSchemeConfig.getPathJsonSchemeOrder()))
                .statusCode(200)
                .body("id", equalToObject(orderId.intValue()))
                .extract()
                .as(Order.class);
        log.info(orderResponse.toString());
    }

    @Test
    @DisplayName("Получение не существующего заказа")
    public void testGetOrderIncorrect() {
        Long orderId = 21434325314321L;


        ApiResponse response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .pathParam("orderId", orderId)
                .when()
                .get(baseUrlOrder)
                .then()
                .log().all()
                .body(matchesJsonSchemaInClasspath(jsonSchemeConfig.getPathJsonSchemeApiResponse()))
                .statusCode(404)
                .extract()
                .as(ApiResponse.class);
        log.info(response.toString());
    }

}
