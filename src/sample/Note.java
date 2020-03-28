package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;

public class Note extends Application {
    private Stage primaryStage;
    StringBuilder res = new StringBuilder();
    TextArea textArea;

    public Note() {
        File file = new File("test.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            BufferedReader br;//构造一个BufferedReader类来读取文件
            try {
                br = new BufferedReader(new FileReader("test.txt"));
                String temp;
                while ((temp = br.readLine()) != null) res.append(temp).append("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Stage stage = new Stage();
        try {
            start(stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("sample2.fxml"));

        this.primaryStage = primaryStage;
        primaryStage.setTitle("Demo");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("sample.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);//窗口不可改变高度 宽度 这样就不用调节自适应了
        primaryStage.setOpacity(0.6);//设置透明度 0为完全透明 1为完全不透明 默认是1
        primaryStage.initStyle(StageStyle.UNDECORATED);//设定窗口无边框

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX((screenBounds.getWidth() + 600) / 2);
        primaryStage.setY((screenBounds.getHeight() - 400) / 2);

        textArea = (TextArea) root.lookup("#textArea");
        textArea.setText(res.toString());
    }

    public void func() {
        save();
        Platform.setImplicitExit(false); //多次使用显示和隐藏设置false
        if (primaryStage.isShowing()) {
            Platform.runLater(primaryStage::hide);
        } else {
            Platform.runLater(primaryStage::show);
        }
    }

    public void save() {
        FileWriter bw;
        try {
            bw = new FileWriter("test.txt");
            bw.write(textArea.getText());
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
