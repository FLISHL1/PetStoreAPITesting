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
import static org.hamcrest.Matchers.equalToObject;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
public class TestApiCreateUser {
    private final long correctUserId = 123L;
    private final long repeatedUserId = 1234L;
    private final int correctUserStatus = -17771223;
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
    @DisplayName("Создание пользователя с указанием id")
    public void testCreateUserSuccessfulWithId() {
        User user = getCorrectUser(correctUserId);

        ApiResponse apiResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(baseUrlUser)
                .then()
                .log().all()
                .statusCode(CodeStatus.SUCCESS.getCode())
                .body(matchesJsonSchemaInClasspath(pathJsonSchemeConfig.getPathJsonSchemeApiResponse()))
                .body("code", equalToObject(CodeStatus.SUCCESS.getCode()))
                .body("type", equalToObject("unknown"))
                .body("message", equalToObject(String.valueOf(user.getId())))
                .extract()
                .as(ApiResponse.class);
        log.info(apiResponse.toString());
    }

    private User getCorrectUser(Long userId) {
        return User.builder()
                .id(userId)
                .username("testUser")
                .firstName("testFirstName")
                .lastName("testLastName")
                .email("test@gmail.com")
                .phone("89604707981")
                .userStatus(correctUserStatus)
                .build();
    }

    private User getUserNoId() {
        return getCorrectUser(null);
    }

    @Test
    @DisplayName("Создание пользователя без указания")
    public void testCreateUserSuccessfulNoId() {
        User user = getUserNoId();

        ApiResponse apiResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(baseUrlUser)
                .then()
                .log().all()
                .statusCode(CodeStatus.SUCCESS.getCode())
                .body(matchesJsonSchemaInClasspath(pathJsonSchemeConfig.getPathJsonSchemeApiResponse()))
                .body("code", equalToObject(CodeStatus.SUCCESS.getCode()))
                .body("type", equalToObject("unknown"))
                .body("message", notNullValue())
                .extract()
                .as(ApiResponse.class);
        log.info(apiResponse.toString());
    }

    @Test
    @DisplayName("Создание существующего пользователя")
    public void testCreateRepeatableUserIncorrect() {
        User user = getCorrectUser(repeatedUserId);
        ApiResponse apiResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(baseUrlUser)
                .then()
                .log().all()
                .statusCode(CodeStatus.SUCCESS.getCode())
                .body(matchesJsonSchemaInClasspath(pathJsonSchemeConfig.getPathJsonSchemeApiResponse()))
                .body("code", equalToObject(CodeStatus.SUCCESS.getCode()))
                .body("type", equalToObject("unknown"))
                .body("message", notNullValue())
                .extract()
                .as(ApiResponse.class);
        log.info("Ответ первого запроса: {}", apiResponse.toString());

        apiResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(baseUrlUser)
                .then()
                .log().all()
                .statusCode(CodeStatus.INVALID_ID.getCode())
                .body(matchesJsonSchemaInClasspath(pathJsonSchemeConfig.getPathJsonSchemeApiResponse()))
                .body("code", equalToObject(CodeStatus.INVALID_ID.getCode()))
                .body("type", equalToObject("unknown"))
                .body("message", notNullValue())
                .extract()
                .as(ApiResponse.class);
        log.info("Ответ второго запроса: {}", apiResponse.toString());

    }
}
