package Development.frontend.controller;

import Development.Model.Appointment;
import Development.frontend.service.ApiClient;
import Development.frontend.util.SceneManager;
import Development.frontend.util.SessionManager;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
public class AppointmentListController {
    
    // Componentes UI
    @FXML private Label dateLabel;
    @FXML private Label professionalNameLabel;
    @FXML private Label totalCitasLabel;
    @FXML private TableView<AppointmentRow> appointmentsTable;
    @FXML private TableColumn<AppointmentRow, String> colTime;
    @FXML private TableColumn<AppointmentRow, String> colDocument;
    @FXML private TableColumn<AppointmentRow, String> colPhone;
    @FXML private TableColumn<AppointmentRow, String> colFullName;
    @FXML private TableColumn<AppointmentRow, String> colAge;
    @FXML private TableColumn<AppointmentRow, String> colObservation;
    @FXML private TableColumn<AppointmentRow, String> colEmail;
    
    @FXML private Button prevDayButton;
    @FXML private Button nextDayButton;
    @FXML private Button todayButton;
    @FXML private Button newAppointmentButton;
    @FXML private Button exportButton;
    @FXML private Button closeButton;
    @FXML private Label statusLabel;
    @FXML private ProgressIndicator loadingIndicator;
    
    private final ApiClient apiClient;
    private final SceneManager sceneManager;
    private final SessionManager sessionManager;
    private final ObservableList<AppointmentRow> appointmentsList = FXCollections.observableArrayList();
    
    private LocalDate currentDate = LocalDate.now();
    private List<LocalTime> allTimeSlots;
    
    @Autowired
    public AppointmentListController(ApiClient apiClient, SceneManager sceneManager, SessionManager sessionManager) {
        this.apiClient = apiClient;
        this.sceneManager = sceneManager;
        this.sessionManager = sessionManager;
        this.allTimeSlots = generateTimeSlots();
    }
    
    @FXML
    public void initialize() {
        setupTable();
        setupUserInfo();
        setupDateNavigation();
        loadAppointmentsForDate(currentDate);
    }
    
