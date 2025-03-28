package uk.gov.dwp.utils;

import uk.gov.dwp.data.checkboxes.*;
import uk.gov.dwp.data.datamodels.PreviewQuestionsDetails;
import uk.gov.dwp.data.datamodels.disrupt.HousingRFEDetails;
import uk.gov.dwp.data.datamodels.disrupt.PersonDetails;
import uk.gov.dwp.data.datamodels.esoc.DecisionDetails;
import uk.gov.dwp.data.datamodels.esoc.SuspectDetails;
import uk.gov.dwp.data.datamodels.investigations.SetAnActivityDetails;
import uk.gov.dwp.data.dropdownlists.BenefitType;
import uk.gov.dwp.data.radiobuttons.disrupt.EvidenceFrom;
import uk.gov.dwp.datatypes.FunctionType;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

import static uk.gov.dwp.data.CaseWorkerActivities.*;
import static uk.gov.dwp.data.Regions.GB;
import static uk.gov.dwp.data.Regions.NI;
import static uk.gov.dwp.data.TeamLeaderActivities.DISRUPT_TEAM_LEADER_ACTION_REQUESTED;

public class AppDataUtil {

    public static String getBenefitTypeShortName(BenefitType benefitType) {
        return Arrays.stream(benefitType.value.split("[ ]+"))
                .filter(text -> !text.equalsIgnoreCase("and"))
                .map(AppDataUtil::getInitialLetter)
                .collect(Collectors.joining());
    }

    public static String getBenefitTypeShortName(String benefitType) {
        return Arrays.stream(benefitType.split("[ ]+"))
                .filter(text -> !text.equalsIgnoreCase("and"))
                .map(AppDataUtil::getInitialLetter)
                .collect(Collectors.joining());
    }

    public static String getDynamicBenefitTypeActivity(String linkName, BenefitType benefitType) {
        return linkName.replace("<Benefit_Type>", benefitType.shortName);
    }

    public static String getDynamicBenefitTypeActivity(String linkName, String benefitType) {
        return linkName.replace("<Benefit_Type>", AppDataUtil.getBenefitTypeShortName(benefitType));
    }

    public static String getDynamicActivity(String linkName, String replaceWith) {
        return linkName.replace("<Dynamic_Text>", replaceWith);
    }

    public static String getEvidenceExpectedActivity(String replaceWith) {
        return requiredDynamicActivity(EVIDENCE_EXPECTED, replaceWith);
    }

    public static String getVerificationExpectedActivity(String replaceWith) {
        return requiredDynamicActivity(VERIFICATION_EXPECTED, replaceWith);
    }

    private static String requiredDynamicActivity(String activityName, String replaceWith) {
        replaceWith = replaceWith.length() > 20 ? replaceWith.substring(0, 20) : replaceWith;
        return activityName.replace("<Dynamic_Text>", replaceWith);
    }

    private static String getInitialLetter(String text) {
        switch (text) {
            case "Jobseeker's":
                return "JS";
            case "to":
                return "t";
            default:
                return text.substring(0, 1).toUpperCase();
        }
    }

    public static String getCaseID(String caseNumber) {
        return caseNumber.split("-")[1];
    }

    public static String getComplianceCaseNewAcceptNote(
            PreviewQuestionsDetails previewQuestionsDetails, String comments) {
        return previewQuestionsDetails.isProsecuted() || previewQuestionsDetails.isOverPayment3K() ||
                previewQuestionsDetails.isFalseClaimed() ? ". Comment: " + comments : "";
    }

    public static String getFunctionDropDownValue(FunctionType functionType) {
        switch (functionType) {
            case RIT:
                return "RIT";
            case INTERVENTIONS:
                return "Interventions";
            case COMPLIANCE:
                return "Compliance";
            case INVESTIGATIONS:
                return "Investigations";
            default:
                throw new UnsupportedOperationException("Extended due date not supported for: " + functionType);
        }
    }

