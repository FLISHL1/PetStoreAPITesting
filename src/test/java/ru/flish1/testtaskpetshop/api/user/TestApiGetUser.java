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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.flish1.testtaskpetshop.config.ApiProperty;
import ru.flish1.testtaskpetshop.config.TestPathJsonSchemeConfig;
import ru.flish1.testtaskpetshop.entity.ApiResponse;
import ru.flish1.testtaskpetshop.entity.User;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static io.restassured.module.jsv.JsonSchemaValidatorSettings.settings;
import static org.hamcrest.Matchers.*;

@Slf4j
public class TestApiGetUser {
    private final String baseUrlUserGet = "/user/{username}";
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

    private void createGetUser(String username) {
        User user = User.builder()
                .username(username)
                .build();
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(baseUrlUser)
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .as(ApiResponse.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"user1"})
    @DisplayName("Успешное получение пользователя")
    public void testGetUserSuccessful(String username) {
        createGetUser(username);
        User userResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .pathParam("username", username)
                .when()
                .get(baseUrlUserGet)
                .then()
                .log().all()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath(pathJsonSchemeConfig.getPathJsonSchemeUser()))
                .body("id", notNullValue())
                .extract()
                .as(User.class);
        log.info(userResponse.toString());
    }

    @Test
    @DisplayName("Получение не существующего пользователя")
    public void testGetUserNotFound() {
        String username = "unknown";
        ApiResponse apiResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .pathParam("username", username)
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
    @DisplayName("Получение пользователя с некорректным именем")
    public void testGetUserIncorrectUsername() {
        String username = "unkno!e._)(\\wn";
        ApiResponse apiResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .pathParam("username", username)
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
