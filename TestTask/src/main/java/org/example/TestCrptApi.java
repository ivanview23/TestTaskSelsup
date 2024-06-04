package org.example;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class TestCrptApi {
  public static void main(String[] args) {

    CrptApi api = new CrptApi(TimeUnit.MINUTES, 5);

    // Подготовка данных для отправки
    CrptApi.Document document = new CrptApi.Document();
    document.setDescription("{\"participantInn\": \"1234567890\"}");
    document.setDocId("unique_doc_id_1");
    document.setDocStatus("new");
    document.setDocType("LP_INTRODUCE_GOODS");
    document.setImportRequest(true);
    document.setOwnerInn("9876543210");
    document.setParticipantInn("1234567890");
    document.setProducerInn("0987654321");
    document.setProductionDate(LocalDateTime.now().toString());
    document.setProductionType("type");
    document.setProducts(null);
    document.setRegDate(LocalDateTime.now().toString());
    document.setRegNumber("number");


    String signature = "your_signature_here"; // Замените на актуальную подпись


    try {
      api.createDocument(document, signature);
      System.out.println("Документ успешно отправлен.");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

