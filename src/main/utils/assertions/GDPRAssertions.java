package uk.gov.dwp.utils.assertions;

import org.hamcrest.Matchers;
import uk.gov.dwp.data.GDPRRetentionPolicy;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.dwp.utils.gdpr.GDPRMatchers.containsOnly;
import static uk.gov.dwp.utils.gdpr.GDPRMatchers.isAnonymised;
import static uk.gov.dwp.utils.gdpr.GDPRQueries.*;

public class GDPRAssertions {

    public static void assertGDPRPolicyDBQueryDetails(String caseID, String nino) throws Exception {
        assertThat(caseID.concat(": Case has not anonymized"), getCaseAnonymityStatus(caseID), containsOnly("true"));
        assertThat(caseID.concat(": Case has not anonymized"), getReferralData(nino), containsOnly(null));
        assertThat(caseID.concat(": Case has not anonymized"), getBacklogPersonalData(caseID), isAnonymised());
        assertThat(caseID.concat(": Case has not anonymized"), getInvestigationPersonalData(caseID), isAnonymised());
        assertThat(caseID.concat(": Case has not anonymized"), countSubjectPersonalData(nino), Matchers.is(0L));
        assertThat(caseID.concat(": Case has not anonymized"), getAuditPersonalData(caseID), isAnonymised());
        assertThat(caseID.concat(": Case has not anonymized"), countPolicyActions(caseID), Matchers.is(0L));
        assertThat("Documents not anonymize in Link AttachmentTable",
                isDocumentAvailableInLinkAttachmentTable(caseID),is(0L));
        assertThat("Documents not anonymize in data work attachable table",
                isDocumentAvailableInDataWorkAttachTable(caseID),is(0L));
    }

    public static void assertGDPRPolicyApplied(String caseId, GDPRRetentionPolicy gdprRetentionPolicy) throws Exception {
        assertThat("Retention policy does not appear on table as expected for the case : " + caseId,
                checkRetentionPolicy(caseId), containsOnly(gdprRetentionPolicy.value));
    }


    public static void assertReferralIdDetailsForExceptionCase(String referralId) throws Exception {
        assertThat("Referral Details not anonymized", getReferralDataByReferralId(referralId), containsOnly(null));
        assertThat("Details are not deleted", countPolicyActions(referralId), is(0L));
        assertThat("Details not present", countReferralAuditEntry(referralId), is(1L));
    }

    public static void assertIsDocumentsAnonymized(String caseId) throws Exception {
        assertThat("Documents not anonymize in Link AttachmentTable",
                isDocumentAvailableInLinkAttachmentTable(caseId),is(0L));
        assertThat("Documents not anonymize in data work attachable table",
                isDocumentAvailableInDataWorkAttachTable(caseId),is(0L));
    }
}
