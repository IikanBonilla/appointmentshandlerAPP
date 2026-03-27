package Development.frontend.controller;

import Development.DTOs.CreateAppointDTO;
import Development.Model.Gender;
import Development.frontend.service.ApiClient;
import Development.frontend.util.SceneManager;
import Development.frontend.util.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class AppointmentFormController {
    
    // Componentes UI
    @FXML private TextField identificacionField;
    @FXML private TextField fullNameField;
    @FXML private TextField professionalNameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField ageField;
    @FXML private TextArea observationArea;
    
    @FXML private RadioButton masculinoRadio;
    @FXML private RadioButton femeninoRadio;
    @FXML private RadioButton otroRadio;
    @FXML private ToggleGroup genderGroup;
    
    @FXML private DatePicker birthDatePicker;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> timeCombo;
    @FXML private CheckBox saveToHistoryCheck;
    
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Label statusLabel;
    @FXML private ProgressIndicator loadingIndicator;
    
    // Servicios
    private final ApiClient apiClient;
    private final SceneManager sceneManager;
    private final SessionManager sessionManager;
    
    @Autowired
    public AppointmentFormController(ApiClient apiClient, SceneManager sceneManager, SessionManager sessionManager) {
        this.apiClient = apiClient;
        this.sceneManager = sceneManager;
        this.sessionManager = sessionManager;
    }
    
    @FXML
    public void initialize() {
        setupUI();
        setupBindings();
        setupListeners();
        loadAvailableTimes();
    }
    
    private void setupUI() {
        femeninoRadio.setSelected(true);
        datePicker.setPromptText("dd/MM/yyyy");
        birthDatePicker.setPromptText("dd/MM/yyyy");
        
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
        
        loadingIndicator.setVisible(false);
        statusLabel.setVisible(false);
        ageField.setEditable(false);
    }
    
    private void setupBindings() {
        saveButton.setDisable(true);
    }
    
    private void validateFormFields() {
        boolean isValid = !identificacionField.getText().isEmpty()
            && !fullNameField.getText().isEmpty()
            && !phoneField.getText().isEmpty()
            && datePicker.getValue() != null
            && timeCombo.getValue() != null;
        
        saveButton.setDisable(!isValid);
    }
    
    private void setupListeners() {
        identificacionField.textProperty().addListener((obs, old, val) -> validateFormFields());
        fullNameField.textProperty().addListener((obs, old, val) -> validateFormFields());
        phoneField.textProperty().addListener((obs, old, val) -> validateFormFields());
        datePicker.valueProperty().addListener((obs, old, val) -> validateFormFields());
        timeCombo.valueProperty().addListener((obs, old, val) -> validateFormFields());
        
        birthDatePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                calculateAge(newDate);
            } else {
                ageField.setText("0");
            }
        });
        
        datePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                loadAvailableTimes();
            }
        });
    }
    
    private void calculateAge(LocalDate birthDate) {
        LocalDate today = LocalDate.now();
        if (birthDate.isBefore(today)) {
            int age = Period.between(birthDate, today).getYears();
            ageField.setText(String.valueOf(age));
        } else {
            ageField.setText("0");
        }
    }
    
    private void loadAvailableTimes() {
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate == null) {
            return;
        }
        
        showLoading(true);
        
        Platform.runLater(() -> {
            List<String> times = generateExampleTimes();
            timeCombo.setItems(FXCollections.observableArrayList(times));
            showLoading(false);
        });
    }
    
    private List<String> generateExampleTimes() {
        List<String> times = new java.util.ArrayList<>();
        LocalTime start = LocalTime.of(7, 0);
        LocalTime end = LocalTime.of(13, 0);

        LocalTime current = start;
        while (!current.isAfter(end)) {
            times.add(current.format(DateTimeFormatter.ofPattern("HH:mm")));
            current = current.plusMinutes(30);
        }
        return times;
    }
    
    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }
        
        showLoading(true);
        statusLabel.setVisible(false);
        
        CreateAppointDTO dto = buildCreateAppointDTO();
        sendCreateAppointmentRequest(dto);
    }
    
    private CreateAppointDTO buildCreateAppointDTO() {
        CreateAppointDTO dto = new CreateAppointDTO();
        dto.setIdentificacion(identificacionField.getText());
        dto.setFullName(fullNameField.getText());
        dto.setProfessionalName(professionalNameField.getText());
        dto.setPhoneNumber(phoneField.getText());
        dto.setEmail(emailField.getText());
        dto.setBirthDate(birthDatePicker.getValue());
        dto.setObservation(observationArea.getText());
        
        if (masculinoRadio.isSelected()) {
            dto.setGender(Gender.MALE);
        } else if (femeninoRadio.isSelected()) {
            dto.setGender(Gender.FEMALE);
        } else {
            dto.setGender(Gender.OTHER);
        }
        
        dto.setDate(datePicker.getValue());
        
        String timeStr = timeCombo.getValue();
        if (timeStr != null) {
            dto.setTime(LocalTime.parse(timeStr));
        }
        
        return dto;
    }
    
    private void sendCreateAppointmentRequest(CreateAppointDTO dto) {
        String userId = getCurrentUserId();
        
        new Thread(() -> {
            try {
                CloseableHttpClient client = apiClient.getHttpClient();
                String url = apiClient.getBaseUrl() + "/api/appointments/create/" + userId;
                HttpPost request = new HttpPost(url);
                request.setHeader("Content-Type", "application/json");
                
                String json = apiClient.getObjectMapper().writeValueAsString(dto);
                System.out.println("📤 Enviando JSON: " + json);
                request.setEntity(new StringEntity(json));
                
                var response = client.execute(request);
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = response.getEntity() != null ? EntityUtils.toString(response.getEntity()) : "";
                
                Platform.runLater(() -> {
                    if (statusCode >= 200 && statusCode < 300) {
                        showSuccess("Cita agendada exitosamente");
                        clearForm();
                        sceneManager.showAppointmentList();
                    } else {
                        showError("Error: " + responseBody);
                    }
                    showLoading(false);
                });
                
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Error de conexión: " + e.getMessage());
                    showLoading(false);
                });
            }
        }).start();
    }
    
    private String getCurrentUserId() {
        return sessionManager.getCurrentUserId();
    }
    
    private boolean validateForm() {
        String phone = phoneField.getText();
        if (!phone.matches("\\d{7,15}")) {
            showError("Teléfono inválido. Debe contener entre 7 y 15 dígitos");
            return false;
        }
        
        String email = emailField.getText();
        if (email != null && !email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Email inválido");
            return false;
        }
        
        String identificacion = identificacionField.getText();
        if (identificacion.length() < 5) {
            showError("Identificación inválida");
            return false;
        }
        
        return true;
    }
    
    private void showSuccess(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        statusLabel.setVisible(true);
    }
    
    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        statusLabel.setVisible(true);
    }
    
    private void showLoading(boolean show) {
        loadingIndicator.setVisible(show);
        saveButton.setDisable(show);
        cancelButton.setDisable(show);
    }
    
    private void clearForm() {
        identificacionField.clear();
        fullNameField.clear();
        professionalNameField.clear();
        phoneField.clear();
        emailField.clear();
        observationArea.clear();
        birthDatePicker.setValue(null);
        datePicker.setValue(null);
        timeCombo.setValue(null);
        femeninoRadio.setSelected(true);
        ageField.setText("0");
        saveToHistoryCheck.setSelected(false);
        statusLabel.setVisible(false);
    }
    
    @FXML
    private void handleCancel() {
        sceneManager.showAppointmentList();
    }
}