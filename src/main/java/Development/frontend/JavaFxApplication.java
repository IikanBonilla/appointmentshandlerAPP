package Development.frontend;

import Development.PahandlerApplication;
import Development.frontend.service.ApiClient;
import Development.frontend.util.SceneManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

public class JavaFxApplication extends Application {
    
    @Autowired
    private ApiClient apiClient;
    private ConfigurableApplicationContext springContext;
    
    @Override
    public void init() {
        // Spring ya está iniciado desde main(), solo obtener el contexto
        springContext = PahandlerApplication.getSpringContext();
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("JavaFX iniciado correctamente");
        
        SceneManager sceneManager = springContext.getBean(SceneManager.class);
        sceneManager.initialize(stage);
        sceneManager.showLogin();
    }
    
    @Override
    public void stop() {
        try {
            apiClient.getHttpClient().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        springContext.close();
        Platform.exit();
    }
}