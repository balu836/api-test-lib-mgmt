package org.example;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;

public class LibraryApiTests {

    private LibraryApi libraryApi;

    @BeforeTest
    public void setUp() throws IOException {
        libraryApi = LibraryApi.get();
    }

    //Transaction to check availability, borrow and return
    @Test(dataProvider = "books")
    public void test_Book_Availability_Borrow_And_Return_Transaction(String bookName) {
        //Verify the Book availability
        ArrayList arrayList = libraryApi.bookAvailability(bookName);
        Assert.assertNotEquals(arrayList.get(0), 0, "Books not available");
        //Borrow book successfully
        Response response = libraryApi.bookBorrowed(bookName);
        Assert.assertEquals(response.statusCode(), 201, "Book not available to borrow");
        //Return the book if borrowed
        response = libraryApi.bookReturned(bookName);
        Assert.assertEquals(response.statusCode(), 200);

    }

    //Below test to test each API separately

    @Test
    public void test_To_Verify_User_Able_To_Book() {
        Response response = libraryApi.bookBorrowed("The Very Busy Spider");
        Assert.assertEquals(response.statusCode(), 201, "Book not available to borrow");
    }

    @Test
    public void test_To_Verify_Availability_Of_Book() {
        ArrayList arrayList = libraryApi.bookAvailability("Little Blue Truck");
        Assert.assertFalse(arrayList.get(0).equals(0), "Books not available");
    }

    @Test
    public void test_To_Verify_Return_Of_Book() {
        Response response = libraryApi.bookReturned("The Very Busy Spider");
        Assert.assertEquals(response.statusCode(), 200);
    }


    @DataProvider(name = "books")
    public Object[][] booksData() {
        return new Object[][]{
                {"The Very Busy Spider"},
                {"Pete the Cat: Rocking in My School Shoes"},
        };
    }
}


