package uk.gov.dwp.utils.pdf;

import de.redsix.pdfcompare.PdfComparator;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import uk.gov.dwp.utils.FileUtils;
import uk.gov.dwp.webdriver.configuration.BrowserType;
import uk.gov.dwp.webdriver.configuration.RunType;
import uk.gov.dwp.webdriver.configuration.TestConfigHelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class PDFUtil {

    public  String getPDFFileContent(String pdfFileName) throws IOException {
        PDDocument pdfDocument = getDocument(pdfFileName);
        String pdfContent = getPDFDocumentContent(pdfDocument);
        pdfDocument.close();
        return pdfContent.trim();
    }

    public static String sanitizeExtension(String fileName) {
        return fileName.replace(".pdf", "") + ".pdf";
    }

    private static PDDocument getDocument(String pdfFileName) throws IOException {
        RunType runType = TestConfigHelper.get().getRunType();
        switch (runType) {
            case GRID:
                return getDocumentFromRemoteSeleniumGrid(pdfFileName);
            case LOCAL:
                return getDocumentFromLocalFileSystem(pdfFileName);
            default:
                String errorMessage = String.format("Sorry we do not support PDF downloads for the run type %s",
                        runType);
                throw new UnsupportedOperationException(errorMessage);
        }
    }

    private static PDDocument getDocumentFromLocalFileSystem(String pdfFileName) throws IOException {
        String pdfFilePath = getLocalPDFFilePath(pdfFileName);
        return PDDocument.load(new File(pdfFilePath));
    }

    private static PDDocument getDocumentFromRemoteSeleniumGrid(String pdfFileName) throws IOException {
        String seleniumGridHost = TestConfigHelper.get().getGridConfig().getGridUrl().getHost();
        String gridHostPdfUrl = String.format("http://%s/%s", seleniumGridHost, pdfFileName);
        InputStream in = new URL(gridHostPdfUrl).openStream();
        return PDDocument.load(in);
    }

    private static String getLocalPDFFilePath(String pdfFileName) throws IOException {
        BrowserType browserType = TestConfigHelper.get().getBrowserType();

        switch (browserType) {
            case FIREFOX:
                return new File(TestConfigHelper.get().getBrowserPreferences(BrowserType.FIREFOX)
                        .get("browser.download.dir").textValue()).getCanonicalPath()
                        .concat("/").concat(pdfFileName);
            case CHROME:
                return new File(TestConfigHelper.get().getBrowserPreferences(BrowserType.CHROME)
                        .get("download.default_directory").textValue()).getCanonicalPath()
                        .concat("/").concat(pdfFileName);
            default:
                String errorMessage = String.format("Sorry we do not support PDF downloads for the browser %s",
                        browserType);
                throw new UnsupportedOperationException(errorMessage);
        }
    }

    private static String getPDFDocumentContent(PDDocument document) throws IOException {
        PDFTextStripper pdfStripper = new PDFTextStripper();
        pdfStripper.setSortByPosition(true);
        pdfStripper.setStartPage(0);
        pdfStripper.setEndPage(pdfStripper.getEndPage());
        return pdfStripper.getText(document);
    }

    public static boolean comparePDFFormat(String expectedPDF, String actualPDF) throws IOException {
        String pdfDiffFolderPath = "target/pdf-difference/";
        FileUtils.checkFolderExists(pdfDiffFolderPath);
        String expectedPDFFilePath = "src/test/resources/templates/pdf-files/" + expectedPDF;
        String actualPDFFilePath = getActualPDFPath(actualPDF);
        String ignorePDFFilePath = "src/test/resources/templates/pdf-files/"
                + expectedPDF.split("/")[0] + "/ignore-areas.conf";
        return new PdfComparator(expectedPDFFilePath, actualPDFFilePath)
                .withIgnore(ignorePDFFilePath)
                .compare()
                .writeTo("target/pdf-difference/" + actualPDF.split(".pdf")[0] + "_diff");
    }

    public static String createAndGetPDFFilePathFromSeleniumGrid(String actualPDFFileName) throws IOException {
        String pdfFolderPath = "target/actual-pdf-files/";
        FileUtils.checkFolderExists(pdfFolderPath);
        PDDocument inputPDF = getDocumentFromRemoteSeleniumGrid(actualPDFFileName);
        PDDocument outPutPDF = new PDDocument();
        outPutPDF.save(pdfFolderPath + actualPDFFileName);
        for (int index = 0; index < inputPDF.getNumberOfPages(); index++) {
            PDPage pdPage = inputPDF.getPage(index);
            outPutPDF.addPage(pdPage);
        }
        outPutPDF.save(pdfFolderPath + actualPDFFileName);
        outPutPDF.close();
        inputPDF.close();
        return pdfFolderPath + actualPDFFileName;
    }

    private static String getActualPDFPath(String pdfFileName) throws IOException {
        RunType runType = TestConfigHelper.get().getRunType();
        switch (runType) {
            case GRID:
                return createAndGetPDFFilePathFromSeleniumGrid(pdfFileName);
            case LOCAL:
                return getLocalPDFFilePath(pdfFileName);
            default:
                String errorMessage = String.format("Sorry we do not support PDF downloads for the run type %s",
                        runType);
                throw new UnsupportedOperationException(errorMessage);
        }
    }
}
