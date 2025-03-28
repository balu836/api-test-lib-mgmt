package uk.gov.dwp.utils.gdpr;

import uk.gov.dwp.utils.database.DatabaseClient;
import uk.gov.dwp.utils.database.QueryResult;

import java.math.BigDecimal;
import java.util.Objects;

import static uk.gov.dwp.utils.database.QueryResult.Column;


public class GDPRQueries {

    public static long countPolicyActions(String caseId) throws Exception {
        String queryTemplate = "SELECT COUNT(*) FROM dataschema.pr_ldm_data_policyactions WHERE handle=?";
        if (caseId.contains("I-")) {
            return getLongValue(DatabaseClient.get()
                    .executeQuery(queryTemplate, "DWP-FES-WORK " + caseId)
                    .getFirst());
        } else if (caseId.contains("DISRUPT-")) {
            return getLongValue(DatabaseClient.get()
                    .executeQuery(queryTemplate, "DWP-FES-WORK-DISRUPT " + caseId)
                    .getFirst());
        } else {
            return getLongValue(DatabaseClient.get()
                    .executeQuery(queryTemplate, "DWP-Int-Referral " + caseId)
                    .getFirst());
        }
    }

    public static Column<String> checkRetentionPolicy(String caseId) throws Exception {
        String queryTemplate = "SELECT RETENTIONPOLICY FROM dataschema.pr_ldm_data_policyactions WHERE handle=?";
        if (caseId.contains("I-")) {
            return DatabaseClient.get()
                    .executeQuery(queryTemplate, "DWP-FES-WORK " + caseId)
                    .getColumn("RETENTIONPOLICY");
        } else if (caseId.toUpperCase().contains("DISRUPT-")) {
            return DatabaseClient.get()
                    .executeQuery(queryTemplate, "DWP-FES-WORK-DISRUPT " + caseId.toUpperCase())
                    .getColumn("RETENTIONPOLICY");
        }else if (caseId.toUpperCase().contains("OP-")) {
            return DatabaseClient.get()
                    .executeQuery(queryTemplate, "DWP-FES-WORK-ESOC " + caseId.toUpperCase())
                    .getColumn("RETENTIONPOLICY");
        } else {
            return DatabaseClient.get()
                    .executeQuery(queryTemplate, "DWP-Int-Referral " + caseId)
                    .getColumn("RETENTIONPOLICY");
        }
    }

    public static Column<String> getCaseAnonymityStatus(String caseId) throws Exception {
        String queryTemplate = "SELECT ISCASEANONYMISED FROM dataschema.pc_dwp_fes_work WHERE pxinsname=?";
        return DatabaseClient.get()
                .executeQuery(queryTemplate, caseId)
                .getColumn("ISCASEANONYMISED");
    }

    public static Column<String> getReferralData(String nino) throws Exception {
        String queryTemplate = "SELECT REFERRALDATA FROM businessdataschema.referral WHERE nino=?";
        return DatabaseClient.get()
                .executeQuery(queryTemplate, nino)
                .getColumn("REFERRALDATA");
    }

    public static Column<String> getBacklogPersonalData(String caseId) throws Exception {
        String queryTemplate = "SELECT CLAIMANTNINO FROM businessdataschema.backlog where investigationid=?";
        return DatabaseClient.get()
                .executeQuery(queryTemplate, getIDFromCaseId(caseId))
                .getColumn("CLAIMANTNINO");
    }

    public static Column<String> getInvestigationPersonalData(String caseId) throws Exception {
        String queryTemplate = "SELECT SUBJECTIDENTIFIER FROM businessdataschema.investigation where ID=?";
        return DatabaseClient.get().executeQuery(queryTemplate, getIDFromCaseId(caseId)).getColumn("SUBJECTIDENTIFIER");
    }

    public static QueryResult getAuditPersonalData(String caseId) throws Exception {
        String queryTemplate = "SELECT NINO, INTERVIEWRESPONSE, CLOSURECOMMENT, REFERRALSUMMARY, TELEPHONENUMBER " +
                "FROM businessdataschema.dwp_data_audit where CASEID=?";
        return DatabaseClient.get().executeQuery(queryTemplate, caseId)
                .mapIf(Objects::isNull, value -> "XXXXXX");
    }

    public static long countSubjectPersonalData(String nino) throws Exception {
        String queryTemplate = "SELECT COUNT(*) FROM businessdataschema.subject where NINO=?";
        return getLongValue(DatabaseClient.get().executeQuery(queryTemplate, nino).getFirst());
    }

    public static String getPostCodeData(String caseId) throws Exception {
        String queryTemplate = "SELECT POSTCODE FROM businessdataschema.backlog where INVESTIGATIONID=?";
        return DatabaseClient.get()
                .executeQuery(queryTemplate, getIDFromCaseId(caseId))
                .getColumn("POSTCODE").get(0).toString();
    }

    public static long getIdFromRefferalTable(String referralIdentifier, String status, String exceptionReason) throws Exception {
        String queryTemplate = "SELECT ID FROM businessdataschema.referral " +
                "where REFERRALIDENTIFIER=? and STATUS=? and EXCEPTIONREASON=?";
        return getLongValue(DatabaseClient.get().executeQuery(queryTemplate,
                referralIdentifier, status, exceptionReason).getFirst());
    }

    public static long getIdFromRefferalTable(String referralIdentifier, String status) throws Exception {
        String queryTemplate = "SELECT ID FROM businessdataschema.referral where REFERRALIDENTIFIER=? and STATUS=?";
        return getLongValue(DatabaseClient.get().executeQuery(queryTemplate, referralIdentifier, status).getFirst());
    }

    private static String getIDFromCaseId(String caseId) {
        return caseId.split("-")[1];
    }

    public static Column<String> getReferralDataByReferralId(String refferalId) throws Exception {
        String queryTemplate = "SELECT REFERRALDATA FROM businessdataschema.referral WHERE ID=?";
        return DatabaseClient.get()
                .executeQuery(queryTemplate, Integer.parseInt(refferalId))
                .getColumn("REFERRALDATA");
    }

    public static long countReferralAuditEntry(String referralid) throws Exception {
        String queryTemplate = "Select count(*) from businessdataschema.referralaudit " +
                "where auditcomponent='DataRetention' and referralid=?";
        return getLongValue(DatabaseClient.get()
                .executeQuery(queryTemplate, Integer.parseInt(referralid))
                .getFirst());
    }

    public static long isDocumentAvailableInLinkAttachmentTable(String caseId) throws Exception {
        String queryTemplate = "SELECT Count(*) FROM DATASCHEMA.PC_LINK_ATTACHMENT WHERE pxlinkedreffrom=?";
        Object attachmentCount = DatabaseClient.get()
                .executeQuery(queryTemplate, "DWP-FES-WORK " + caseId).getFirst();
        return getLongValue(attachmentCount);
    }

    public static long isDocumentAvailableInDataWorkAttachTable(String caseId) throws Exception {
        String queryTemplate = "SELECT Count(*) FROM DATASCHEMA.pc_data_workattach where pxrefobjectkey=?";
        Object attachmentCount = DatabaseClient.get()
                .executeQuery(queryTemplate, "DWP-FES-WORK " + caseId).getFirst();
        return getLongValue(attachmentCount);
    }

    private static Long getLongValue(Object object) {
        if (object instanceof BigDecimal) {
            return ((BigDecimal) object).longValue();
        }
        return (long) object;
    }
}
