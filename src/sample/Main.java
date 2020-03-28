package sample;

import com.melloware.jintellitype.JIntellitype;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.media.AudioClip;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static sample.Util.readLineSearch;
import static sample.Util.searchFileContent;

public class Main extends Application {
    private Word word = new Word();

    ArrayList<String> tempArray = new ArrayList<>();
    private boolean isTesting = false;
    private String nowWord;
    private int score = 0;
    private boolean process = false;
    private boolean isTrue = false;
    private boolean isFirst = true;

    private Note note = new Note();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        primaryStage.setTitle("Demo");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("sample.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setHeight(80);
        primaryStage.setResizable(false);//窗口不可改变高度 宽度 这样就不用调节自适应了
        primaryStage.setOpacity(0.8);//设置透明度 0为完全透明 1为完全不透明 默认是1
        primaryStage.initStyle(StageStyle.UNDECORATED);//设定窗口无边框
        primaryStage.show();

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX((screenBounds.getWidth() - scene.getWidth()) / 2);
        primaryStage.setY((screenBounds.getHeight() - 400) / 2);

        // 一定show();完再lookup
        TextField input = (TextField) root.lookup("#input");
        ListView<String> listview = (ListView<String>) root.lookup("#listView");

        // 朗读
        ChangeListener<Object> changeListener = (observable1, oldValue1, newValue1) -> {
            Platform.runLater(() -> {
                if (newValue1 == null) return;
                String str = (String) newValue1;
                String res = str;
                if (isTesting && !process) {
                    process = true;
                    isTrue = nowWord.contains((String) newValue1);
                    tempArray.add(" ");
                    tempArray.add(isTrue + "\tAlt+A继续");
                    listview.getItems().clear();
                    listview.getItems().addAll(tempArray);
                    primaryStage.setHeight(400);
                }
                // 判断中文
                Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
                Matcher m = p.matcher(str);
                if (m.find()) res = str.split("\\s+")[0];
                Matcher m2 = p.matcher(res);
                if (m2.find()) return;
                res = res.replaceAll("\\s+", "%20");
                AudioClip audioClip = new AudioClip("http://dict.youdao.com/dictvoice?audio=" + res);
                audioClip.play();
//                        listview.getSelectionModel().selectedItemProperty().removeListener(this);
            });
        };
        listview.getSelectionModel().selectedItemProperty().addListener(changeListener);

        input.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isTesting) return;
            String trimed = newValue.trim();
            if (trimed.length() <= 0) {
                primaryStage.setHeight(80);
                return;
            }
            if (trimed.charAt(0) == '`') {
                // 后面的`相当于回车
                if (trimed.length() <= 3 || trimed.charAt(trimed.length() - 1) != '`') {
                    primaryStage.setHeight(80);
                    return;
                }

                // 文件查找 格式举例 `-r=C:\Users\ttp\Desktop -c=你好 -e=.txt!.cpp`  `-r=F: -e=txt -c=hello`  `-r=F: -c=hello` 可能爆栈
                String[] res = readLineSearch(trimed);
                if (res[2].charAt(res[2].length() - 1) != '\\') res[2] += "\\"; // 添加\

                // 文件查找线程开启，总不能让GUI卡死吧
                new Thread(() -> {
                    try {
                        listview.getItems().clear();
                        listview.getItems().add("正在查找");
                        List<String> searchResult = searchFileContent(res[2], res[0], res[1]);
                        listview.getItems().clear();
                        listview.getItems().add("查找完成");
                        listview.getItems().addAll(searchResult);
//                        ChangeListener<Object> changeListener = new ChangeListener<Object>() {
//                            @Override
//                            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
//                                // 获取系统剪贴板,是awt包下的
//                                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//                                Transferable trans = new StringSelection((String) newValue);
//                                clipboard.setContents(trans, null);
//                                listview.getSelectionModel().selectedItemProperty().removeListener(this);
//                            }
//                        };
//                        listview.getSelectionModel().selectedItemProperty().addListener(changeListener);
                    } catch (Exception e1) {
                        listview.getItems().clear();
                        listview.getItems().add(e1.toString());
                        e1.printStackTrace();
                    }
                }).start();
            } else {
                if (trimed.contains(" ")) {
                    String str = null;
                    String urlStr = "http://fanyi.youdao.com/openapi.do?keyfrom=youdao111&key=60638690&type=data&doctype=xml&version=1.1&q=" + trimed;
                    try {
                        URL url = new URL(urlStr);
                        URLConnection urlConnection = url.openConnection(); // 打开连接
                        BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8")); // 获取输入流
                        String line = null;
                        StringBuilder sb = new StringBuilder();
                        while ((line = br.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                        br.close();
                        str = sb.toString();
                    } catch (Exception e1) {
                        listview.getItems().clear();
                        listview.getItems().add(e1.toString());
                        e1.printStackTrace();
                    }

                    Pattern pe = Pattern.compile("\\[CDATA\\[(?<q>.*?)\\]");
//                    Pattern pe = Pattern.compile("\\<paragraph\\>\\<\\!\\[CDATA\\[(?<q>.*?)\\]");
                    Matcher me = pe.matcher(str);
                    listview.getItems().clear();
                    while (me.find())
                        listview.getItems().addAll(me.group("q"));
                } else {
                    // 单词查找
                    List<String> searchResult1 = Word.search(trimed, word.wordsA, String::contains);
                    List<String> searchResult2 = Word.search(trimed, word.wordDict, String::contains);

                    listview.getItems().clear();
                    listview.getItems().addAll(searchResult1);
                    listview.getItems().addAll(searchResult2);
                }
            }
            primaryStage.setHeight(400);
        });

