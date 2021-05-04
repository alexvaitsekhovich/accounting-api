package com.alexvait.accountingapi.usermanagement.controller;

import com.alexvait.accountingapi.security.config.SecurityConstants;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasLength;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("User management integration tests")
@EnabledIf(value = "#{'${spring.profiles.active}' == 'testing'}", loadContext = true)
class UserManagementRestAssuredIT {

    public static final String APPLICATION_JSON = "application/json";

    private static Map<String, String> userData;
    private static Map<String, String> changedUserData;
    private static String publicId;
    private static String jwtToken;

    @BeforeAll
    static void setUserData() {
        RestAssured.baseURI = "http://localhost:8080";
        RestAssured.port = 8080;

        userData = new HashMap<>();
        userData.put("firstName", "Rest_John");
        userData.put("lastName", "Rest_Doe");
        userData.put("email", "ra.john.doe2@api.com");
        userData.put("password", "ra-pass");

        changedUserData = new HashMap<>();
        changedUserData.put("firstName", "Bob");
        changedUserData.put("lastName", "Ross");
    }

    @Test
    @Order(1)
    @DisplayName("Test create user")
    void testCreateUser() {
        JsonPath jsonResponse = given()
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                .body(userData)
                .when()
                .post(UserController.BASE_URL)
                .then()
                .statusCode(201)
                .contentType(APPLICATION_JSON)
                .extract().response().jsonPath();

        assertAll(
                "test returned fields",
                () -> assertThat(jsonResponse.get("publicId"), hasLength(40)),
                () -> assertEquals(userData.get("firstName"), jsonResponse.get("firstName"), "First name comparison failed"),
                () -> assertEquals(userData.get("lastName"), jsonResponse.get("lastName"), "Last name comparison failed"),
                () -> assertEquals(userData.get("email"), jsonResponse.get("email"), "Email name comparison failed")
        );

        publicId = jsonResponse.get("publicId");
    }

    @Test
    @Order(2)
    @DisplayName("Test login user")
    void testLoginUser() {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", userData.get("email"));
        loginData.put("password", userData.get("password"));

        Response response = given()
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                .body(loginData)
                .when()
                .post(SecurityConstants.LOGIN_URL)
                .then()
                .statusCode(200)
                .extract().response();

        jwtToken = response.header("Authorization");
        assertEquals(publicId, response.header("UserPublicId"));
    }

    @Test
    @Order(3)
    @DisplayName("Test get user")
    void testGetUser() {
        JsonPath jsonResponse = given()
                .pathParam("publicId", publicId)
                .header("Authorization", jwtToken)
                .accept(APPLICATION_JSON)
                .when()
                .get(UserController.BASE_URL + "/{publicId}")
                .then()
                .statusCode(200)
                .extract().response().jsonPath();

        assertAll(
                "test returned fields",
                () -> assertEquals(publicId, jsonResponse.get("publicId"), "Public id comparison failed"),
                () -> assertEquals(userData.get("firstName"), jsonResponse.get("firstName"), "First name comparison failed"),
                () -> assertEquals(userData.get("lastName"), jsonResponse.get("lastName"), "Last name comparison failed"),
                () -> assertEquals(userData.get("email"), jsonResponse.get("email"), "Email name comparison failed")
        );
    }

    @Test
    @Order(4)
    @DisplayName("Test update user")
    void testUpdateUser() {
        JsonPath jsonResponse = given()
                .pathParam("publicId", publicId)
                .header("Authorization", jwtToken)
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                .body(changedUserData)
                .when()
                .put(UserController.BASE_URL + "/{publicId}")
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .extract().response().jsonPath();

        assertAll(
                "test returned fields",
                () -> assertThat(jsonResponse.get("publicId"), hasLength(40)),
                () -> assertEquals(changedUserData.get("firstName"), jsonResponse.get("firstName"), "First name comparison failed"),
                () -> assertEquals(changedUserData.get("lastName"), jsonResponse.get("lastName"), "Last name comparison failed"),
                () -> assertEquals(userData.get("email"), jsonResponse.get("email"), "Email name comparison failed")
        );
    }

