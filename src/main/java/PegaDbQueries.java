//package uk.gov.dwp.utils.database;
//
//import java.sql.SQLException;
//import java.util.HashMap;
//
//public class PegaDbQueries {
//
//    private static final String DELETE_TEAM_DATA_IN_PRADMIN_TABLE_QUERY =
//            "DELETE FROM dataschema.pr_data_admin WHERE pxobjclass='Data-Admin-WorkGroup' AND pyLabel=?";
//
//    private static final String DELETE_TEAM_DATA_IN_TEAMDATA_TABLE_QUERY =
//            "DELETE FROM businessdataschema.teamdata WHERE team=?";
//
//    private static final String DELETE_USER_DATA_IN_LINKUSERDATA_RISKID_TABLE_QUERY =
//            "DELETE FROM businessdataschema.link_userdata_riskid WHERE staffid=?";
//
//    private static final String DELETE_USER_DATA_IN_LINKUSERDATA_SKILLDATA_TABLE_QUERY =
//            "DELETE FROM businessdataschema.link_userdata_skilldata WHERE staffid=?";
//
//    private static final String DELETE_USER_DATA_IN_LINKUSERDATA_ROLLDATA_TABLE_QUERY =
//            "DELETE FROM  businessdataschema.link_userdata_roledata WHERE staffid=?";
//
//    private static final String DELETE_USER_DATA_IN_LINKUSERDATA_TEAMDATA_TABLE_QUERY =
//            "DELETE FROM  businessdataschema.link_userdata_roledata WHERE staffid=?";
//
//    private static final String DELETE_USER_DATA_IN_PROPERATORS_TABLE_QUERY =
//            "DELETE FROM  dataschema.pr_operators WHERE pyuseridentifier=?";
//
//    private static final String DELETE_USER_DATA_IN_USERDATA_TABLE_QUERY =
//            "DELETE FROM businessdataschema.userdata WHERE staffID=?";
//
//    private static final String DELETE_ALLOCATIONGROUP_DATA_IN_ALLOCATIONGROUP_TABLE_QUERY =
//            "DELETE FROM businessdataschema.allocationgroup where Description=?";
//
//    private static final String DELETE_ALLOCATIONGROUP_DATA_IN_ALLOCATIONGROUPPOSTCODEDATA_TABLE_QUERY =
//            "DELETE FROM businessdataschema.link_backlog_allocationgroup WHERE allocationgroupid="
//                    + "(SELECT allocationgroupid FROM businessdataschema.allocationgroup WHERE Description=?)";
//    private static final String DISRUPT_RECORD_CASE_OUTCOME =
//            "select * from businessdataschema.index_disruptoutcome where CaseID = ?";
//
//    private static final String REFFERAL_TABLE_QUERY =
//            "select * from businessdataschema.referral where NINO = ? order by CREATEDTIMESTAMP DESC";
//
//    private static final String BACKLOG_REFFERAL_TABLE_QUERY =
//            "SELECT * FROM BUSINESSDATASCHEMA.BACKLOG where CLAIMANTNINO = ?";
//
//    private static final String LINK_REFFERAL_TABLE_QUERY =
//            "SELECT * FROM BUSINESSDATASCHEMA.LINK_REFERRAL_BACKLOG where BACKLOGID = ?";
//
//    private static final String RETENTION_POLICY_TABLE_QUERY =
////            "SELECT * FROM Dataschema.PR_LDM_DATA_POLICYACTIONS where HANDLE like = ?";
//    "Select RETENTIONPOLICY FROM Dataschema.PR_LDM_DATA_POLICYACTIONS where HANDLE like = ?";
//
//    //Using this reference for particular test case
//    private static final String RESOLVED_CRE_TABLE_QUERY =
//            "SELECT * from  businessdataschema.referral where ReferralIdentifier like 'CRE%' and status like 'Resolved%' and NINO  = 'AB173860A' order by CREATEDTIMESTAMP DESC";
//
//    private static final String RESOLVED_NONCRE_TABLE_QUERY =
//            "select * from businessdataschema.referral where Status ='Resolved' and SourceReferralIdentifier like 'A%' order by CREATEDTIMESTAMP DESC";
//
//    private static final String DISRUPT_CAMPAIGN_STATUS =
//            "select * from businessdataschema.link_campaignstatus where CampaignName = ?";
//
//    private static final String BENEFIT_TYPE_CODE =
//            "select benefittype from businessdataschema.backlog where claimantnino = 'AB307114A'";
//
//    private static final String FRAIMS_CASE_ID_REPORTABLE =
//            "select  * from  dataschema.PC_DWP_FES_WORK where FRAIMSCASEID = ?";
//
//    public static void deleteUserDataInPegaDBTables(String staffId) throws SQLException {
//        DatabaseClient.get().execute(DELETE_USER_DATA_IN_LINKUSERDATA_RISKID_TABLE_QUERY, staffId);
//        DatabaseClient.get().execute(DELETE_USER_DATA_IN_LINKUSERDATA_SKILLDATA_TABLE_QUERY, staffId);
//        DatabaseClient.get().execute(DELETE_USER_DATA_IN_LINKUSERDATA_ROLLDATA_TABLE_QUERY, staffId);
//        DatabaseClient.get().execute(DELETE_USER_DATA_IN_LINKUSERDATA_TEAMDATA_TABLE_QUERY, staffId);
//        DatabaseClient.get().execute(DELETE_USER_DATA_IN_PROPERATORS_TABLE_QUERY, staffId);
//        DatabaseClient.get().execute(DELETE_USER_DATA_IN_USERDATA_TABLE_QUERY, staffId);
//    }
//
//    public static void deleteTeamDataInPegaDBTables(String teamName) throws SQLException {
//        DatabaseClient.get().execute(DELETE_TEAM_DATA_IN_PRADMIN_TABLE_QUERY, teamName);
//        DatabaseClient.get().execute(DELETE_TEAM_DATA_IN_TEAMDATA_TABLE_QUERY, teamName);
//    }
//
//    public static void deleteAllocationGroupDataPegaDBTables(String allocationGroupName) throws SQLException {
//        DatabaseClient.get().execute(DELETE_ALLOCATIONGROUP_DATA_IN_ALLOCATIONGROUP_TABLE_QUERY, allocationGroupName);
//        DatabaseClient.get().execute(DELETE_ALLOCATIONGROUP_DATA_IN_ALLOCATIONGROUPPOSTCODEDATA_TABLE_QUERY, allocationGroupName);
//    }
//
//    public static String getDisruptCaseOutcomeFromDB(String disruptCaseID) throws Exception {
//        return DatabaseClient.get().executeQuery(DISRUPT_RECORD_CASE_OUTCOME, disruptCaseID)
//                .getColumnValue("OUTCOMETYPE");
//    }
//
//    public static void waitUntillRecordReachesReferenceTable(String NINO) throws Exception {
//         DatabaseClient.get().waitUntilRefferalRecordExistsInDB(REFFERAL_TABLE_QUERY,NINO);
//    }
//
//    public static void waitUntillRecordReachesBackLogTable(String NINO) throws Exception {
//        DatabaseClient.get().waitUntilRefferalRecordExistsInDB(BACKLOG_REFFERAL_TABLE_QUERY,NINO);
//    }
//
//    public static String getBackLogIdFromDB(String NINO) throws Exception {
//        return DatabaseClient.get().executeQuery(BACKLOG_REFFERAL_TABLE_QUERY, NINO)
//                .getColumnValue("BACKLOGID");
//    }
//
//    public static void waitUntillRefferalReachesLinkedCasesTable(String backLogId) throws Exception {
//        DatabaseClient.get().waitUntilRefferalRecordExistsInDB(LINK_REFFERAL_TABLE_QUERY,backLogId);
//    }
//
//    public static String getPriorityWeightingFromDB(String NINO) throws Exception {
//        return DatabaseClient.get().executeQuery(BACKLOG_REFFERAL_TABLE_QUERY, NINO)
//                .getColumnValue("PRIORITYWEIGHTING");
//    }
//
//    public static String getStatusFromRefferalTable(String NINO) throws Exception {
//        return DatabaseClient.get().executeQuery(REFFERAL_TABLE_QUERY, NINO)
//                .getColumnValue("STATUS");
//    }
//
//    public static HashMap<String,String> getResolvedCRECaseDetails() throws Exception {
//        HashMap<String,String> resolvedCRECaseValues = new HashMap<>();
//        resolvedCRECaseValues.put("NINO",DatabaseClient.get().executeQuery(RESOLVED_CRE_TABLE_QUERY)
//                .getColumnValue("NINO"));
//        return resolvedCRECaseValues;
//    }
//
//    public static HashMap<String,String> getResolvedNONCRECaseDetails() throws Exception {
//        HashMap<String,String> resolvedCRECaseValues = new HashMap<>();
//        resolvedCRECaseValues.put("NINO",DatabaseClient.get().executeQuery(RESOLVED_NONCRE_TABLE_QUERY)
//                .getColumnValue("NINO"));
//        resolvedCRECaseValues.put("RISKID",DatabaseClient.get().executeQuery(RESOLVED_NONCRE_TABLE_QUERY)
//                .getColumnValue("RISKID"));
//        return resolvedCRECaseValues;
//    }
//
//
//    public static String getCampaignStatusFromCampaignStatusTable(String campaignName) throws Exception {
//        return DatabaseClient.get().executeQuery(DISRUPT_CAMPAIGN_STATUS, campaignName)
//                .getColumnValue("STATUS");
//    }
//
//    public static String getBenefitTypeFromBacklogTable() throws Exception {
//        return DatabaseClient.get().executeQuery(BENEFIT_TYPE_CODE)
//                .getColumnValue("BENEFITTYPE");
//    }
//    public static String getFraimsCaseIdReportable(String fraimsCaseId) throws Exception {
//        return DatabaseClient.get().executeQuery(FRAIMS_CASE_ID_REPORTABLE,fraimsCaseId)
//                .getColumnValue("FRAIMSCASEID");
//    }
//    public static String getRententionPeriodFromDB(String caseId) throws Exception {
//        String caseid = "DWP-FES-WORK "+caseId;
//        System.out.println(caseid);
//        return DatabaseClient.get().executeQuery(RETENTION_POLICY_TABLE_QUERY,caseId)
//                .getColumnValue("RETENTIONPOLICY");
//    }
//}