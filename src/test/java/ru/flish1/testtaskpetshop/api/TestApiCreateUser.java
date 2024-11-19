package ru.flish1.testtaskpetshop.api;

import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.flish1.testtaskpetshop.config.ApiProperty;
import ru.flish1.testtaskpetshop.config.JsonSchemeProperty;
import ru.flish1.testtaskpetshop.config.TestPathJsonSchemeConfig;
import ru.flish1.testtaskpetshop.entity.ApiResponse;
import ru.flish1.testtaskpetshop.entity.User;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static io.restassured.module.jsv.JsonSchemaValidatorSettings.settings;
import static org.hamcrest.Matchers.equalToObject;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
public class TestApiCreateUser {
    private final String baseUrlUser = "/user";
    private final TestPathJsonSchemeConfig pathJsonSchemeConfig = new TestPathJsonSchemeConfig();
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
    public void testCreateUserSuccessfulWithId() {


        User user = User.builder()
                .id(14282420L)
                .userName("testUser")
                .firstName("testFirstName")
                .lastName("testLastName")
                .email("test@gmail.com")
                .phone("89604707981")
                .userStatus(-17771223)
                .build();

        ApiResponse apiResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(baseUrlUser)
                .then()
                .log().all()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath(pathJsonSchemeConfig.getPathJsonSchemeApiResponse()))
                .body("code", equalToObject(200))
                .body("type", equalToObject("unknown"))
                .body("message", equalToObject(String.valueOf(user.getId())))
                .extract()
                .as(ApiResponse.class);
        log.info(apiResponse.toString());
    }

    @Test
    public void testCreateUserSuccessfulNoId() {
        User user = User.builder()
                .id(null)
                .userName("testUser")
                .firstName("testFirstName")
                .lastName("testLastName")
                .email("test@gmail.com")
                .phone("89604707981")
                .userStatus(-17771223)
                .build();

        ApiResponse apiResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(baseUrlUser)
                .then()
                .log().all()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath(pathJsonSchemeConfig.getPathJsonSchemeApiResponse()))
                .body("code", equalToObject(200))
                .body("type", equalToObject("unknown"))
                .body("message", notNullValue())
                .extract()
                .as(ApiResponse.class);
        log.info(apiResponse.toString());
    }
}
