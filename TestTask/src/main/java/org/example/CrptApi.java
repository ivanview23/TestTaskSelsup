package org.example;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.logging.Logger;
import okhttp3.*;

    public class CrptApi {

      public static void main(String[] args) {

        CrptApi crptApi = new CrptApi(TimeUnit.SECONDS,2);

      }
      private final TimeUnit timeUnit;
      private final int requestLimit;
      private final AtomicInteger requestCount = new AtomicInteger(0);
      private final Lock lock = new ReentrantLock();
      private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

      private static final String API_URL = "https://ismp.crpt.ru/api/v3/lk/documents/create";
      private static final OkHttpClient client = new OkHttpClient();
      private static final ObjectMapper objectMapper = new ObjectMapper();

      public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.timeUnit = timeUnit;
        this.requestLimit = requestLimit;
        scheduler.scheduleAtFixedRate(() -> requestCount.set(0), 0, 1, timeUnit);
      }

      public void createDocument(Document document) throws Exception {
        lock.lock();
        try {
          while (requestCount.get() >= requestLimit) {
            System.out.println("res" + requestCount.get());


            lock.wait();
          }
          requestCount.incrementAndGet();
        } finally {
          lock.unlock();
        }

        RequestBody body = RequestBody.create(objectMapper.writeValueAsString(document), MediaType.get("application/json"));
        Request request = new Request.Builder()
            .url(API_URL)
            .post(body)
            .build();

        try (Response response = client.newCall(request).execute()) {
          if (!response.isSuccessful()) {
            throw new RuntimeException("Request failed: " + response);
          }
        } finally {
          lock.lock();
          try {
            lock.notifyAll();
          } finally {
            lock.unlock();
          }
        }
      }

      public static class Document {
        public String description;
        public String participantInn;
        public String docId;
        public String docStatus;
        public String docType;
        public String docTypeQualifier;
        public boolean importRequest;
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

        // Getters and setters
      }
  }
