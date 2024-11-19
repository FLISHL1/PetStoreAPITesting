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
import ru.flish1.testtaskpetshop.entity.ApiResponse;
import ru.flish1.testtaskpetshop.entity.User;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.module.jsv.JsonSchemaValidatorSettings.settings;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
public class TestApiDeleteUser {
    private final String baseUrlUserDelete = "/user/{username}";
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

        createDeletedUser();
    }

    @BeforeEach
    public void before(){
        createDeletedUser();
    }

    private void createDeletedUser() {
        User user = User.builder()
                .username("user1")
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

    @Test
    @DisplayName("Успешное удаление пользователя")
    public void testDeleteUserSuccessful() {
        String username = "user1";
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .pathParam("username", username)
                .when()
                .delete(baseUrlUserDelete)
                .then()
                .log().all()
                .statusCode(200);
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
                .statusCode(404);
    }

    @Test
    @DisplayName("Удаление пользователя с некорректным именем")
    public void testDeleteUserIncorrectUsername() {
        String username = "unkno!e._)(\\wn";
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .pathParam("username", username)
                .when()
                .delete(baseUrlUserDelete)
                .then()
                .log().all()
                .statusCode(400);
    }

}
