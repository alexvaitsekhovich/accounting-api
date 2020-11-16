package com.alexvait.accountingapi.usermanagement.controller;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasLength;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserManagementRestAssuredIT {

    public static final String APPLICATION_JSON = "application/json";

    private static Map<String, String> userData;
    private static Map<String, String> changedUserData;
    private static String publicId;
    private static String jwtToken;

    @BeforeAll
    static void setUserData() {
        userData = new HashMap<>();
        userData.put("firstName", "Rest_John");
        userData.put("lastName", "Rest_Doe");
        userData.put("email", "ra.john.doe2@api.com");
        userData.put("password", "ra-pass");

        changedUserData = new HashMap<>();
        changedUserData.put("firstName", "Bob");
        changedUserData.put("lastName", "Ross");
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:8080";
        RestAssured.port = 8080;
    }

    @Test
    @Order(1)
    void testCreateUser() {
        JsonPath jsonResponse = given()
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                .body(userData)
                .when()
                .post("/user")
                .then()
                .statusCode(201)
                .contentType(APPLICATION_JSON)
                .extract().response().jsonPath();

        assertThat(jsonResponse.get("publicId"), hasLength(40));
        assertThat(jsonResponse.get("firstName"), equalTo(userData.get("firstName")));
        assertThat(jsonResponse.get("lastName"), equalTo(userData.get("lastName")));
        assertThat(jsonResponse.get("email"), equalTo(userData.get("email")));

        publicId = jsonResponse.get("publicId");
    }

    @Test
    @Order(2)
    void testLoginUser() {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", userData.get("email"));
        loginData.put("password", userData.get("password"));

        Response response = given()
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                .body(loginData)
                .when()
                .post("/gettoken")
                .then()
                .statusCode(200)
                .extract().response();

        jwtToken = response.header("Authorization");
        assertThat(response.header("UserPublicId"), equalTo(publicId));
    }

    @Test
    @Order(3)
    void testGetUser() {
        JsonPath jsonResponse = given()
                .pathParam("publicId", publicId)
                .header("Authorization", jwtToken)
                .accept(APPLICATION_JSON)
                .when()
                .get("/user/{publicId}")
                .then()
                .statusCode(200)
                .extract().response().jsonPath();

        assertThat(jsonResponse.get("publicId"), equalTo(publicId));
        assertThat(jsonResponse.get("firstName"), equalTo(userData.get("firstName")));
        assertThat(jsonResponse.get("lastName"), equalTo(userData.get("lastName")));
        assertThat(jsonResponse.get("email"), equalTo(userData.get("email")));
    }

    @Test
    @Order(4)
    void testUpdateUser() {
        JsonPath jsonResponse = given()
                .pathParam("publicId", publicId)
                .header("Authorization", jwtToken)
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                .body(changedUserData)
                .when()
                .put("/user/{publicId}")
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .extract().response().jsonPath();

        assertThat(jsonResponse.get("publicId"), hasLength(40));
        assertThat(jsonResponse.get("firstName"), equalTo(changedUserData.get("firstName")));
        assertThat(jsonResponse.get("lastName"), equalTo(changedUserData.get("lastName")));
        assertThat(jsonResponse.get("email"), equalTo(userData.get("email")));
    }

    @Test
    @Order(5)
    void testDeleteUser() {
        JsonPath jsonResponse = given()
                .pathParam("publicId", publicId)
                .header("Authorization", jwtToken)
                .accept(APPLICATION_JSON)
                .when()
                .delete("/admin/user/{publicId}")
                .then()
                .statusCode(500)
                .contentType(APPLICATION_JSON)
                .extract().response().jsonPath();

        assertThat(jsonResponse.get("details.status"), equalTo(500));
        assertThat(jsonResponse.get("responseState"), equalTo("FAILURE"));
    }

    @Test
    @Order(6)
    void testLoginAdmin() {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", "admin@api.com");
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
    void testGetUserByAdmin() {
        JsonPath jsonResponse = given()
                .pathParam("publicId", publicId)
                .header("Authorization", jwtToken)
                .accept(APPLICATION_JSON)
                .when()
                .get("/user/{publicId}")
                .then()
                .statusCode(200)
                .extract().response().jsonPath();

        assertThat(jsonResponse.get("publicId"), equalTo(publicId));
        assertThat(jsonResponse.get("firstName"), equalTo(changedUserData.get("firstName")));
        assertThat(jsonResponse.get("lastName"), equalTo(changedUserData.get("lastName")));
        assertThat(jsonResponse.get("email"), equalTo(userData.get("email")));
    }

    @Test
    @Order(8)
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
                .put("/user/{publicId}")
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .extract().response().jsonPath();

        assertThat(jsonResponse.get("publicId"), hasLength(40));
        assertThat(jsonResponse.get("firstName"), equalTo(updateData.get("firstName")));
        assertThat(jsonResponse.get("lastName"), equalTo(updateData.get("lastName")));
        assertThat(jsonResponse.get("email"), equalTo(userData.get("email")));
    }

    @Test
    @Order(9)
    void testDeleteUserByAdmin() {
        JsonPath jsonResponse = given()
                .pathParam("publicId", publicId)
                .header("Authorization", jwtToken)
                .accept(APPLICATION_JSON)
                .when()
                .delete("/admin/user/{publicId}")
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .extract().response().jsonPath();

        assertThat(jsonResponse.get("responseState"), equalTo("SUCCESS"));
        assertThat(jsonResponse.get("httpStatus"), equalTo("OK"));
    }
}