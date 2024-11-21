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
import ru.flish1.testtaskpetshop.entity.UserLogin;
import ru.flish1.testtaskpetshop.enums.CodeStatus;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static io.restassured.module.jsv.JsonSchemaValidatorSettings.settings;
import static org.hamcrest.Matchers.*;

@Slf4j
public class TestApiLoginUser {
    private final String baseUrlUserLogin = "/user/login";
    private final String correctLogin = "testUsername";
    private final String correctPassword = "testPassword";
    private final String incorrectLogin = "incorrect";
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

    private UserLogin getUserLogin(String username, String password) {
        return UserLogin.builder()
                .username(username)
                .password(password)
                .build();
    }

    @Test
    @DisplayName("Успешная авторизация пользователя")
    public void testLoginUserSuccessful() {
        UserLogin user = getUserLogin(correctLogin, correctPassword);

        ApiResponse apiResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .queryParam("username", user.getUsername())
                .queryParam("password", user.getPassword())
                .when()
                .get(baseUrlUserLogin)
                .then()
                .log().all()
                .statusCode(CodeStatus.SUCCESS.getCode())
                .body(matchesJsonSchemaInClasspath(pathJsonSchemeConfig.getPathJsonSchemeApiResponse()))
                .body("code", equalToObject(CodeStatus.SUCCESS.getCode()))
                .body("type", equalToObject("unknown"))
                .body("message", notNullValue())
                .header("X-Expires-After", notNullValue())
                .header("X-Rate-Limit", notNullValue())
                .extract()
                .as(ApiResponse.class);
        log.info(apiResponse.toString());
    }

    @Test
    @DisplayName("Авторизация с некорректными данными")
    public void testLoginUserIncorrect() {
        UserLogin user = getUserLogin(incorrectLogin, correctPassword);

        ApiResponse apiResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .queryParam("username", user.getUsername())
                .queryParam("password", user.getPassword())
                .when()
                .get(baseUrlUserLogin)
                .then()
                .log().all()
                .statusCode(CodeStatus.INVALID_ID.getCode())
                .body(matchesJsonSchemaInClasspath(pathJsonSchemeConfig.getPathJsonSchemeApiResponse()))
                .body("code", equalToObject(CodeStatus.INVALID_ID.getCode()))
                .body("type", equalToObject("unknown"))
                .body("message", notNullValue())
                .header("X-Expires-After", nullValue())
                .header("X-Rate-Limit", nullValue())
                .extract()
                .as(ApiResponse.class);
        log.info(apiResponse.toString());
    }

    @Test
    @DisplayName("Авторизация без пароля")
    public void testLoginUserIncorrectWithOnlyUsername() {
        UserLogin user = getUserLogin(correctLogin, null);

        ApiResponse apiResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .queryParam("username", user.getUsername())
                .when()
                .get(baseUrlUserLogin)
                .then()
                .log().all()
                .statusCode(CodeStatus.INVALID_ID.getCode())
                .body(matchesJsonSchemaInClasspath(pathJsonSchemeConfig.getPathJsonSchemeApiResponse()))
                .body("code", equalToObject(CodeStatus.INVALID_ID.getCode()))
                .body("type", equalToObject("unknown"))
                .body("message", notNullValue())
                .header("X-Expires-After", nullValue())
                .header("X-Rate-Limit", nullValue())
                .extract()
                .as(ApiResponse.class);
        log.info(apiResponse.toString());

    }
}
