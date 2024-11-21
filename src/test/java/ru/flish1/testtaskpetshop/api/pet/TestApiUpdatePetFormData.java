package ru.flish1.testtaskpetshop.api.pet;

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
import ru.flish1.testtaskpetshop.enums.CodeStatus;
import ru.flish1.testtaskpetshop.enums.PetStatus;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static io.restassured.module.jsv.JsonSchemaValidatorSettings.settings;

@Slf4j
public class TestApiUpdatePetFormData {
    private final String baseUrlPet = "/pet/{petId}";
    private final long incorrectPetId = -123L;
    private final long correctPetId = 123L;
    private final TestPathJsonSchemeConfig jsonSchemeConfig = new TestPathJsonSchemeConfig();

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
    @DisplayName("Успешное обновление питомца")
    public void testUpdatePetSuccessful() {
        String nameUpdate = "nameUpdate";
        PetStatus statusUpdate = PetStatus.available;
        ApiResponse apiResponse = RestAssured
                .given()
                .contentType(ContentType.URLENC)
                .formParam("name", nameUpdate)
                .formParam("status", statusUpdate)
                .pathParam("petId", correctPetId)
                .when()
                .post(baseUrlPet)
                .then()
                .log().all()
                .body(matchesJsonSchemaInClasspath(jsonSchemeConfig.getPathJsonSchemeApiResponse()))
                .statusCode(CodeStatus.SUCCESS.getCode())
                .extract()
                .as(ApiResponse.class);
        log.info(apiResponse.toString());
    }

    @Test
    @DisplayName("Обновление не существующего питомца")
    public void testUpdatePetIncorrect() {

        String nameUpdate = "nameUpdate";
        PetStatus statusUpdate = PetStatus.available;
        ApiResponse apiResponse = RestAssured
                .given()
                .contentType(ContentType.URLENC)
                .formParam("name", nameUpdate)
                .formParam("status", statusUpdate)
                .pathParam("petId", incorrectPetId)
                .when()
                .post(baseUrlPet)
                .then()
                .log().all()
                .body(matchesJsonSchemaInClasspath(jsonSchemeConfig.getPathJsonSchemeApiResponse()))
                .statusCode(CodeStatus.NOT_FOUND.getCode())
                .extract()
                .as(ApiResponse.class);
        log.info(apiResponse.toString());
    }
}