    @Test
    @Order(5)
    @DisplayName("Test delete user")
    void testDeleteUser() {
        JsonPath jsonResponse = given()
                .pathParam("publicId", publicId)
                .header("Authorization", jwtToken)
                .accept(APPLICATION_JSON)
                .when()
                .delete(AdminController.BASE_URL + "/user/{publicId}")
                .then()
                .statusCode(403)
                .contentType(APPLICATION_JSON)
                .extract().response().jsonPath();

        assertAll(
                "test returned fields",
                () -> assertEquals(403, (int) jsonResponse.get("details.status"), "Last name comparison failed"),
                () -> assertEquals("FAILURE", jsonResponse.get("responseState"), "Email name comparison failed")
        );
    }

    @Test
    @Order(6)
    @DisplayName("Test login as admin")
    void testLoginAdmin() {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", "users-admin@api.com");
        loginData.put("password", "admin-pass");

        Response response = given()
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                .body(loginData)
                .when()
                .post("/gettoken")
                .then()
                .statusCode(200)
                .extract().response();

        jwtToken = response.header("Authorization");
    }

    @Test
    @Order(7)
    @DisplayName("Test get user as admin")
    void testGetUserByAdmin() {
        JsonPath jsonResponse = given()
                .pathParam("publicId", publicId)
                .header("Authorization", jwtToken)
                .accept(APPLICATION_JSON)
                .when()
                .get(UserController.BASE_URL + "/{publicId}")
                .then()
                .statusCode(200)
                .extract().response().jsonPath();

        assertAll(
                "test returned fields",
                () -> assertEquals(publicId, jsonResponse.get("publicId"), "Public id comparison failed"),
                () -> assertEquals(changedUserData.get("firstName"), jsonResponse.get("firstName"), "First name comparison failed"),
                () -> assertEquals(changedUserData.get("lastName"), jsonResponse.get("lastName"), "Last name comparison failed"),
                () -> assertEquals(userData.get("email"), jsonResponse.get("email"), "Email name comparison failed")
        );
    }

    @Test
    @Order(8)
    @DisplayName("Test update user as admin")
    void testUpdateUserByAdmin() {
        Map<String, String> updateData = new HashMap<>();
        updateData.put("firstName", "AdminChangedFirst");
        updateData.put("lastName", "AdminChangedLast");

        JsonPath jsonResponse = given()
                .pathParam("publicId", publicId)
                .header("Authorization", jwtToken)
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                .body(updateData)
                .when()
                .put(UserController.BASE_URL + "/{publicId}")
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .extract().response().jsonPath();

        assertAll(
                "test returned fields",
                () -> assertThat(jsonResponse.get("publicId"), hasLength(40)),
                () -> assertEquals(updateData.get("firstName"), jsonResponse.get("firstName"), "First name comparison failed"),
                () -> assertEquals(updateData.get("lastName"), jsonResponse.get("lastName"), "Last name comparison failed"),
                () -> assertEquals(userData.get("email"), jsonResponse.get("email"), "Email name comparison failed")
        );
    }

    @Test
    @Order(9)
    @DisplayName("Test delete user as admin")
    void testDeleteUserByAdmin() {
        JsonPath jsonResponse = given()
                .pathParam("publicId", publicId)
                .header("Authorization", jwtToken)
                .accept(APPLICATION_JSON)
                .when()
                .delete(AdminController.BASE_URL + "/user/{publicId}")
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .extract().response().jsonPath();

        assertAll(
                "test returned fields",
                () -> assertEquals("SUCCESS", jsonResponse.get("responseState"), "Response status comparison failed"),
                () -> assertEquals("OK", jsonResponse.get("httpStatus"), "Http comparison failed")
        );
    }
}