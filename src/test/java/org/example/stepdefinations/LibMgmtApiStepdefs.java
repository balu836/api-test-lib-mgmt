package org.example.stepdefinations;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.example.LibraryApi;
import org.testng.Assert;

import java.io.IOException;
import java.util.ArrayList;

public class LibMgmtApiStepdefs {
    private static LibraryApi libraryApi;
    private static String authToken;

    @Given("Login Library Management API")
    public void loginLibraryManagementAPI() throws IOException {
        libraryApi = LibraryApi.get();
        authToken = LibraryApi.getAuthToken();
    }

    @Given("Check the Book {string} availability in the Library")
    public void checkTheBookAvailabilityInTheLibrary(String bookName) {
        ArrayList arrayList = libraryApi.bookAvailability(bookName);
        Assert.assertNotEquals(arrayList.get(0), 0, "Books not available");
    }

    @When("Borrow the Book  {string} from the Library")
    public void borrowTheBookFromTheLibrary(String bookName) {
                Response response = libraryApi.bookBorrowed(bookName, authToken);
        Assert.assertEquals(response.statusCode(), 201, "Book not available to borrow");

    }

    @Then("Return the Book {string} to the Library")
    public void returnTheBookToTheLibrary(String bookName) {
                Response response = libraryApi.bookReturned(bookName, authToken);
        Assert.assertEquals(response.statusCode(), 200);

    }
}
