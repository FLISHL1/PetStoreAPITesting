package ru.flish1.testtaskpetshop.api.pet;

import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.flish1.testtaskpetshop.config.ApiProperty;
import ru.flish1.testtaskpetshop.entity.Order;
import ru.flish1.testtaskpetshop.enums.CodeStatus;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.module.jsv.JsonSchemaValidatorSettings.settings;

public class TestApiDeletePet {
    private final String baseUrlPetDelete = "/pet/{petId}";
    private final String baseUrlPet = "/pet";
    private final Long unknownPetId = -123L;
    private final Long correctPetId = 123L;

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

    private void createDeletedPet(long petId) {
        Order order = Order.builder()
                .id(petId)
                .build();
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(order)
                .when()
                .post(baseUrlPet)
                .then()
                .log().all()
                .statusCode(CodeStatus.SUCCESS.getCode());
    }

    @Test
    @DisplayName("Успешное удаление питомца")
    public void testDeletePetSuccessful() {
        createDeletedPet(correctPetId);
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .pathParam("petId", correctPetId)
                .when()
                .delete(baseUrlPetDelete)
                .then()
                .log().all()
                .statusCode(CodeStatus.SUCCESS.getCode());
    }

    @Test
    @DisplayName("Удаление не существующего питомца")
    public void testDeletePetNotFound() {

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .pathParam("petId", unknownPetId)
                .when()
                .delete(baseUrlPetDelete)
                .then()
                .log().all()
                .statusCode(CodeStatus.NOT_FOUND.getCode());
    }
}
