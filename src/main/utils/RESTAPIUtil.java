package uk.gov.dwp.utils;

import com.google.common.collect.Sets;
import io.restassured.response.Response;
import uk.gov.dwp.data.ReferralPackageData;
import uk.gov.dwp.data.datamodels.Function;
import uk.gov.dwp.data.datamodels.User;
import uk.gov.dwp.exceptions.CaseNotAssignedException;
import uk.gov.dwp.exceptions.FunctionNotSupportedException;
import uk.gov.dwp.exceptions.ReferralAPIException;
import uk.gov.dwp.webdriver.configuration.TestConfigHelper;

import java.util.*;

import static io.restassured.RestAssured.given;
import static uk.gov.dwp.utils.gdpr.GDPRQueries.getIdFromRefferalTable;

public class RESTAPIUtil {

    private static Optional<String> getCaseIDFromResponse(Response response) {
        return Optional.ofNullable(response)
                .filter(apiResponse -> apiResponse.getStatusCode() == 200)
                .map(apiResponse -> apiResponse.path("Referral.ReferralIdentifier").toString())
                .filter(caseId -> !caseId.trim().isEmpty());
    }

    /**
     * Method to return GB region case ID
     * @param function
     * @return
     * @throws Throwable
     */
    public static String getCaseID(Function function) throws Throwable {
        String referralBody = ReferralPackageData.get(function.getReferral());
        Response response = postReferral(function, referralBody);
        return getCaseIDFromResponse(response)
                .orElseThrow(() -> new ReferralAPIException(referralBody, response));
    }

    /**
     * Method to return Case ID based on Region parameter
     * @param region GB and NI
     * @param function RIT,Compliance,Investigations,....
     * @return Case ID based on region
     * @throws Throwable
     */
    public static String getCaseID(String region, Function function) throws Throwable {
        switch (region) {
            case "GB":
                return getCaseID(function);
            case "NI":
                return getNICaseID(function);
            default:
                throw new RuntimeException();

        }
    }

    private static String getNICaseID(Function function) throws Throwable {
        String referralBody = ReferralPackageData.getNIReferral(function.getReferral());
        Response response = postReferral(function, referralBody);
        return getCaseIDFromResponse(response)
                .orElseThrow(() -> new ReferralAPIException(referralBody, response));
    }

    public static List<String> getCaseIDs(Function function, User caseWorker, int noOfCases) throws Throwable {
        List<String> caseIds = new ArrayList<>();
        for (int i = 0; i < noOfCases; i++) {
            String referralBody = ReferralPackageData.get(function.getReferral());
            Response response = postReferral(caseWorker, referralBody);
            caseIds.add(getCaseIDFromResponse(response).orElseThrow(() -> new ReferralAPIException(referralBody, response)));
        }
        return caseIds;
    }

    public static String getCaseID(Function function, User caseWorker) throws Throwable {
        String referralBody = ReferralPackageData.get(function.getReferral());
        Response response = postReferral(caseWorker, referralBody);
        return getCaseIDFromResponse(response).orElseThrow(() -> new ReferralAPIException(referralBody, response));

    }

    public static String getCaseIDFromCisEnvironment(Function function, String nino) throws Throwable {
        String referralBody = ReferralPackageData.get(function.getReferral(), nino);
        Response response = postReferral(function.getCisUser(), referralBody);
        return getCaseIDFromResponse(response)
                .orElseThrow(() -> new ReferralAPIException(referralBody, response));
    }

    public static void assignRFICasesToIGOCaseWorker(List<String> rfiCaseIDs, User rfiUser) throws CaseNotAssignedException {
        Set<String> actualCases = Sets.newHashSet(rfiCaseIDs);
        for (String caseID : actualCases) {
            assignRFICaseToIGOCaseWorker(caseID, rfiUser);
        }
    }

    public static void assignRFICaseToIGOCaseWorker(String caseID, User rfiUser) throws CaseNotAssignedException {
        String referralApiEndpoint = TestConfigHelper.get().getBaseUrl()
                + TestConfigHelper.get().getTestConfigItem("restApiReassignRFICaseUrl");
        Response response = given().auth().basic(rfiUser.getUserID(),
                        rfiUser.getPassword()).queryParam("caseID", caseID)
                .queryParam("assignOperatorID", rfiUser.getUserID()).post(referralApiEndpoint);
        Optional.ofNullable(response).filter(apiResponse -> apiResponse.statusCode() == 200)
                .orElseThrow(() -> new CaseNotAssignedException(caseID, rfiUser.getUserID(),
                        Objects.requireNonNull(response).statusCode()));
    }

    public static String createResolvedDuplicateCase(Function function, String referralBody, String referralIdentifier)
            throws Throwable {
        postReferral(function, referralBody);
        return String.valueOf(getIdFromRefferalTable(referralIdentifier, "Resolved-Duplicate"));
    }

    public static String createRiskIDNotFoundCase(Function function, String referralBody, String referralIdentifier)
            throws Throwable {
        postReferral(function, referralBody);
        return String.valueOf(getIdFromRefferalTable(referralIdentifier, "Resolved-ExceptionRiskIDNotFound"));
    }

    public static Response postReferral(Function function, String referralBody) throws FunctionNotSupportedException {
        String referralApiEndpoint = TestConfigHelper.get().getBaseUrl()
                + TestConfigHelper.get().getTestConfigItem("restApiCreateCaseUrl");
        return given().auth().basic(function.getCaseWorker().getUserID(), function.getCaseWorker().getPassword())
                .contentType("application/xml")
                .body(referralBody)
                .when().post(referralApiEndpoint);
    }

    public static Response postReferral(User user, String referralBody) {
        String referralApiEndpoint = TestConfigHelper.get().getBaseUrl()
                + TestConfigHelper.get().getTestConfigItem("restApiCreateCaseUrl");
        return given().auth().basic(user.getUserID(), user.getPassword())
                .contentType("application/xml")
                .body(referralBody)
                .when().post(referralApiEndpoint);
    }


    public static String getDisruptCase(List<String> disruptUploadFileDetails, User caseWorker) throws ReferralAPIException {
        return getDisruptCaseForInProgressCampaign(disruptUploadFileDetails, caseWorker, false);
    }

    public static String getDisruptCaseForInProgressCampaign(List<String> disruptUploadFileDetails,
                                                             User caseWorker) throws ReferralAPIException {
        return getDisruptCaseForInProgressCampaign(disruptUploadFileDetails, caseWorker, true);
    }

    private static String getDisruptCaseForInProgressCampaign(
            List<String> disruptUploadFileDetails, User caseWorker, boolean skipBacklog) throws ReferralAPIException {
        String referralApiEndpoint = TestConfigHelper.get().getBaseUrl()
                + TestConfigHelper.get().getTestConfigItem("restApiDisruptCreateCaseUrl");
        Response response = given().auth()
                .basic(caseWorker.getUserID(), caseWorker.getPassword())
                .queryParam("CRCReference", disruptUploadFileDetails.get(0))
                .queryParam("ContractID", disruptUploadFileDetails.get(1))
                .queryParam("SkipBacklog", skipBacklog)
                .post(referralApiEndpoint);
        Optional.ofNullable(response).filter(apiResponse -> apiResponse.statusCode() == 200)
                .orElseThrow(() -> new ReferralAPIException(disruptUploadFileDetails.get(0), response));
        return response.headers().get("caseid").getValue();
    }

}