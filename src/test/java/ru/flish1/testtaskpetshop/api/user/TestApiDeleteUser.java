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
import ru.flish1.testtaskpetshop.entity.User;
import ru.flish1.testtaskpetshop.enums.CodeStatus;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.module.jsv.JsonSchemaValidatorSettings.settings;

@Slf4j
public class TestApiDeleteUser {
    private final String baseUrlUserDelete = "/user/{username}";
    private final String incorrectUsername = "unkno!e._)(\\wn";
    private final String correctUsername = "user1";
    private final String baseUrlUser = "/user";

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


    private void createDeletedUser(String username) {
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
                .extract();
    }

    @Test
    @DisplayName("Успешное удаление пользователя")
    public void testDeleteUserSuccessful() {
        createDeletedUser(correctUsername);
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .pathParam("username", correctUsername)
                .when()
                .delete(baseUrlUserDelete)
                .then()
                .log().all()
                .statusCode(CodeStatus.SUCCESS.getCode());
    }

    @Test
    @DisplayName("Удаление не существующего пользователя")
    public void testDeleteUserNotFound() {
        String username = "unknown";
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .pathParam("username", username)
                .when()
                .delete(baseUrlUserDelete)
                .then()
                .log().all()
                .statusCode(CodeStatus.NOT_FOUND.getCode());
    }

    @Test
    @DisplayName("Удаление пользователя с некорректным именем")
    public void testDeleteUserIncorrectUsername() {
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .pathParam("username", incorrectUsername)
                .when()
                .delete(baseUrlUserDelete)
                .then()
                .log().all()
                .statusCode(CodeStatus.INVALID_ID.getCode());
    }

}
