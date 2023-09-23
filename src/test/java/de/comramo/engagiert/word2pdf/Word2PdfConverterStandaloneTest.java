package de.comramo.engagiert.word2pdf;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Word2PdfConverterStandaloneTest {


    public static void main(String[] args) throws IOException {


//    String uri = "http://localhost:5168/api/v1/convert";
        String uri = "http://localhost:5000/api/v1/convert";


//        String templateFile = "P:\\COMRAMO\\word2pdf\\testDoc-1seitig.docx";
//        String templateFile = "P:\\COMRAMO\\word2pdf\\testDoc-2seitig.docx";
        String templateFile = "C:\\work\\comramo\\formulare\\DWH\\Anfangsbescheinigung_TZ.docx";
//        String backgroundFile = "P:\\COMRAMO\\word2pdf\\220901_P1121006_Brief _Vorlage_netzwerk-m_mehrseitig.docx";
//        String backgroundFile = "P:\\COMRAMO\\word2pdf\\220901_P1121006_Brief _Vorlage_netzwerk-m.docx";
//        String backgroundFile = "P:\\COMRAMO\\word2pdf\\220901_P1121006_DWiN_Brief mit Logo FWD.docx";
        String backgroundFile = "C:\\work\\comramo\\formulare\\DWH\\Briefkopf_mehrseitig.docx";
//        String backgroundFile = null;
        String outputFolder = "c:/temp";
        HashMap<String, String> valueMapping = new HashMap<>();

        valueMapping.put("addr_anrede", "freiwillige.anrede");
        valueMapping.put("addr_vorname", "freiwillige.vorname");
        valueMapping.put("addr_nachname", "freiwillige.nachname");
        valueMapping.put("addr_adresszusatz", "freiwillige.adresse.adresszusatz");
        valueMapping.put("addr_strasse", "freiwillige.adresse.strasseHausnummer");
        valueMapping.put("addr_plz", "freiwillige.adresse.plz");
        valueMapping.put("addr_ort", "freiwillige.adresse.ort");
        valueMapping.put("currentDate", "currentDate");
        valueMapping.put("anrede", "freiwillige.anrede");
        valueMapping.put("vorname", "freiwillige.vorname");
        valueMapping.put("nachname", "freiwillige.nachname");
        valueMapping.put("geburtsdatum", "freiwillige.geburtsdatum");
        valueMapping.put("strasse", "freiwillige.adresse.strasseHausnummer");
        valueMapping.put("plz", "freiwillige.adresse.plz");
        valueMapping.put("ort", "freiwillige.adresse.ort");
        valueMapping.put("startDate", "freiwillige.dienststart");
        valueMapping.put("endDate", "freiwillige.dienstende");
        valueMapping.put("anzahlStunden", "freiwillige.anzahlStunden");
        valueMapping.put("einrichtungName1", "einsatzstelle.bezeichnung");
        valueMapping.put("einrichtungName2", "einsatzstelle.bezeichnung2");
        valueMapping.put("einrichtungStrasse", "einsatzstelle.postadresse.strasseHausnummer");
        valueMapping.put("einrichtungPlz", "einsatzstelle.postadresse.plz");
        valueMapping.put("einrichtungOrt", "einsatzstelle.postadresse.ort");
        valueMapping.put("taschengeld", "freiwillige.kostenerstattungen.taschengeld");
        valueMapping.put("fahrtkosten", "freiwillige.kostenerstattungen.fahrtgeld");
        valueMapping.put("verpflegungskostenzuschuss", "freiwillige.kostenerstattungen.verpflegungskostenzuschuss");
        valueMapping.put("username", "userName");

        ConversionRequest conversionRequest = new ConversionRequest(toBase64(new File(templateFile)), valueMapping);
        if (backgroundFile != null) {
            conversionRequest.setBackgroundAsBase64(toBase64(new File(backgroundFile)));
        }

        String jsonRequest = new Gson().toJson(conversionRequest);

        ConversionResponse conversionResponse;
        long end;
        long start;
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

            HttpPost httpPost = new HttpPost(uri);
            HttpEntity stringEntity = new StringEntity(jsonRequest, ContentType.APPLICATION_JSON);
            httpPost.setEntity(stringEntity);
            start = System.currentTimeMillis();
            try (CloseableHttpResponse response2 = httpclient.execute(httpPost)) {

                end = System.currentTimeMillis();
                if (response2.getCode() != 200) {
                    throw new RuntimeException("Failed with HTTP error code : " + response2.getCode());
                }

                InputStream responseBody = response2.getEntity().getContent();
                String responseAsString = IOUtils.toString(responseBody, UTF_8);
                conversionResponse = new Gson().fromJson(responseAsString, ConversionResponse.class);
            }
        }

        toFile(conversionResponse.getPdfAsBase64(), outputFolder);
        System.out.println("... REST call took " + (end - start) + "ms");
    }


    private static void toFile(String base64, String folder) throws IOException {
        byte[] byteArray = Base64.getDecoder().decode(base64);
        String fileName = FilenameUtils.concat(folder, System.currentTimeMillis() + ".pdf");
        FileUtils.writeByteArrayToFile(new File(fileName), byteArray);
        System.out.println("saved to file: " + fileName);
    }

    private static String toBase64(File file) {
        if (file == null) {
            return null;
        }

        byte[] encoded;
        try {
            encoded = Base64.getEncoder().encode(FileUtils.readFileToByteArray(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new String(encoded, UTF_8);
    }
}