    public static String getFunctionDropDownValue1(FunctionType functionType) {
        switch (functionType) {
            case RIT:
                return "UC Interventions";
            case INTERVENTIONS:
                return "Interventions";
            case COMPLIANCE:
                return "Compliance";
            case INVESTIGATIONS:
                return "Investigations";
            case CAMPAIGN:
                return "Campaign";
            default:
                throw new UnsupportedOperationException("Extended due date not supported for: " + functionType);
        }
    }

    public static String getDisruptCaseWithClaimantName(String disruptCase, PersonDetails personDetails) {
        String[] caseSplit = disruptCase.split("-");
        return "Disrupt-" + caseSplit[1] + " " + personDetails.getFirstName() + " " + personDetails.getLastName();
    }

    public static String getDisruptReviewDecisionActivity(BenefitType benefitType, DecisionType decisionType) {
        return REVIEW_BENEFIT_DECISION.replace("<Benefit_Type>", benefitType.shortName)
                .replace("<Decision_Type>", decisionType.acronym);
    }

    public static String getDisruptRecordDecisionActivity(BenefitType benefitType, DecisionType decisionType) {
        return RECORD_BENEFIT_DECISION.replace("<Benefit_Type>", benefitType.shortName)
                .replace("<Decision_Type>", decisionType.acronym);
    }

    public static String getDisruptTeamLeaderRequestedActivity(String activity) {
        return DISRUPT_TEAM_LEADER_ACTION_REQUESTED.replace("<Activity>", activity);
    }

    public static String getDisruptReviewTeamLeaderActivity(String activity) {
        return DISRUPT_REVIEW_TEAM_LEADER_ACTION.replace("<Activity>", activity);
    }

    public static String getDisruptEvidenceExpectedActivity(IdentityEvidence identityEvidence, EvidenceFrom evidenceFrom) {
        String identityEvidenceText = identityEvidence.equals(IdentityEvidence.OTHER) ?
                "Other evidence" : identityEvidence.value;
        String dynamicText = identityEvidenceText + " for " + evidenceFrom.value;
        return EVIDENCE_EXPECTED.replace("<Dynamic_Text>", dynamicText);
    }

    public static String getDisruptVerificationExpectedActivity(IdentityEvidence identityEvidence, EvidenceFrom evidenceFrom) {
        String identityEvidenceText = identityEvidence.equals(IdentityEvidence.OTHER) ?
                "Other evidence" : identityEvidence.value;
        String dynamicText = identityEvidenceText + " for " + evidenceFrom.value;
        return VERIFICATION_EXPECTED.replace("<Dynamic_Text>", dynamicText);
    }

    public static String getDisruptEvidenceExpectedActivity(UKResidencyEvidence ukResidencyEvidence, EvidenceFrom evidenceFrom) {
        String identityEvidenceText = ukResidencyEvidence.equals(UKResidencyEvidence.OTHER) ?
                "Other evidence" : ukResidencyEvidence.value;
        String dynamicText = identityEvidenceText + " for " + evidenceFrom.value;
        return EVIDENCE_EXPECTED.replace("<Dynamic_Text>", dynamicText);
    }

    public static String getDisruptVerificationExpectedActivity(UKResidencyEvidence ukResidencyEvidence, EvidenceFrom evidenceFrom) {
        String identityEvidenceText = ukResidencyEvidence.equals(UKResidencyEvidence.OTHER) ?
                "Other evidence" : ukResidencyEvidence.value;
        String dynamicText = identityEvidenceText + " for " + evidenceFrom.value;
        return VERIFICATION_EXPECTED.replace("<Dynamic_Text>", dynamicText);
    }

    public static String getDisruptEvidenceExpectedActivity(ChildrenEvidence childrenEvidence, String childName) {
        String identityEvidenceText = childrenEvidence.equals(ChildrenEvidence.OTHER) ?
                "Other evidence" : childrenEvidence.value;
        String dynamicText = identityEvidenceText + " for " + childName;
        return EVIDENCE_EXPECTED.replace("<Dynamic_Text>", dynamicText);
    }

