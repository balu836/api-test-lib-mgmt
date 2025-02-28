# Created by bala.matam at 28/02/2025
Feature: Library management api tests
  # To verify the library management API functionality with different transactions
  Background:
    Given Login Library Management API

  Scenario Outline: Test To check the Book availability, Borrow and Return to the Library Transaction
    Given Check the Book "<BookTitle>" availability in the Library
    When Borrow the Book  "<BookTitle>" availability from the Library
    Then Return the Book "<BookTitle>" availability to the Library

    Examples:
      | BookTitle            |
      | The Very Busy Spider |
      | Little Blue Truck    |
