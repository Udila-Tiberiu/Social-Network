package com.example.lab7bun;

import com.example.lab7bun.domain.Prietenie;
import com.example.lab7bun.domain.validators.CerereValidator;
import com.example.lab7bun.domain.validators.MesajValidator;
import com.example.lab7bun.domain.validators.PrietenieValidator;
import com.example.lab7bun.repository.*;
import com.example.lab7bun.domain.Utilizator;
import com.example.lab7bun.domain.validators.UtilizatorValidator;

import com.example.lab7bun.service.Service;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.controlsfx.glyphfont.INamedCharacter;

import java.io.IOException;

public class MainApp extends Application {
    public static Service service;
    void setService(Service service){
        this.service=service;
    }

    //public Service getService(){
    //   return service;
    //}

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {


        String url = "jdbc:postgresql://localhost:5432/socialnetwork";
        String username = "postgres";
        String password = "123456";
        String s = username + password;
        System.out.println(s);

        UtilizatorDBRepository utilizatorRepo = new UtilizatorDBRepository(url, username, password, new UtilizatorValidator());
        PrietenieDBRepository prietenieRepo = new PrietenieDBRepository(url, username, password, new PrietenieValidator(), utilizatorRepo);
        MesajDBRepository msgRepo = new MesajDBRepository(url,username,password,new MesajValidator(),utilizatorRepo);
        CerereDBRepository cerereRepo=new CerereDBRepository(url,username,password,new CerereValidator(),utilizatorRepo);
        //MainApp.service = new Service(utilizatorRepo, prietenieRepo, msgRepo);

        Service service = Service.getInstance();

        initView(primaryStage);
        primaryStage.show();
    }
    private void initView(Stage primaryStage) throws IOException{
        FXMLLoader stageLoader = new FXMLLoader(MainApp.class.getResource("hello-view.fxml"));
        //stageLoader.setLocation(getClass().getResource("C:/Users/USER/IdeaProjects/lab7/src/main/resources/com/example/lab7/hello-view.fxml"));

        AnchorPane layout=stageLoader.load();
        primaryStage.setScene(new Scene(layout));
        primaryStage.setTitle("App");

    }
}