    public static String getDisruptVerificationExpectedActivity(ChildrenEvidence childrenEvidence, String childName) {
        String identityEvidenceText = childrenEvidence.equals(ChildrenEvidence.OTHER) ?
                "Other evidence" : childrenEvidence.value;
        String dynamicText = identityEvidenceText + " for " + childName;
        return VERIFICATION_EXPECTED.replace("<Dynamic_Text>", dynamicText);
    }

    public static String getDisruptEvidenceExpectedActivity(HousingRFEDetails housingRFEDetails, String propertyName) {
        return EVIDENCE_EXPECTED.replace("<Dynamic_Text>",
                getHouseRFEEvidence(housingRFEDetails, propertyName));
    }

    public static String getDisruptVerificationExpectedActivity(HousingRFEDetails housingRFEDetails, String propertyName) {
        return VERIFICATION_EXPECTED.replace("<Dynamic_Text>",
                getHouseRFEEvidence(housingRFEDetails, propertyName));
    }

    public static String getDisruptEvidenceExpectedActivity(String evidence, EvidenceFrom evidenceFrom) {
        return EVIDENCE_EXPECTED.replace("<Dynamic_Text>", evidence + " for " + evidenceFrom.value);
    }

    public static String getDisruptVerificationExpectedActivity(String evidence, EvidenceFrom evidenceFrom) {
        return VERIFICATION_EXPECTED.replace("<Dynamic_Text>", evidence + " for " + evidenceFrom.value);
    }

    private static String getHouseRFEEvidence(HousingRFEDetails housingRFEDetails, String propertyName) {
        HousingEvidence housingEvidence = housingRFEDetails.getHousingEvidence();
        String identityEvidenceText = "";
        if (!housingEvidence.equals(HousingEvidence.OTHER) &&
                !housingEvidence.equals(HousingEvidence.UTILITY_BILLS))
            identityEvidenceText = housingEvidence.value;
        if (housingEvidence.equals(HousingEvidence.OTHER))
            identityEvidenceText = "Other evidence";
        if (housingEvidence.equals(HousingEvidence.UTILITY_BILLS))
            identityEvidenceText = housingRFEDetails.getUtilityBill() + " bills";
        return identityEvidenceText + " for " + propertyName;
    }

    public static String getESOCReviewDecisionActivity(DecisionDetails decisionDetails) {
        return ESOC_REVIEW_DECISION_SINGLE.replace("<SUSPECT_FULL_NAME>", decisionDetails.getFullName())
                .replace("<BENEFIT_TYPE>", getBenefitTypeShortName(decisionDetails.getBenefitType()));
    }

    public static String getESOCRecordDecisionActivity(DecisionDetails decisionDetails) {
        return ESOC_RECORD_DECISION_SINGLE.replace("<SUSPECT_FULL_NAME>", decisionDetails.getFullName())
                .replace("<BENEFIT_TYPE>", getBenefitTypeShortName(decisionDetails.getBenefitType()));
    }

    public static String getESOCReviewDecisionActivity(String benefitType) {
        return ESOC_REVIEW_DECISION_MULTIPLE.replace("<BENEFIT_TYPE>", getBenefitTypeShortName(benefitType));
    }

    public static String getESOCRecordDecisionActivity(String benefitType) {
        return ESOC_RECORD_DECISION_MULTIPLE.replace("<BENEFIT_TYPE>", getBenefitTypeShortName(benefitType));
    }

    public static String getReviewCourtAction(SuspectDetails suspectDetails) {
        return REVIEW_COURT_ACTIONS.replace("<full name>", suspectDetails.getFullName());
    }

    public static String getReviewSetAnActivity(SetAnActivityDetails setAnActivityDetails) {
        return REVIEW_SET_AN_ACTIVITY.replace("<ACTIVITY_NAME>", setAnActivityDetails.getActivityName());
    }

    public static String getSurveillanceInformationActivity(String fromSource) {
        return SURVEILLANCE_INFORMATION_EXCPECTED.replace("<FROM_SOURCE>", fromSource);
    }

    public static String getFIReferralQueue(String region) {
        switch (region) {
            case GB:
                return "FI_Referral_queue";
            case NI:
                return "FI_Referral_queue_NI";
            default:
                throw new RuntimeException(region + " not available");
        }
    }
}