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
import ru.flish1.testtaskpetshop.enums.CodeStatus;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static io.restassured.module.jsv.JsonSchemaValidatorSettings.settings;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
public class TestApiGetUser {
    private final String baseUrlUserGet = "/user/{username}";
    private final String baseUrlUser = "/user";
    private final String incorrectUsername = "unkno!e._)(\\wn";
    private final String correctUsername = "user1";
    private final String notFoundUsername = "unknown";
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
                .statusCode(CodeStatus.SUCCESS.getCode())
                .extract()
                .as(ApiResponse.class);
    }

    @Test
    @DisplayName("Успешное получение пользователя")
    public void testGetUserSuccessful() {
        createGetUser(correctUsername);
        User userResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .pathParam("username", correctUsername)
                .when()
                .get(baseUrlUserGet)
                .then()
                .log().all()
                .statusCode(CodeStatus.SUCCESS.getCode())
                .body(matchesJsonSchemaInClasspath(pathJsonSchemeConfig.getPathJsonSchemeUser()))
                .body("id", notNullValue())
                .extract()
                .as(User.class);
        log.info(userResponse.toString());
    }

    @Test
    @DisplayName("Получение не существующего пользователя")
    public void testGetUserNotFound() {
        ApiResponse apiResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .pathParam("username", notFoundUsername)
                .when()
                .get(baseUrlUserGet)
                .then()
                .log().all()
                .statusCode(CodeStatus.NOT_FOUND.getCode())
                .body(matchesJsonSchemaInClasspath(pathJsonSchemeConfig.getPathJsonSchemeApiResponse()))
                .extract()
                .as(ApiResponse.class);
        log.info(apiResponse.toString());
    }

    @Test
    @DisplayName("Получение пользователя с некорректным именем")
    public void testGetUserIncorrectUsername() {
        ApiResponse apiResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .pathParam("username", incorrectUsername)
                .when()
                .get(baseUrlUserGet)
                .then()
                .log().all()
                .statusCode(CodeStatus.INVALID_ID.getCode())
                .body(matchesJsonSchemaInClasspath(pathJsonSchemeConfig.getPathJsonSchemeApiResponse()))
                .extract()
                .as(ApiResponse.class);
        log.info(apiResponse.toString());
    }

}
