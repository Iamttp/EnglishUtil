package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MyRect extends Application {
    Stage stage;
    static double sceneX_start;
    static double sceneY_start;
    static double sceneX_end;
    static double sceneY_end;
    static HBox hbox;

    /* 矩形拖拉 */
    public void drag(AnchorPane an) {
        an.setOnMousePressed(arg0 -> {
            an.getChildren().clear();
            hbox = new HBox();
            hbox.setBackground(null);
            hbox.setBorder(new Border(new BorderStroke(Paint.valueOf("#CD3700"), BorderStrokeStyle.SOLID, null,
                    new BorderWidths(2))));
            /* 获取坐标 */
            sceneX_start = arg0.getSceneX();
            sceneY_start = arg0.getSceneX();

            an.getChildren().add(hbox);

            AnchorPane.setLeftAnchor(hbox, sceneX_start);
            AnchorPane.setTopAnchor(hbox, sceneY_start);
        });

        /* 拖拽检测 */
        an.setOnDragDetected(event -> {
            an.startFullDrag();
        });

        /* 拖拽获取坐标 */
        an.setOnMouseDragOver((EventHandler<MouseEvent>) event -> {
            Label label = new Label();
            label.setAlignment(Pos.CENTER);
            label.setPrefWidth(170);
            label.setPrefHeight(30);

            an.getChildren().add(label);

            AnchorPane.setLeftAnchor(label, sceneX_start);
            AnchorPane.setTopAnchor(label, sceneY_start - label.getPrefHeight());

            label.setTextFill(Paint.valueOf("#ffffff"));
            label.setStyle("-fx-background-color:#000000");

            double sceneX = event.getSceneX();
            double sceneY = event.getSceneY();

            double width = sceneX - sceneX_start;
            double height = sceneY - sceneY_start;

            hbox.setPrefWidth(width);
            hbox.setPrefHeight(height);

            label.setText("宽度：" + width + "高度：" + height);
        });

        /*当鼠标拖拽出矩形后，可以通过点击完成，得到截图*/
        an.setOnMouseDragExited(event -> {
            sceneX_end = event.getSceneX();
            sceneY_end = event.getSceneY();

            Button btn_fin = new Button("完成");
            hbox.getChildren().add(btn_fin);
            hbox.setAlignment(Pos.BOTTOM_RIGHT);

            btn_fin.setOnAction(arg0 -> {
                stage.close();// 关闭当前窗口
                double w = sceneX_end - sceneX_start;
                double h = sceneY_end - sceneY_start;
                /*截图*/
                Robot robot = null;
                try {
                    robot = new Robot();
                } catch (AWTException e) {
                    e.printStackTrace();
                }
                Rectangle rec = new Rectangle((int) sceneX_start, (int) sceneY_start, (int) w, (int) h);
                assert robot != null;
                BufferedImage buffimg = robot.createScreenCapture(rec);
                try {
                    File desktopDir = FileSystemView.getFileSystemView().getHomeDirectory();
                    ImageIO.write(buffimg, "png", new File(desktopDir.getAbsolutePath() + "\\img.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });

    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        AnchorPane root = new AnchorPane();
        root.setStyle("-fx-background-color:#B5B5B522");
        Scene scene = new Scene(root);
        scene.setFill(Paint.valueOf("#ffffff00"));
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);// 全屏
        primaryStage.setFullScreenExitHint("");// 设置空字符串
        primaryStage.initStyle(StageStyle.TRANSPARENT);// 透明
        primaryStage.show();
        drag(root);//调用矩形拖拉
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                primaryStage.close();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
