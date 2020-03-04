package sample;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    static Set<String> set = new HashSet<String>() {
        {
            add(".BIN");
            add(".bin");
            add(".EXE");
            add(".exe");
            add(".DLL");
            add(".dll");
            add(".MSI");
            add(".msi");
            add(".A");
            add(".a");
            add(".lib");
            add(".tar");
            add(".zip");
            add(".jar");
            add(".jpg");
            add(".png");
            add(".gif");
            add(".map");
            add(".PNG");
            add(".mp3");
            add(".mp4");
            add(".o");
            add(".O");
            add(".pyc");
            add(".class");
            add(".dat");
            add(".c~");
            add(".hprof");
            add(".so");
            add(".ttf");
        }
    };


    public static boolean isBinary(File file) throws IOException {
        FileInputStream fin = new FileInputStream(file);
        long len = file.length();
        for (int j = 0; j < (int) len; j++) {
            int t = fin.read();
            if (t < 32 && t != 9 && t != 10 && t != 13) {
                fin.close();
                return true;
            }
        }
        fin.close();
        return false;
    }

    public static void getAllFileName(String path, ArrayList<String> listFileName) {
        File file = new File(path);
        File[] files = file.listFiles();
        String[] names = file.list();

        if (names != null) {
            String[] completNames = new String[names.length];
            for (int i = 0; i < names.length; i++)
                completNames[i] = path + names[i];
            listFileName.addAll(Arrays.asList(completNames));
        }

        if (files != null)
            for (File f : files)
                if (f.isDirectory() && f.getName().length() >= 1 && f.getName().charAt(0) != '.')
                    getAllFileName(f.getAbsolutePath() + "\\", listFileName);
    }

    public static String[] readLineSearch(String str) {
        Pattern pc = Pattern.compile("-c=(?<q>.*?)[`\\s]");
        Pattern pe = Pattern.compile("-e=(?<q>.*?)[`\\s]");
        Pattern pr = Pattern.compile("-r=(?<q>.*?)[`\\s]");
        Matcher mc = pc.matcher(str);
        Matcher me = pe.matcher(str);
        Matcher mr = pr.matcher(str);
        String[] res = new String[3];
        if (me.find()) res[0] = me.group("q");
        if (mc.find()) res[1] = mc.group("q");
        if (mr.find()) res[2] = mr.group("q");
        return res;
    }

    public static List<String> searchFileContent(String rootPath, String end, String content) {
        List<String> res = new ArrayList<>();

        ArrayList<String> listFileName = new ArrayList<>();
        getAllFileName(rootPath, listFileName);
        res.add("该路径下共有文件数：" + listFileName.size());

        ArrayList<String> listFileName2 = new ArrayList<>();
        if (end == null || end.equals("")) {
            listFileName2 = listFileName;
        } else {
            String[] rail = end.split("!");
            for (String name : listFileName)
                for (String item : rail)
                    if (name.contains(item)) { // 用的contains，所以不一定是后缀
                        listFileName2.add(name);
                        break;
                    }
            listFileName.clear(); // 用完了，及时(ノ｀Д)ノ  java是引用，所以上面的条件还是不要滚
        }
        res.add("该路径下后缀满足共有文件数：" + listFileName2.size());

        if (content == null || content.equals("")) { // 表示不查找文件内容
            res.add("---查找成功---");
            res.addAll(listFileName2);
            return res;
        }

        // https://zhidao.baidu.com/question/588878015.html
        // 先把二进制文件统统continue了
        ArrayList<String> listFileName3 = new ArrayList<>();
        for (String name : listFileName2) {
            StringBuilder stringBuilder = new StringBuilder(); // 大文件读取会爆栈
            try {
                //获取文件的后缀名
                if(name.contains(".")){
                    String suffix = name.substring(name.lastIndexOf("."));
                    if (set.contains(suffix)) {
                        System.out.println("忽略二进制文件" + name);
                        continue;
                    }
                }
                File file = new File(name);
                if (isBinary(file)) {
                    System.out.println("忽略二进制文件" + name);
                    continue;
                }

                FileReader fileReader = new FileReader(name);
                BufferedReader bufferedReader = new BufferedReader(fileReader);

                String line = bufferedReader.readLine();
                while (line != null) {
                    line = bufferedReader.readLine();
                    stringBuilder.append(line);
                }

                bufferedReader.close();
                fileReader.close();
            } catch (Exception e) { // 什么文件无法访问呢，文件太大呀，直接continue
                res.add(e.toString());
                continue;
            }
            if (stringBuilder.toString().contains(content))
                listFileName3.add(name);
        }

        if (listFileName3.size() > 0) {
            res.add("---查找成功---");
            res.add("该路径下后缀满足且内容满足共有文件数：" + listFileName3.size());
            res.addAll(listFileName3);
        } else {
            res.add("---未发现该内容文件---");
        }
        return res;
    }
}