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
public class TestApiCreateUserWithArray {
    private final String baseUrlUserArray = "/user/createWithArray";
    private final Long correctUserId2 = 1234L;
    private final long correctUserId1 = 123L;
    private final int correctUserStatus = -17771223;
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

    @Test
    @DisplayName("Создание пользователей через массив")
    public void testCreateUsersSuccessful() {
        User user1 = getCorrectUser(correctUserId1);
        User user2 = getCorrectUser(correctUserId2);
        User[] userArray = {user2, user1};
        ApiResponse apiResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(userArray)
                .when()
                .post(baseUrlUserArray)
                .then()
                .log().all()
                .statusCode(CodeStatus.SUCCESS.getCode())
                .body(matchesJsonSchemaInClasspath(pathJsonSchemeConfig.getPathJsonSchemeApiResponse()))
                .body("code", equalToObject(CodeStatus.SUCCESS.getCode()))
                .body("type", equalToObject("unknown"))
                .body("message", equalToObject("ok"))
                .extract()
                .as(ApiResponse.class);
        log.info(apiResponse.toString());
    }

    @Test
    @DisplayName("Создание повторяющихся пользователей через массив")
    public void testCreateRepeatableUsersIncorrect() {
        User user1 = getCorrectUser(correctUserId1);
        User[] userArray = {user1, user1};
        ApiResponse apiResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(userArray)
                .when()
                .post(baseUrlUserArray)
                .then()
                .log().all()
                .statusCode(CodeStatus.INVALID_ID.getCode())
                .body(matchesJsonSchemaInClasspath(pathJsonSchemeConfig.getPathJsonSchemeApiResponse()))
                .body("code", equalToObject(CodeStatus.INVALID_ID.getCode()))
                .body("type", equalToObject("unknown"))
                .body("message", notNullValue())
                .extract()
                .as(ApiResponse.class);
        log.info(apiResponse.toString());
    }
}
