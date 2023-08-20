package org.example;

import groovy.util.logging.Log;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import org.junit.jupiter.api.*;

import java.util.List;

public class ReqresApiTests {

    private static final String BASE_URI = "https://reqres.in/api";
    private static final String USERS_ENDPOINT = "/users";
    private static final String LOGIN_ENDPOINT = "/login";

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.filters(new RequestLoggingFilter());
    }

    @Test
    public void testGetList() {
        List<User> users = RestAssured
            .given()
            .get(USERS_ENDPOINT)
            .then()
            .contentType(ContentType.JSON)
            .extract()
            .jsonPath()
            .getList("data", User.class);

        for (User user : users) {
            System.out.println("The user's name is: " + user.getFirst_name());
        }

        String[] userNames = new String[] {
                "George",
                "Janet",
                "Emma",
                "Eve",
                "Charles",
                "Tracey"
        };
        for (int i=0; i<users.size(); ++i) {
            Assertions.assertEquals(users.get(i).getFirst_name(), userNames[i]);
        }
    }

    @Test
    public void testCreateUser() {
        // Створення об'єкту користувача для відправки у POST запиті
        User user = new User(12, "bob@gmail.com", "BoB", "Blam", "https://avatars.com/bob");

        UserCreateResponse userResponse = RestAssured
            .given()
            .contentType(ContentType.JSON)
            .body(user)
            .post(USERS_ENDPOINT)
            .then()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .extract()
            .jsonPath()
            .getObject("$", UserCreateResponse.class);

        Assertions.assertEquals(userResponse.getEmail(), user.getEmail());
        Assertions.assertEquals(userResponse.getId(), user.getId());
        Assertions.assertEquals(userResponse.getFirst_name(), user.getFirst_name());
        Assertions.assertEquals(userResponse.getAvatar(), user.getAvatar());
    }

    @Test
    public void testUpdateUser() {
        // Отримати ідентифікатор користувача, якого потрібно оновити (наприклад, з попереднього тесту)
        int userId = 123;

        class UserUpdate {
            private String first_name;
            private String last_name;

            public UserUpdate(String first_name, String last_name) {
                this.first_name = first_name;
                this.last_name = last_name;
            }

            public String getFirst_name() {
                return first_name;
            }

            public void setFirst_name(String first_name) {
                this.first_name = first_name;
            }

            public String getLast_name() {
                return last_name;
            }

            public void setLast_name(String last_name) {
                this.last_name = last_name;
            }
        }

        // Оновлений об'єкт користувача
        UserUpdate updatedUser = new UserUpdate("NEW_FIRST_NAME", "NEW_LAST_NAME");

        UserUpdateResponse response = RestAssured
            .given()
            .contentType(ContentType.JSON)
            .body(updatedUser)
            .when()
            .put(USERS_ENDPOINT + "/" + userId)
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .extract()
            .jsonPath()
            .getObject("$", UserUpdateResponse.class);

        Assertions.assertEquals(response.getFirst_name(), updatedUser.getFirst_name());
        Assertions.assertEquals(response.getLast_name(), updatedUser.getLast_name());
    }

    @Test
    public void testDeleteUser() {
        // Отримати ідентифікатор користувача для видалення (наприклад, з попереднього тесту)
        int userId = 123;

        RestAssured
            .given()
            .when()
            .delete(USERS_ENDPOINT + "/" + userId)
            .then()
            .statusCode(204);
    }

    @Test
    public void testLoginSuccessful() {
        class LoginRequest {
            private String email;
            private String password;

            LoginRequest() {}

            public LoginRequest(String email, String password) {
                this.email = email;
                this.password = password;
            }


            public String getPassword() {
                return password;
            }

            public void setPassword(String password) {
                this.password = password;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }
        }
        // Створення об'єкту для логіну
        LoginRequest loginRequest = new LoginRequest("eve.holt@reqres.in", "cityslicka");

        Response response = RestAssured
            .given()
            .contentType(ContentType.JSON)
            .body(loginRequest)
            .when()
            .post(LOGIN_ENDPOINT)
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .extract()
            .response();

        // Перевірка логіну
        // Ваш код для перевірки тут
    }
}

class LoginRequest {
    private String username;
    private String password;

    // Конструктори, геттери, сеттери та інші методи
}
