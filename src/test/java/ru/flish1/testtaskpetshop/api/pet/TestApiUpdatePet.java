package ru.flish1.testtaskpetshop.api.pet;

import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.flish1.testtaskpetshop.config.ApiProperty;
import ru.flish1.testtaskpetshop.config.TestPathJsonSchemeConfig;
import ru.flish1.testtaskpetshop.entity.Category;
import ru.flish1.testtaskpetshop.entity.Pet;
import ru.flish1.testtaskpetshop.entity.Tag;
import ru.flish1.testtaskpetshop.enums.CodeStatus;
import ru.flish1.testtaskpetshop.enums.PetStatus;

import java.util.List;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static io.restassured.module.jsv.JsonSchemaValidatorSettings.settings;

@Slf4j
public class TestApiUpdatePet {
    private final long incorrectPetId = -123L;
    private final long categoryId = -20476978L;
    private final long tag1Id = 895601L;
    private final long tag2Id = 86688648L;
    private final long correctPetId = 123L;
    private final String baseUrlPet = "/pet";
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

    private Pet getPetRequest(long id) {
        Category category = Category.builder()
                .id(categoryId)
                .name("test category")
                .build();

        Tag tag1 = Tag.builder()
                .id(tag1Id)
                .name("Tag1")
                .build();

        Tag tag2 = Tag.builder()
                .id(tag2Id)
                .name("Tag2")
                .build();

        return Pet.builder()
                .id(id)
                .name("Cat")
                .photoUrls(List.of("testImage1", "testImage2"))
                .category(category)
                .tags(List.of(tag1, tag2))
                .status(PetStatus.available)
                .build();
    }

    @Test
    @DisplayName("Успешное полное обновление питомца")
    public void testUpdatePetSuccessful() {
        Pet petRequest = getPetRequest(correctPetId);

        Pet petResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(petRequest)
                .when()
                .put(baseUrlPet)
                .then()
                .log().all()
                .body(matchesJsonSchemaInClasspath(jsonSchemeConfig.getPathJsonSchemePet()))
                .statusCode(CodeStatus.SUCCESS.getCode())
                .extract()
                .as(Pet.class);
        Assertions.assertEquals(petResponse, petRequest);
        log.info(petResponse.toString());
    }

    @Test
    @DisplayName("Обновление не существующего питомца")
    public void testUpdatePetIncorrect() {
        Pet petRequest = getPetRequest(incorrectPetId);

        Pet petResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(petRequest)
                .when()
                .put(baseUrlPet)
                .then()
                .log().all()
                .body(matchesJsonSchemaInClasspath(jsonSchemeConfig.getPathJsonSchemePet()))
                .statusCode(CodeStatus.NOT_FOUND.getCode())
                .extract()
                .as(Pet.class);
        Assertions.assertEquals(petResponse, petRequest);
        log.info(petResponse.toString());
    }
}