    private void setupTable() {
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colDocument.setCellValueFactory(new PropertyValueFactory<>("document"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        colObservation.setCellValueFactory(new PropertyValueFactory<>("observation"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        appointmentsTable.setItems(appointmentsList);
        appointmentsTable.setPlaceholder(new Label("No hay citas para este día"));
    }
    
    private void setupUserInfo() {
        String roleText = sessionManager.isDoctor() ? "👨‍⚕️ Médico" : "🧠 Terapista";
        professionalNameLabel.setText(roleText + " - " + sessionManager.getCurrentUserFullName());
    }
    
    private void setupDateNavigation() {
        prevDayButton.setOnAction(e -> changeDate(-1));
        nextDayButton.setOnAction(e -> changeDate(1));
        todayButton.setOnAction(e -> goToToday());
        newAppointmentButton.setOnAction(e -> sceneManager.showAppointmentForm());
        exportButton.setOnAction(e -> exportAppointments());
        closeButton.setOnAction(e -> handleLogout());
    }
    
    private void changeDate(int days) {
        currentDate = currentDate.plusDays(days);
        loadAppointmentsForDate(currentDate);
    }
    
    private void goToToday() {
        currentDate = LocalDate.now();
        loadAppointmentsForDate(currentDate);
    }
    
    private void loadAppointmentsForDate(LocalDate date) {
        updateDateDisplay(date);
        showLoading(true);
        
        String userId = sessionManager.getCurrentUserId();
        String url = apiClient.getBaseUrl() + "/api/appointments/user/" + userId + "/date/" + date.toString();
        
        System.out.println("📡 URL: " + url);
        
        new Thread(() -> {
            try{
                CloseableHttpClient client = apiClient.getHttpClient();
                HttpGet request = new HttpGet(url);
                request.setHeader("Content-Type", "application/json");
                
                var response = client.execute(request);
                String responseBody = EntityUtils.toString(response.getEntity());
                int statusCode = response.getStatusLine().getStatusCode();
                
                Platform.runLater(() -> {
                    if (statusCode == 200) {
                        try {
                            List<Appointment> appointments = apiClient.getObjectMapper()
                                .readValue(responseBody, new TypeReference<List<Appointment>>() {});
                            
                            System.out.println("📡 Citas recibidas: " + appointments.size());
                            fillTableWithAppointments(appointments);
                            totalCitasLabel.setText("Total citas: " + appointments.size());
                            
                        } catch (Exception e) {
                            System.err.println("❌ Error: " + e.getMessage());
                            e.printStackTrace();
                            showError("Error al procesar datos: " + e.getMessage());
                        }
                    } else {
                        showError("Error al cargar citas");
                        fillTableWithAppointments(null);
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
    
    private void fillTableWithAppointments(List<Appointment> appointments) {
        Map<LocalTime, Appointment> appointmentMap = new HashMap<>();
        if (appointments != null) {
            for (Appointment app : appointments) {
                appointmentMap.put(app.getTime(), app);
            }
        }
        
        appointmentsList.clear();
        
        for (LocalTime time : allTimeSlots) {
            Appointment app = appointmentMap.get(time);
            AppointmentRow row = new AppointmentRow();
            
            row.setTime(formatTime(time));
            
            if (app != null) {
                row.setDocument(app.getIdentificacion() != null ? app.getIdentificacion() : "");
                row.setPhone(app.getPhoneNumber() != null ? app.getPhoneNumber() : "");
                row.setFullName(app.getFullName() != null ? app.getFullName() : "");
                row.setEmail(app.getEmail() != null ? app.getEmail() : "");
                row.setObservation(app.getObservation() != null ? app.getObservation() : "");
                
                if (app.getBirthDate() != null) {
                    int age = Period.between(app.getBirthDate(), LocalDate.now()).getYears();
                    row.setAge(String.valueOf(age));
                } else {
                    row.setAge("-");
                }
            } else {
                row.setDocument("");
                row.setPhone("");
                row.setFullName("");
                row.setEmail("");
                row.setAge("");
                row.setObservation("");
            }
            
            appointmentsList.add(row);
        }
    }
    
    private void updateDateDisplay(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        String dayName = getDayName(date.getDayOfWeek().getValue());
        dateLabel.setText(date.format(formatter) + " - " + dayName);
    }
    
    private String getDayName(int dayOfWeek) {
        switch (dayOfWeek) {
            case 1: return "Lunes";
            case 2: return "Martes";
            case 3: return "Miércoles";
            case 4: return "Jueves";
            case 5: return "Viernes";
            case 6: return "Sábado";
            case 7: return "Domingo";
            default: return "";
        }
    }
    
    private String formatTime(LocalTime time) {
        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
    
    private List<LocalTime> generateTimeSlots() {
        List<LocalTime> times = new java.util.ArrayList<>();
        LocalTime start = LocalTime.of(7, 0);
        LocalTime end = LocalTime.of(13, 0);
        
        LocalTime current = start;
        while (!current.isAfter(end)) {
            times.add(current);
            current = current.plusMinutes(30);
        }
        return times;
    }
    
    private void exportAppointments() {
        showInfo("Funcionalidad en desarrollo");
    }
    
    @FXML
    private void handleLogout() {
        sessionManager.logout();
        sceneManager.showLogin();
    }
    
    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: red;");
        statusLabel.setVisible(true);
        
        new Thread(() -> {
            try { Thread.sleep(3000); } catch (InterruptedException e) {}
            Platform.runLater(() -> statusLabel.setVisible(false));
        }).start();
    }
    
    private void showInfo(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #3498db;");
        statusLabel.setVisible(true);
        
        new Thread(() -> {
            try { Thread.sleep(3000); } catch (InterruptedException e) {}
            Platform.runLater(() -> statusLabel.setVisible(false));
        }).start();
    }
    
    private void showLoading(boolean show) {
        loadingIndicator.setVisible(show);
        appointmentsTable.setDisable(show);
        prevDayButton.setDisable(show);
        nextDayButton.setDisable(show);
        todayButton.setDisable(show);
        newAppointmentButton.setDisable(show);
        exportButton.setDisable(show);
    }
    
    public static class AppointmentRow {
        private String time;
        private String document;
        private String phone;
        private String fullName;
        private String age;
        private String observation;
        private String email;
        
        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }
        
        public String getDocument() { return document; }
        public void setDocument(String document) { this.document = document; }
        
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        
        public String getAge() { return age; }
        public void setAge(String age) { this.age = age; }
        
        public String getObservation() { return observation; }
        public void setObservation(String observation) { this.observation = observation; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}