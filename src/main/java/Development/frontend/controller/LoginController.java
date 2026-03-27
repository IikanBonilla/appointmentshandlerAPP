package Development.frontend.controller;

import Development.DTOs.AuthResponseDTO;
import Development.DTOs.LoginRequestDTO;
import Development.frontend.service.ApiClient;
import Development.frontend.util.SceneManager;
import Development.frontend.util.SessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class LoginController {
    
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label statusLabel;
    @FXML private ProgressIndicator loadingIndicator;
    
    private final ApiClient apiClient;
    private final SceneManager sceneManager;
    private final SessionManager sessionManager;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public LoginController(ApiClient apiClient, SceneManager sceneManager, SessionManager sessionManager) {
        this.apiClient = apiClient;
        this.sceneManager = sceneManager;
        this.sessionManager = sessionManager;
        this.objectMapper = apiClient.getObjectMapper();
    }
    
    @FXML
    public void initialize() {
        loginButton.setDisable(true);
        
        usernameField.textProperty().addListener((obs, old, val) -> validateForm());
        passwordField.textProperty().addListener((obs, old, val) -> validateForm());
        
        loadingIndicator.setVisible(false);
    }
    
    private void validateForm() {
        boolean isValid = !usernameField.getText().isEmpty() && !passwordField.getText().isEmpty();
        loginButton.setDisable(!isValid);
    }
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        showLoading(true);
        statusLabel.setVisible(false);
        
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUserName(username);
        loginRequest.setPassword(password);
        
        new Thread(() -> {
            try{
                CloseableHttpClient client = apiClient.getHttpClient();
                String url = apiClient.getBaseUrl() + "/auth/login";
                HttpPost request = new HttpPost(url);
                request.setHeader("Content-Type", "application/json");
                
                String json = objectMapper.writeValueAsString(loginRequest);
                request.setEntity(new StringEntity(json));
                
                var response = client.execute(request);
                String responseBody = EntityUtils.toString(response.getEntity());
                int statusCode = response.getStatusLine().getStatusCode();
                
                Platform.runLater(() -> {
                    if (statusCode == 200) {
                        try {
                            // Parsear respuesta
                            AuthResponseDTO authResponse = objectMapper.readValue(responseBody, AuthResponseDTO.class);
                            
                            // Guardar sesión
                            sessionManager.login(
                                authResponse.getId(),
                                username,
                                authResponse.getFullName(),
                                authResponse.getRole(),
                                authResponse.getStatus()
                            );
                            
                            showSuccess("Bienvenido/a " + authResponse.getFullName());
                            showLoading(false);
                            
                            // Ir a la lista de citas (que filtrará automáticamente por userId)
                            sceneManager.showAppointmentList();
                            
                        } catch (Exception e) {
                            showError("Error al procesar respuesta: " + e.getMessage());
                            showLoading(false);
                        }
                    } else {
                        showError("Usuario o contraseña incorrectos");
                        showLoading(false);
                    }
                });
                
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Error de conexión: " + e.getMessage());
                    showLoading(false);
                });
            }
        }).start();
    }
    
    private void showSuccess(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: green;");
        statusLabel.setVisible(true);
    }
    
    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: red;");
        statusLabel.setVisible(true);
    }
    
    private void showLoading(boolean show) {
        loadingIndicator.setVisible(show);
        loginButton.setDisable(show);
    }
}
