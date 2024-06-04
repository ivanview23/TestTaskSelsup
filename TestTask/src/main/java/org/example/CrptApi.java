package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CrptApi {
  private final TimeUnit timeUnit;
  private final int requestLimit;
  private final ConcurrentHashMap<String, LocalDateTime> lastRequestTimes = new ConcurrentHashMap<>();
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private final OkHttpClient client = new OkHttpClient();
  private final ObjectMapper objectMapper = new ObjectMapper();


  public CrptApi(TimeUnit timeUnit, int requestLimit) {
    this.timeUnit = timeUnit;
    this.requestLimit = requestLimit;
    scheduler.scheduleAtFixedRate(this::checkAndResetLastRequestTime, 0, 1, timeUnit);
  }

  public void createDocument(Document document, String signature)
      throws IOException, InterruptedException {
    synchronized (lastRequestTimes) {
      while (getRequestsCountSinceLastReset() >= requestLimit) {
        System.out.println("Ожидание возможности сделать запрос...");
        lastRequestTimes.wait();
      }
      lastRequestTimes.put(Thread.currentThread().getId() + "", LocalDateTime.now());
      lastRequestTimes.notifyAll();
    }

    String json = objectMapper.writeValueAsString(document);
    RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
    Request request = new Request.Builder()
        .url("https://ismp.crpt.ru/api/v3/lk/documents/create")
        .post(body)
        .addHeader("Content-Type", "application/json")
        .addHeader("Signature", signature)
        .build();

    try (Response response = client.newCall(request).execute()) {
//      if (!response.isSuccessful()) {
//        throw new IOException("Ошибка выполнения запроса: " + response);
//      }
    } finally {
      lastRequestTimes.remove(Thread.currentThread().getId() + "");
    }
  }

  private int getRequestsCountSinceLastReset() {
    return lastRequestTimes.entrySet().stream()
        .mapToInt(entry -> (int) ChronoUnit.SECONDS.between(entry.getValue(), LocalDateTime.now()))
        .filter(seconds -> seconds <= Duration.ofSeconds(1L, TimeUnit.SECONDS.ordinal()).toMillis())
        .sum();
  }


  private void checkAndResetLastRequestTime() {
    lastRequestTimes.keySet().removeIf(key -> {
      LocalDateTime lastRequestTime = lastRequestTimes.get(key);
      if (ChronoUnit.SECONDS.between(lastRequestTime, LocalDateTime.now()) > Duration.ofSeconds(timeUnit.toSeconds(1)).toMillis()) {
        return true;
      }
      return false;
    });
  }

  public static class Document {
    public String description;
    public String participantInn;
    public String docId;
    public String docStatus;
    public String docType;
    public Boolean importRequest;
    public String ownerInn;
    public String producerInn;
    public String productionInn;
    public String productionDate;
    public String productionType;
    public String products;
    public String certificateDocument;
    public String certificateDocumentDate;
    public String certificateDocumentNumber;
    public String uitCode;
    public String uituCode;
    public String regDate;
    public String regNumber;

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public String getParticipantInn() {
      return participantInn;
    }

    public void setParticipantInn(String participantInn) {
      this.participantInn = participantInn;
    }

    public String getDocId() {
      return docId;
    }

    public void setDocId(String docId) {
      this.docId = docId;
    }

    public String getDocStatus() {
      return docStatus;
    }

    public void setDocStatus(String docStatus) {
      this.docStatus = docStatus;
    }

    public String getDocType() {
      return docType;
    }

    public void setDocType(String docType) {
      this.docType = docType;
    }

    public Boolean getImportRequest() {
      return importRequest;
    }

    public void setImportRequest(Boolean importRequest) {
      this.importRequest = importRequest;
    }

    public String getOwnerInn() {
      return ownerInn;
    }

    public void setOwnerInn(String ownerInn) {
      this.ownerInn = ownerInn;
    }

    public String getProducerInn() {
      return producerInn;
    }

    public void setProducerInn(String producerInn) {
      this.producerInn = producerInn;
    }

    public String getProductionInn() {
      return productionInn;
    }

    public void setProductionInn(String productionInn) {
      this.productionInn = productionInn;
    }

    public String getProductionDate() {
      return productionDate;
    }

    public void setProductionDate(String productionDate) {
      this.productionDate = productionDate;
    }

    public String getProductionType() {
      return productionType;
    }

    public void setProductionType(String productionType) {
      this.productionType = productionType;
    }

    public String getProducts() {
      return products;
    }

    public void setProducts(String products) {
      this.products = products;
    }

    public String getCertificateDocument() {
      return certificateDocument;
    }

    public void setCertificateDocument(String certificateDocument) {
      this.certificateDocument = certificateDocument;
    }

    public String getCertificateDocumentDate() {
      return certificateDocumentDate;
    }

    public void setCertificateDocumentDate(String certificateDocumentDate) {
      this.certificateDocumentDate = certificateDocumentDate;
    }

    public String getCertificateDocumentNumber() {
      return certificateDocumentNumber;
    }

    public void setCertificateDocumentNumber(String certificateDocumentNumber) {
      this.certificateDocumentNumber = certificateDocumentNumber;
    }

    public String getUitCode() {
      return uitCode;
    }

    public void setUitCode(String uitCode) {
      this.uitCode = uitCode;
    }

    public String getUituCode() {
      return uituCode;
    }

    public void setUituCode(String uituCode) {
      this.uituCode = uituCode;
    }

    public String getRegDate() {
      return regDate;
    }

    public void setRegDate(String regDate) {
      this.regDate = regDate;
    }

    public String getRegNumber() {
      return regNumber;
    }

    public void setRegNumber(String regNumber) {
      this.regNumber = regNumber;
    }

    // Геттеры и сеттеры
  }
}
