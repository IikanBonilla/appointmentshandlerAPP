package Development.frontend.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Gestiona la navegación entre las diferentes pantallas de la aplicación
 * 
 * @author iikan
 */

@Component
public class SceneManager {
    
    private final ApplicationContext springContext;
    private Stage primaryStage;
    private Scene currentScene;
    
    @Autowired
    public SceneManager(ApplicationContext springContext) {
        this.springContext = springContext;
    }
    
    
    // Inicializa el SceneManager con la ventana principal
    // Debe llamarse al iniciar la aplicación
    
    public void initialize(Stage stage) {
        this.primaryStage = stage;
        this.primaryStage.setTitle("Clínica - Sistema de Agendamiento de Citas");
        this.primaryStage.setMinWidth(650);
        this.primaryStage.setMinHeight(600);
        this.primaryStage.setResizable(true);
    }
    
    /**
     * Muestra el formulario de creación de citas
     */
    public void showAppointmentForm() {
        switchScene("/view/AppointmentFormView.fxml", "Agendar Cita");
    }
    
    /**
     * Muestra la pantalla de login (para cuando la implementemos)
     */
    public void showLogin() {
        switchScene("/view/LoginView.fxml", "Iniciar Sesión");
    }
    
    /**
     * Muestra el dashboard principal (para cuando la implementemos)
     */
    public void showDashboard() {
        switchScene("/view/DashboardView.fxml", "Panel Principal");
    }
    
    /**
     * Muestra la lista de citas (para cuando la implementemos)
     */
    public void showAppointmentList() {
        switchScene("/view/AppointmentListView.fxml", "Lista de Citas");
    }
    
    /**
     * Método privado que realiza el cambio de pantalla
     * 
     * @param fxmlPath Ruta del archivo FXML
     * @param title Título de la ventana
     */
    private void switchScene(String fxmlPath, String title) {
        try {
            // Crear el FXMLLoader
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            
            // IMPORTANTE: Decirle a Spring que inyecte los controladores
            loader.setControllerFactory(springContext::getBean);
            
            // Cargar la vista
            Parent root = loader.load();
            
            // Crear o actualizar la escena
            currentScene = new Scene(root);
            
            // Aplicar CSS si tienes (opcional)
            // String cssPath = getClass().getResource("/css/styles.css").toExternalForm();
            // currentScene.getStylesheets().add(cssPath);
            
            // Mostrar en la ventana principal
            primaryStage.setScene(currentScene);
            primaryStage.setTitle(title);
            primaryStage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al cargar la vista: " + fxmlPath, e);
        }
    }
    
    /**
     * Obtiene la ventana principal
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    /**
     * Obtiene la escena actual
     */
    public Scene getCurrentScene() {
        return currentScene;
    }
}