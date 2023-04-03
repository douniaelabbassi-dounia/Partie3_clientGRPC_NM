package ma.enset.ClientsJouer;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ma.enset.stubs.Jeu;
import ma.enset.stubs.NbrMgServiceGrpc;

import java.util.Scanner;

public class jouerService extends Application {
    ObservableList<String>observableList= FXCollections.observableArrayList();
    public static void main(String[] args) {
     launch(args);
    }


    @Override
    public void start(Stage stage) throws Exception {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 500)
                .usePlaintext()
                .build();
        NbrMgServiceGrpc.NbrMgServiceStub asyncstubs=NbrMgServiceGrpc.newStub(channel);

        BorderPane borderPane =new BorderPane();
        stage.setTitle("Client");
        Label label=new Label("Name");
        label.setTextFill(Color.WHITE);
        Label label1=new Label("Nombre");
        label1.setTextFill(Color.WHITE);
        TextField textUser=new TextField();
        TextField textNombre=new TextField();
        Button buttonSend=new Button("Envoyer");
        ListView<String> listView =new ListView<>(observableList);
        HBox hBox=new HBox(label,textUser,label1,textNombre,buttonSend);
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10));
        hBox.setBackground(new Background(new BackgroundFill(Color.PURPLE,null,null)));
        borderPane.setCenter(listView);
        borderPane.setTop(hBox);
        Scene scene=new Scene(borderPane);
        stage.setScene(scene);
        stage.show();


        StreamObserver<Jeu.Request> df =
                asyncstubs.nbrMagique(new StreamObserver<Jeu.Response>() {
                    @Override
                    public void onNext(Jeu.Response response) {
                        Platform.runLater(()->{
                            observableList.add(response.getEvent());

                        });

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        System.out.println(throwable.getMessage());
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("FIN");

                    }
              });
        buttonSend.setOnAction((actionEvent -> {
            String user=textUser.getText();
            int nombre=Integer.parseInt(textNombre.getText());
            Jeu.Request request= Jeu.Request.newBuilder()
                    .setUser(user)
                    .setNbrMg(nombre)
                    .build();
                 df.onNext(request);
                 Platform.runLater(()->{
                     textUser.setDisable(true);
                     textNombre.setText("");
                     observableList.add(String.valueOf(nombre));
                 });

        }));
    }
}
