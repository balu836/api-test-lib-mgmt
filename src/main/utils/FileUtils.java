package uk.gov.dwp.utils;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import uk.gov.dwp.webdriver.configuration.RunType;
import uk.gov.dwp.webdriver.configuration.TestConfigHelper;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class FileUtils {

    public static String readFile(String filename) {
        String content = null;
        File file = new File(filename);
        try (FileReader reader = new FileReader(file)) {
            char[] chars = new char[(int) file.length()];
            reader.read(chars);
            content = new String(chars);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static void checkFolderExists(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    public static synchronized String createAndSaveFileForUpload(String fileName) throws IOException {
        String uploadPath = TestConfigHelper.get().getTestConfigItem("localUploadPath");
        var destinationPath = new File(uploadPath + "/" + fileName).toPath();
        checkFolderExists(uploadPath);
        Files.writeString(destinationPath, "Sample Text for file upload");
        if (TestConfigHelper.get().getRunType() == RunType.GRID) {
            String fileUploadUrl = TestConfigHelper.get().getTestConfigItem("remoteUploadUrl");
            saveFileToRemoteSeleniumClient(destinationPath.toString(), fileUploadUrl);
        }
        return destinationPath.toString();
    }

    public static synchronized String createDisruptAndSaveFileForUpload(
            String fileName, List<List<String>> disruptCases) throws IOException {
        String uploadPath = TestConfigHelper.get().getTestConfigItem("localUploadPath");
        var destinationPath = new File(uploadPath + "/" + fileName).toPath();
        checkFolderExists(uploadPath);
        createDisruptCSVFile(destinationPath.toString(), disruptCases);
        if (TestConfigHelper.get().getRunType() == RunType.GRID) {
            String fileUploadUrl = TestConfigHelper.get().getTestConfigItem("remoteUploadUrl");
            saveFileToRemoteSeleniumClient(destinationPath.toString(), fileUploadUrl);
        }
        return destinationPath.toString();
    }



    private static void createDisruptCSVFile(String fileName, List<List<String>> cases) throws IOException {
        FileWriter csvWriter = new FileWriter(fileName);
        csvWriter.append("CRCReference");
        csvWriter.append(",");
        csvWriter.append("ContractID");
        csvWriter.append("\n");
        for (List<String> data : cases) {
            csvWriter.append(String.join(",", data));
            csvWriter.append("\n");
        }
        csvWriter.flush();
        csvWriter.close();
    }

    private static void saveFileToRemoteSeleniumClient(String destinationPath, String uploadUrl) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            File file = new File(destinationPath);
            HttpPost post = new HttpPost(uploadUrl);
            HttpEntity entity = MultipartEntityBuilder.create()
                    .addPart("referralPackage", new FileBody(file))
                    .build();
            post.setEntity(entity);

            try (CloseableHttpResponse response = client.execute(post)) {
                String responseBody = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new IOException("Unable to upload referral package to remote selenium client." +
                            "\nCode: " + response.getStatusLine().getStatusCode() +
                            "\nBody: " + responseBody);
                }
            }
        }
    }




}
