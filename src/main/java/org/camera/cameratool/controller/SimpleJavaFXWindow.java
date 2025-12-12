package org.camera.cameratool.controller;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class SimpleJavaFXWindow extends Application {
    @Override
    public void start(Stage primaryStage) {
        // 创建按钮
        Button button = new Button("点击我");
        button.setOnAction(e -> {
            System.out.println("按钮被点击了！");
        });

        // 创建布局
        StackPane root = new StackPane();
        root.getChildren().add(button);

        // 创建场景并设置窗口
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("JavaFX 窗口");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args); // 启动 JavaFX 应用程序
    }
}
