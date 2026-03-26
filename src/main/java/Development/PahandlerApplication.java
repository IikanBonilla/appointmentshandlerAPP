package Development;

import Development.frontend.JavaFxApplication;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class PahandlerApplication {
    
    private static ConfigurableApplicationContext springContext;
    
    public static void main(String[] args) {
        // 1. Iniciar Spring Boot primero
        springContext = new SpringApplicationBuilder(PahandlerApplication.class)
                .run(args);
        
        // 2. Luego lanzar JavaFX
        Application.launch(JavaFxApplication.class, args);
    }
    
    public static ConfigurableApplicationContext getSpringContext() {
        return springContext;
    }
}