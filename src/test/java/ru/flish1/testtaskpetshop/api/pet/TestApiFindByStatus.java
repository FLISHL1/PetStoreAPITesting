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
import ru.flish1.testtaskpetshop.enums.CodeStatus;
import ru.flish1.testtaskpetshop.enums.PetStatus;

import java.util.List;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static io.restassured.module.jsv.JsonSchemaValidatorSettings.settings;

@Slf4j
public class TestApiFindByStatus {
    private final String baseUrlPet = "/pet/findByStatus";
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
    @DisplayName("Успешное получение питомцев по статусам")
    public void testFindByStatusPetSuccessful() {
        List<PetStatus> petStatuses = List.of(PetStatus.sold, PetStatus.available, PetStatus.pending);
        List petResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .queryParam("status", petStatuses)
                .when()
                .log().headers()
                .get(baseUrlPet)
                .then()
                .log().all()
                .body(matchesJsonSchemaInClasspath(jsonSchemeConfig.getPathJsonSchemePetList()))
                .statusCode(CodeStatus.SUCCESS.getCode())
                .extract()
                .as(List.class);
        log.info(petResponse.toString());
    }

    @Test
    @DisplayName("Получение питомцев по неверному статусу")
    public void testFindByStatusPetIncorrect() {
        String petStatus = "status";
        List petResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .queryParam("status", petStatus)
                .when()
                .get(baseUrlPet)
                .then()
                .log().all()
                .body(matchesJsonSchemaInClasspath(jsonSchemeConfig.getPathJsonSchemePetList()))
                .statusCode(CodeStatus.INVALID_ID.getCode())
                .extract()
                .as(List.class);
        log.info(petResponse.toString());
    }

}
