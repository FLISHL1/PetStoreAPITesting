package ru.flish1.testtaskpetshop.api;

import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.flish1.testtaskpetshop.config.ApiProperty;
import ru.flish1.testtaskpetshop.config.TestPathJsonSchemeConfig;
import ru.flish1.testtaskpetshop.entity.*;
import ru.flish1.testtaskpetshop.enums.PetStatus;

import java.util.List;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static io.restassured.module.jsv.JsonSchemaValidatorSettings.settings;
import static org.hamcrest.Matchers.equalToObject;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
public class TestApiCreatePet {
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

    @Test
    public void testCreatePetSuccessful() {
        Category category = Category.builder()
                .id(-20476978L)
                .name("test category")
                .build();

        Tag tag1 = Tag.builder()
                .id(895601L)
                .name("Tag1")
                .build();

        Tag tag2 = Tag.builder()
                .id(86688648L)
                .name("Tag2")
                .build();

        Pet petRequest = Pet.builder()
                .id(8979789078978969L)
                .name("Cat")
                .photoUrls(List.of("testImage1", "testImage2"))
                .category(category)
                .tags(List.of(tag1, tag2))
                .status(PetStatus.available)
                .build();

        Pet petResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(petRequest)
                .when()
                .post(baseUrlPet)
                .then()
                .log().all()
                .body(matchesJsonSchemaInClasspath(jsonSchemeConfig.getPathJsonSchemePet()))
                .statusCode(200)

                .extract()
                .as(Pet.class);
        Assertions.assertEquals(petResponse, petRequest);
        log.info(petResponse.toString());
    }

    @Test
    public void testCreatePetSuccessfulWithOnlyNameAndPhotoUrls() {

        Pet petRequest = Pet.builder()
                .name("Cat")
                .photoUrls(List.of("testImage1", "testImage2"))
                .build();

        Pet petResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(petRequest)
                .when()
                .post(baseUrlPet)
                .then()
                .log().all()
                .body(matchesJsonSchemaInClasspath(jsonSchemeConfig.getPathJsonSchemePet()))
                .statusCode(200)
                .body("name", equalToObject(petRequest.getName()))
                .body("photoUrls", equalToObject(petRequest.getPhotoUrls()))
                .extract()
                .as(Pet.class);
        log.info(petResponse.toString());
    }

    @Test
    public void testCreatePetIncorrectWithAnyContentType() {
        ApiResponse apiResponse = RestAssured
                .given()
                .when()
                .contentType(ContentType.ANY)
                .post(baseUrlPet)
                .then()
                .log().all()
                .body(matchesJsonSchemaInClasspath(jsonSchemeConfig.getPathJsonSchemeApiResponse()))
                .statusCode(415)
                .body("code", equalToObject(415))
                .body("type", equalToObject("unknown"))
                .body("message", notNullValue())
                .extract()
                .as(ApiResponse.class);
        log.info(apiResponse.toString());
    }

    @Test
    public void testCreatePetIncorrectWithContentTypeJson() {
        ApiResponse apiResponse = RestAssured
                .given()
                .when()
                .contentType(ContentType.JSON)
                .post(baseUrlPet)
                .then()
                .log().all()
                .body(matchesJsonSchemaInClasspath(jsonSchemeConfig.getPathJsonSchemeApiResponse()))
                .statusCode(405)
                .body("code", equalToObject(405))
                .body("type", equalToObject("unknown"))
                .body("message", equalToObject("no data"))
                .extract()
                .as(ApiResponse.class);
        log.info(apiResponse.toString());
    }
}
