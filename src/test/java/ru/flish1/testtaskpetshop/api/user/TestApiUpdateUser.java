package ru.flish1.testtaskpetshop.api.user;

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
import ru.flish1.testtaskpetshop.entity.User;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static io.restassured.module.jsv.JsonSchemaValidatorSettings.settings;

@Slf4j
public class TestApiUpdateUser {
    private final String baseUrlUserGet = "/user/{username}";
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
    @DisplayName("Успешное обновление пользователя")
    public void testUpdateUserSuccessful() {
        String username = "user1";

        User user = User.builder()
                .id(1L)
                .username("user1")
                .firstName("testFirstName")
                .lastName("testLastName")
                .email("test@gmail.com")
                .password("testPassword")
                .phone("89604707981")
                .userStatus(-17771223)
                .build();

        ApiResponse apiResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .pathParam("username", username)
                .body(user)
                .when()
                .put(baseUrlUserGet)
                .then()
                .log().all()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath(pathJsonSchemeConfig.getPathJsonSchemeApiResponse()))
                .extract()
                .as(ApiResponse.class);
        log.info(apiResponse.toString());
    }

    @Test
    @DisplayName("Обновление не существующего пользователя")
    public void testGetUserNotFound() {
        String username = "unknown";

        User user = User.builder()
                .id(1L)
                .username("testUser")
                .firstName("testFirstName")
                .lastName("testLastName")
                .email("test@gmail.com")
                .password("testPassword")
                .phone("89604707981")
                .userStatus(-17771223)
                .build();

        ApiResponse apiResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .pathParam("username", username)
                .body(user)
                .when()
                .get(baseUrlUserGet)
                .then()
                .log().all()
                .statusCode(404)
                .body(matchesJsonSchemaInClasspath(pathJsonSchemeConfig.getPathJsonSchemeApiResponse()))
                .extract()
                .as(ApiResponse.class);
        log.info(apiResponse.toString());
    }

    @Test
    @DisplayName("Обновление пользователя с некорректным именем")
    public void testGetUserIncorrectUsername() {
        String username = "unkno!e._)(\\wn";
        User user = User.builder()
                .id(1L)
                .username("testUser")
                .firstName("testFirstName")
                .lastName("testLastName")
                .email("test@gmail.com")
                .password("testPassword")
                .phone("89604707981")
                .userStatus(-17771223)
                .build();

        ApiResponse apiResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .pathParam("username", username)
                .body(user)
                .when()
                .get(baseUrlUserGet)
                .then()
                .log().all()
                .statusCode(400)
                .body(matchesJsonSchemaInClasspath(pathJsonSchemeConfig.getPathJsonSchemeApiResponse()))
                .extract()
                .as(ApiResponse.class);
        log.info(apiResponse.toString());
    }

}
