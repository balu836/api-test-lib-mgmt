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

    @Given("Login Library Management API")
    public void loginLibraryManagementAPI() throws IOException {
        libraryApi = LibraryApi.get();

    }

    @Given("Check the Book {string} availability in the Library")
    public void checkTheBookAvailabilityInTheLibrary(String bookName) {
        ArrayList arrayList = libraryApi.bookAvailability(bookName);
        Assert.assertNotEquals(arrayList.get(0), 0, "Books not available");
    }

    @When("Borrow the Book  {string} availability from the Library")
    public void borrowTheBookAvailabilityFromTheLibrary(String bookName) {
        Response response = libraryApi.bookBorrowed(bookName);
        Assert.assertEquals(response.statusCode(), 201, "Book not available to borrow");
    }

    @Then("Return the Book {string} availability to the Library")
    public void returnTheBookAvailabilityToTheLibrary(String bookName) {
       Response response = libraryApi.bookReturned(bookName);
        Assert.assertEquals(response.statusCode(), 200);

    }
}
