package org.example;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import static io.restassured.RestAssured.given;

/**
 * This is the class to read the Library API details
 */
public class LibraryApi {
    private static String authToken;
    private static LibraryApi apiUtil;
    private static String baseURI;
    private static RequestSpecification requestSpecification;
    private static String userID;
    private static String password;


    public LibraryApi() throws IOException {
        Properties properties = PropReader.get();
        baseURI = properties.getProperty("url");
        userID = properties.getProperty("userID");
        password = properties.getProperty("password");
        requestSpecification = given().relaxedHTTPSValidation().contentType("application/json");
        authToken = getAuthToken();

    }

    /**
     * Singleton object to get the Librarry instance
     *
     * @return
     * @throws IOException
     */
    public static LibraryApi get() throws IOException {
        if (apiUtil == null) {
            apiUtil = new LibraryApi();
        }
        return apiUtil;

    }

    /**
     * Send payload and get the responce
     *
     * @param endPoint
     * @param payLoad
     * @return
     */
    private static Response postURL(String endPoint, String payLoad) {
        return requestSpecification.body(payLoad)
                .when()
                .post(baseURI + endPoint);
    }

    /**
     * Method to generate the Authentication token
     *
     * @return
     */
    private static String getAuthToken() {
        String userPayload = "{\n" +
                "  \"username\": \"{{USERID}}\",\n" +
                "  \"password\": \"{{PASSWORD}}\"\n" +
                "}";
        userPayload = userPayload.replace("{{USERID}}", userID)
                .replace("{{PASSWORD}}", password);
        if (authToken == null) {
            Response response = postURL("member/login", userPayload);
            return response.jsonPath().getJsonObject("token");
        }
        return authToken;
    }

    /**
     * Method to get the response to borrow the book
     *
     * @param bookTitle title of the book
     * @return response
     */
    public Response bookBorrowed(String bookTitle) {
        String bookBorrowPayload = "{\n" +
                "  \"title\": \"{{BOOK_TITLE}}\",\n" +
                "  \"username\": \"{{USERID}}\"\n" +
                "}";
        bookBorrowPayload = bookBorrowPayload.replace("{{BOOK_TITLE}}", bookTitle)
                .replace("{{USERID}}", userID);
        return requestSpecification.auth().oauth2(authToken)
                .body(bookBorrowPayload)
                .when()
                .post(baseURI + "transactions/borrow");
    }

    /**
     * @param bookTitle title of the book
     * @return returning the available count
     */
    public ArrayList bookAvailability(String bookTitle) {
        Response response = requestSpecification.when()
                .get(baseURI + "books/title/" + bookTitle);
        return response.body().jsonPath().getJsonObject("availableCopies");
    }

    /**
     * Method to get the response to returned the book
     *
     * @param bookTitle title of the book
     * @return response
     */
    public Response bookReturned(String bookTitle) {
        String bookReturnPayload = "{\n" +
                "  \"title\": \"{{BOOK_TITLE}}\",\n" +
                "  \"username\": \"{{USERID}}\"\n" +
                "}";
        bookReturnPayload = bookReturnPayload.replace("{{BOOK_TITLE}}", bookTitle)
                .replace("{{USERID}}", userID);
        return requestSpecification.auth()
                .oauth2(authToken)
                .body(bookReturnPayload)
                .when()
                .post(baseURI + "transactions/return");
    }


}