        // 系统托盘
        SystemTray tray = SystemTray.getSystemTray();
        //此处不能选择ico格式的图片,要使用16*16的png格式的图片
        BufferedImage image = ImageIO.read(new File("my.png"));
        assert image != null;
        TrayIcon trayIcon = new TrayIcon(image, "MyUtil");
        tray.add(trayIcon);
        trayIcon.addMouseListener(new MouseAdapter() {
            // 鼠标事件
            public void mouseClicked(MouseEvent e) {
                // 鼠标右键关闭
                if (e.getButton() == 3) {
                    note.save();
                    Platform.exit();
                    System.exit(0); // 必杀退出法
                    return;
                }
                // 判断是否双击了鼠标
                if (e.getClickCount() == 2) {
                    Platform.setImplicitExit(false); //多次使用显示和隐藏设置false
                    if (primaryStage.isShowing()) {
                        Platform.runLater(primaryStage::hide);
                    } else {
                        Platform.runLater(primaryStage::show);
                    }
                }
            }
        });

        // 全局按键监听
        int GLOBAL_HOT_KEY_1 = 0;
        int GLOBAL_HOT_KEY_2 = 1;
        int GLOBAL_HOT_KEY_3 = 2;
        int GLOBAL_HOT_KEY_4 = 3;
        int GLOBAL_HOT_KEY_5 = 4;
        JIntellitype.getInstance().registerHotKey(GLOBAL_HOT_KEY_1, JIntellitype.MOD_ALT, (int) 'Q');
        JIntellitype.getInstance().registerHotKey(GLOBAL_HOT_KEY_2, JIntellitype.MOD_ALT, (int) 'Z');
        JIntellitype.getInstance().registerHotKey(GLOBAL_HOT_KEY_3, JIntellitype.MOD_ALT, (int) 'A');
        JIntellitype.getInstance().registerHotKey(GLOBAL_HOT_KEY_4, JIntellitype.MOD_ALT, (int) 'X');
        JIntellitype.getInstance().registerHotKey(GLOBAL_HOT_KEY_5, JIntellitype.MOD_ALT, (int) 'C');
        JIntellitype.getInstance().addHotKeyListener(markCode -> {
                    Platform.runLater(() -> {
                        if (markCode == GLOBAL_HOT_KEY_1) {
                            Platform.setImplicitExit(false); //多次使用显示和隐藏设置false
                            if (primaryStage.isShowing()) {
                                Platform.runLater(primaryStage::hide);
                            } else {
                                Platform.runLater(primaryStage::show);
                            }
                        } else if (markCode == GLOBAL_HOT_KEY_2) {
                            note.save();
                            Platform.exit();
                            System.exit(0); // 必杀退出法
                        } else if (markCode == GLOBAL_HOT_KEY_3) {
                            next(listview, input, primaryStage);
                        } else if (markCode == GLOBAL_HOT_KEY_4) {
                            isTesting = false;
                        } else if (markCode == GLOBAL_HOT_KEY_5) {
                            note.func();
                        }
                    });
                }
        );
    }

    public void next(ListView<String> listview, TextField input, Stage primaryStage) {
        isTesting = true;
        process = false;
        // 测试
        int index = (int) (Math.random() * word.wordsA.size());
        nowWord = word.wordsA.get(index);
        String[] likeWordIndex = word.wordPa.get(index).split(",");

        tempArray.clear();
        tempArray.add(nowWord);
        for (String wordIndex : likeWordIndex) tempArray.add(word.wordsA.get(Integer.parseInt(wordIndex)));
        Collections.shuffle(tempArray);

        input.setText(nowWord.split("\\s+")[0]);
        listview.getItems().clear();
        List<String> likeWords2 = new ArrayList<>();
        for (String s : tempArray) {
            String[] temp = s.split("\\s+");
            likeWords2.add(temp[1]);
        }
        likeWords2.add(" ");
        if (isFirst) {
            likeWords2.add("开始测试" + "\t当前分数为：" + score);
            isFirst = false;
        } else {
            score = isTrue ? score + 10 : score - 10;
            likeWords2.add((isTrue ? "刚刚回答正确" : "刚刚回答错误") + "\t当前分数为：" + score);
        }
        listview.getItems().addAll(likeWords2);
        primaryStage.setHeight(400);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
