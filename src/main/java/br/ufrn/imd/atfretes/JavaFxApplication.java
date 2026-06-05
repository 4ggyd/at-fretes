package br.ufrn.imd.atfretes;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.URL;

public class JavaFxApplication extends Application {

    private ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        // Inicializa o contexto do Spring Boot de forma totalmente isolada antes de carregar a interface gráfica
        this.springContext = new SpringApplicationBuilder()
                .sources(AtFretesApplication.class)
                .run(getParameters().getRaw().toArray(new String[0]));
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Localiza o arquivo de layout de forma segura dentro dos recursos da aplicação
            URL fxmlLocation = getClass().getResource("/fxml/form-frete.fxml");
            if (fxmlLocation == null) {
                throw new IllegalStateException("O arquivo de layout form-frete.fxml nao foi localizado na pasta resources/fxml/");
            }

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
            
            // Vincula o ciclo de vida dos Controllers do JavaFX ao gerenciador de beans do Spring Boot
            fxmlLoader.setControllerFactory(springContext::getBean);

            Parent root = fxmlLoader.load();
            primaryStage.setTitle("Sistema de Fretes IMD");
            primaryStage.setScene(new Scene(root));
            primaryStage.centerOnScreen();
            primaryStage.show();
            
        } catch (Exception e) {
            System.err.println("Falha critica ao renderizar a interface visual: " + e.getMessage());
            e.printStackTrace();
            Platform.exit();
        }
    }

    @Override
    public void stop() {
        // Encerra os servicos em segundo plano do Spring Boot de forma limpa ao fechar a janela
        if (this.springContext != null) {
            this.springContext.close();
        }
        Platform.exit();
    }

    public static void main(String[] args) {
        Application.launch(JavaFxApplication.class, args);
    }
}