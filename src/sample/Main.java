package sample;

import com.melloware.jintellitype.JIntellitype;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class Main extends Application {
    private Word word = new Word();
    private InstantSearch<String> instantSearch = new InstantSearch<>(word.words, String::contains);
    MyRect open = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Application.setUserAgentStylesheet(getClass().getResource("sample.css").toExternalForm());

        TextField Input = (TextField) root.lookup("#Input");
        ListView<String> listview = (ListView<String>) root.lookup("#listView");

        listview.setVisible(false);

        Input.textProperty().addListener((observable, oldValue, newValue) -> {
            String trimed = newValue.trim();
            if (trimed.length() > 0) {
                List<String> searchResult = instantSearch.search(trimed);
                listview.getItems().clear();
                listview.getItems().addAll(searchResult);
                listview.setVisible(true);
                primaryStage.setHeight(400);
            } else {
                listview.setVisible(false);
                primaryStage.setHeight(120);
            }
        });

        listview.getSelectionModel().selectedItemProperty().addListener(new NoticeListItemChangeListener());

        primaryStage.setTitle("Demo");
        primaryStage.setScene(new Scene(root));
        primaryStage.setHeight(120);
        primaryStage.setResizable(false);//窗口不可改变高度 宽度 这样就不用调节自适应了
        primaryStage.setOpacity(0.8);//设置透明度 0为完全透明 1为完全不透明 默认是1
        primaryStage.initStyle(StageStyle.UNDECORATED);//设定窗口无边框
        primaryStage.show();

        SystemTray tray = SystemTray.getSystemTray();
        //此处不能选择ico格式的图片,要使用16*16的png格式的图片
        BufferedImage image = null;
        image = ImageIO.read(new File("my.png"));
        assert image != null;
        TrayIcon trayIcon = new TrayIcon(image, "开始");
        trayIcon.setToolTip("ttp");
        tray.add(trayIcon);
        MouseListener mouseListener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Platform.setImplicitExit(false); //多次使用显示和隐藏设置false
                if (e.getClickCount() == 2) {
                    if (primaryStage.isShowing()) {
                        Platform.runLater(primaryStage::hide);
                    } else {
                        Platform.runLater(primaryStage::show);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        };
        trayIcon.addMouseListener(mouseListener);

        int GLOBAL_HOT_KEY_1 = 0;
        int GLOBAL_HOT_KEY_2 = 1;
        JIntellitype.getInstance().registerHotKey(GLOBAL_HOT_KEY_1, JIntellitype.MOD_SHIFT, (int) 'Q');
        JIntellitype.getInstance().registerHotKey(GLOBAL_HOT_KEY_2, JIntellitype.MOD_SHIFT, (int) 'A');
        JIntellitype.getInstance().addHotKeyListener(markCode -> {
            if (markCode == GLOBAL_HOT_KEY_1) {
                Platform.setImplicitExit(false); //多次使用显示和隐藏设置false
                if (primaryStage.isShowing()) {
                    Platform.runLater(primaryStage::hide);
                } else {
                    Platform.runLater(primaryStage::show);
                }
            } else if (markCode == GLOBAL_HOT_KEY_2) {
                Platform.setImplicitExit(false); //多次使用显示和隐藏设置false
                if (primaryStage.isShowing()) {
                    Platform.runLater(primaryStage::hide);
                }

                Platform.runLater(() -> {
                    if (open != null) return;
                    open = new MyRect();
                    open.start(new Stage());
                });
            }
        });
    }

    static class NoticeListItemChangeListener implements ChangeListener<Object> {
        @Override
        public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
            if (newValue == null) return;
            String str = (String) newValue;
            String res = str.split("\\s+")[0];
            AudioClip audioClip = new AudioClip("http://dict.youdao.com/dictvoice?audio=" + res);
            audioClip.play();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
