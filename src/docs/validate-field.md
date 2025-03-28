## Field Validation


### Steps to add field validation to a test scenario

In this example we will add field validation to `InvestigationsEndToEndTests.testRecordDecisionWithAllBenefitAffectingTypesSelected` test.

1) Annotate web elements with appropriate field type.
 for example ;
 
    in page object : RecordAnOverPaymentPage
    
    a)  To validate blank entries in mandatory field
    
    ```java
    @EvaluateField(ofType = FieldType.SUBMIT_FIELD)
    @FindBy(css = "button[data-test-id='btnSave']")
    private WebElement submitButton;
   ```

    b)  To validate text value in monetary field
    
    ```java
   @EvaluateField(ofType = FieldType.MONETARY_FIELD)
   @FindBy(xpath = "//input[@data-test-id='inpAmount']")
   private List<WebElement> amountTextboxes;
   ```
   
    c) To validate date-time field for past and future date constraints as well as text content
    
    ```java
   @EvaluateField(ofType = FieldType.DATE_TIME_FIELD, ofPeriod = Period.FROM_DATE_FIELD, withValue = EventDate.FUTURE_DATE)
   @FindBy(xpath = "//input[@data-test-id='inpFrom']")
   private List<WebElement> fromTextboxes;
    ```
   
   d) To validate dynamic field for blank entries (extracted from : `uk.gov.dwp.pageobjects.caseworker.RecordMVAForThisBenefitPage`)
   
   ```java
       @EvaluateField(ofType = FieldType.TRIGGER_DYNAMIC_FIELD)
       @EvaluateField(ofType = FieldType.MONETARY_FIELD)
       @FindBy(css = "input[data-test-id='inpMVA']")
       private WebElement mvaAmountTextbox;
   
       @EvaluateDynamicField(triggeredBy = "mvaAmountTextbox", withValue = "45.00")
       @FindBy(css = "input[data-test-id='inpMVAFromDate']")
       private WebElement fromDateTextbox;
   
       @EvaluateDynamicField(triggeredBy = "mvaAmountTextbox", withValue = "45.00")
       @FindBy(css = "select[data-test-id='drdMVAClassification']")
       private WebElement mvaTypeDropdown;
   ```
      
2) Create an instance of `PageField` in page object.
 
3) Add the method, `tryToValidateFields` in page object returning a reference to page object, to support fluent interface
 
     ```java
     public RecordAnOverPaymentPage tryToValidateFields(ErrorMessageStore errorMessageStore)
                throws PageNavigationException {
            pageField.tryToEvaluateFields(errorMessageStore, this);
            super.validatePage();
            return this;
        }
    ```

4) Call the method `tryToValidateFields` in Tests class, right after the page loads.
 
     ```java
     overPaymentPage = myActivitiesPage
                    .clickOnRecordDecisionOverpayment(caseId, testData.getBenefitType())
                    .tryToValidateFields(errorMessageStore)
                    .addOverPayments(testData.getAllOverPaymentDetails());
            overPaymentPage.saveAndContinue();
    ```

5) Add respective assertions towards the end of the test scenario in Tests class

    ```java
        assertValidationMessages("Mandatory Field validation messages are not displayed As Expected",
                    errorMessageStore.getErrorMessagesForMandatoryFields(),
                    expectedErrorMessages.mandatoryFields(errorMessageStore));
    
        assertValidationMessages("Monetary Field validation messages are not displayed As Expected",
                    errorMessageStore.getErrorMessagesForMonetaryFields(),
                    expectedErrorMessages.monetaryFields(errorMessageStore));
    
        assertValidationMessages("EventDate Field validation messages are not displayed As Expected",
                    errorMessageStore.getErrorMessagesForDateTimeFields(),
                    expectedErrorMessages.dateTimeFields(errorMessageStore));
    ```

6) Add the expected error messages in field-error-messages.json file.
 
     ```json
    {
          "pageName": "uk.gov.dwp.pageobjects.caseworker.RecordAnOverPaymentPage",
          "errorMessages": {
            "dynamic-fields": [],
            "datetime-fields": [
              "michael jackson is not a valid date/time value",
              "michael jackson is not a valid date/time value",
              "From Date should be before To Date",
              "Cannot have a future To date"
            ],
            "mandatory-fields": [
              "Enter the amount",
              "Select an offence",
              "Select the classification",
              "Select is it recoverable",
              "From is a required field",
              "To is a required field"
            ],
            "monetary-fields": [
              "michael jackson is not a valid decimal value",
              "Invalid value specified for Amount. Value doesn't adhere to the Validate: ValidateAmount"
            ]
          }
        }
    ```