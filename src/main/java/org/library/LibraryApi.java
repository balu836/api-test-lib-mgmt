package org.library;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static java.lang.ClassLoader.getSystemResource;

/**
 * This is the class to read the Library API details
 */
public class LibraryApi {
    private static LibraryApi apiUtil;
    private static String baseURI;
    private static RequestSpecification requestSpecification;
    private static String userID;
    private static String password;


    private LibraryApi() throws IOException {
        Properties properties = PropReader.get();
        baseURI = properties.getProperty("url");
        userID = properties.getProperty("userID");
        password = properties.getProperty("password");
        requestSpecification = given().relaxedHTTPSValidation().contentType("application/json");

    }

    /**
     * Singleton object to get the Library API instance
     *
     * @return library api instance
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
     * @param endPoint endpoint of the URI
     * @param payLoad payload for the request
     * @return returned the response of the request
     */
    private static Response postURL(String endPoint, String payLoad) {
        return requestSpecification.body(payLoad)
                .when()
                .post(baseURI + endPoint);
    }

    /**
     * @param endPoint endpoint of the URI
     * @param payLoad payload for the request
     * @param authToken authentication token for the request
     * @return returned the response of the request
     */
    private static Response postURL(String endPoint, String payLoad, String authToken) {
        return requestSpecification.auth().oauth2(authToken)
                .body(payLoad)
                .when()
                .post(baseURI + endPoint);
    }

    /**
     * Method to generate the Authentication token
     *
     * @return Return authentication token
     */
    public static String getAuthToken() {
        String userPayload = FileUtils.readFile(getSystemResource("payload/user.json").getFile());
        userPayload = userPayload.replace("{{USERID}}", userID)
                .replace("{{PASSWORD}}", password);
        Response response = postURL("member/login", userPayload);
        return response.jsonPath().getJsonObject("token");
    }

    /**
     * Method to get the response to borrow the book
     *
     * @param bookTitle title of the book
     * @return response returned the borrowed book payload response
     */
    public Response bookBorrowed(String bookTitle, String authToken) {
        String bookBorrowPayload = FileUtils.readFile(getSystemResource("payload/book-borrowed.json").getFile());
        bookBorrowPayload = bookBorrowPayload.replace("{{BOOK_TITLE}}", bookTitle)
                .replace("{{USERID}}", userID);
        return postURL("transactions/borrow", bookBorrowPayload, authToken);
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
     * @return response returned the returned book payload response
     */
    public Response bookReturned(String bookTitle, String authToken) {
        String bookReturnPayload = FileUtils.readFile(getSystemResource("payload/book-returned.json").getFile());
        bookReturnPayload = bookReturnPayload.replace("{{BOOK_TITLE}}", bookTitle)
                .replace("{{USERID}}", userID);
        return postURL("transactions/return", bookReturnPayload, authToken);
    }


}





