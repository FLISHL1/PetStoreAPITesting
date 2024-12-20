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
import ru.flish1.testtaskpetshop.entity.ApiResponse;
import ru.flish1.testtaskpetshop.entity.Category;
import ru.flish1.testtaskpetshop.entity.Pet;
import ru.flish1.testtaskpetshop.entity.Tag;
import ru.flish1.testtaskpetshop.enums.CodeStatus;
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
    private final Long createPetId = 123L;
    private final Long createCategoryId = 1234L;
    private final Long createTag1Id = 12346L;
    private final Long createTag2Id = 123467L;


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

    private Pet getFullPetRequest() {
        Category category = Category.builder()
                .id(createCategoryId)
                .name("test category")
                .build();

        Tag tag1 = Tag.builder()
                .id(createTag1Id)
                .name("Tag1")
                .build();

        Tag tag2 = Tag.builder()
                .id(createTag2Id)
                .name("Tag2")
                .build();

        return Pet.builder()
                .id(createPetId)
                .name("Cat")
                .photoUrls(List.of("testImage1", "testImage2"))
                .category(category)
                .tags(List.of(tag1, tag2))
                .status(PetStatus.available)
                .build();
    }

    private Pet getNoFilledPetRequest() {
        return Pet.builder()
                .name("Cat")
                .photoUrls(List.of("testImage1", "testImage2"))
                .build();
    }

    @Test
    @DisplayName("Успешное создание питомца")
    public void testCreatePetSuccessful() {
        Pet petRequest = getFullPetRequest();

        Pet petResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(petRequest)
                .when()
                .post(baseUrlPet)
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
    @DisplayName("Успешное создание питомца только по имени и фотке")
    public void testCreatePetSuccessfulWithOnlyNameAndPhotoUrls() {
        Pet petRequest = getNoFilledPetRequest();
        Pet petResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(petRequest)
                .when()
                .post(baseUrlPet)
                .then()
                .log().all()
                .body(matchesJsonSchemaInClasspath(jsonSchemeConfig.getPathJsonSchemePet()))
                .statusCode(CodeStatus.SUCCESS.getCode())
                .body("name", equalToObject(petRequest.getName()))
                .body("photoUrls", equalToObject(petRequest.getPhotoUrls()))
                .extract()
                .as(Pet.class);
        log.info(petResponse.toString());
    }

    @Test
    @DisplayName("Не корректное создание питомца без указания типа контента в запросе")
    public void testCreatePetIncorrectWithAnyContentType() {
        ApiResponse apiResponse = RestAssured
                .given()
                .when()
                .contentType(ContentType.ANY)
                .post(baseUrlPet)
                .then()
                .log().all()
                .body(matchesJsonSchemaInClasspath(jsonSchemeConfig.getPathJsonSchemeApiResponse()))
                .statusCode(CodeStatus.UNSUPPORTED_MEDIA_TYPE.getCode())
                .body("code", equalToObject(CodeStatus.UNSUPPORTED_MEDIA_TYPE.getCode()))
                .body("type", equalToObject("unknown"))
                .body("message", notNullValue())
                .extract()
                .as(ApiResponse.class);
        log.info(apiResponse.toString());
    }

    @Test
    @DisplayName("Не корректное создание питомца с указание типа контента в запросе на JSON")
    public void testCreatePetIncorrectWithContentTypeJson() {
        ApiResponse apiResponse = RestAssured
                .given()
                .when()
                .contentType(ContentType.JSON)
                .post(baseUrlPet)
                .then()
                .log().all()
                .body(matchesJsonSchemaInClasspath(jsonSchemeConfig.getPathJsonSchemeApiResponse()))
                .statusCode(CodeStatus.NO_DATA.getCode())
                .body("code", equalToObject(CodeStatus.NO_DATA.getCode()))
                .body("type", equalToObject("unknown"))
                .body("message", equalToObject("no data"))
                .extract()
                .as(ApiResponse.class);
        log.info(apiResponse.toString());
    }
}
