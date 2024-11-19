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

import java.util.List;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static io.restassured.module.jsv.JsonSchemaValidatorSettings.settings;
import static org.hamcrest.Matchers.equalToObject;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
public class TestApiCreateUserWithList {
    private final String baseUrlUserList = "/user/createWithList";
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
    @DisplayName("Создание пользователей через массив")
    public void testCreateListUserSuccessfulWithList() {


        User user1 = User.builder()
                .id(14282420L)
                .username("testUser")
                .firstName("testFirstName")
                .lastName("testLastName")
                .email("test@gmail.com")
                .phone("89604707981")
                .userStatus(-17771223)
                .build();

        User user2 = User.builder()
                .id(14123L)
                .username("testUser")
                .firstName("testFirstName")
                .lastName("testLastName")
                .email("test@gmail.com")
                .phone("89604707981")
                .userStatus(-17771223)
                .build();

        ApiResponse apiResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(List.of(user1, user2))
                .when()
                .post(baseUrlUserList)
                .then()
                .log().all()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath(pathJsonSchemeConfig.getPathJsonSchemeApiResponse()))
                .body("code", equalToObject(200))
                .body("type", equalToObject("unknown"))
                .body("message", equalToObject("ok"))
                .extract()
                .as(ApiResponse.class);
        log.info(apiResponse.toString());
    }

    @Test
    @DisplayName("Создание повторяющихся пользователей через список")
    public void testCreateRepeatableUsersIncorrect() {
        User user1 = User.builder()
                .id(12321431L)
                .username("testUser")
                .firstName("testFirstName")
                .lastName("testLastName")
                .email("test@gmail.com")
                .phone("89604707981")
                .userStatus(-17771223)
                .build();

        User user2 = User.builder()
                .id(12321431L)
                .username("testUser")
                .firstName("testFirstName")
                .lastName("testLastName")
                .email("test@gmail.com")
                .phone("89604707981")
                .userStatus(-17771223)
                .build();



        ApiResponse apiResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(List.of(user2, user1))
                .when()
                .post(baseUrlUserList)
                .then()
                .log().all()
                .statusCode(400)
                .body(matchesJsonSchemaInClasspath(pathJsonSchemeConfig.getPathJsonSchemeApiResponse()))
                .body("code", equalToObject(400))
                .body("type", equalToObject("unknown"))
                .body("message", notNullValue())
                .extract()
                .as(ApiResponse.class);
        log.info(apiResponse.toString());
    }
}
