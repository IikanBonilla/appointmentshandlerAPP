/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Service.java to edit this template
 */
package Development.frontend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author iikan
 */
@Service
public class ApiClient {
    
    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl = "http://localhost:8080";
    
    public ApiClient(){
        this.httpClient = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                    .setConnectTimeout(5000)
                    .setSocketTimeout(30000)
                    .build())
                .build();
        
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    
    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }
    
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
    
    public String getBaseUrl() {
        return baseUrl;
    }
}
