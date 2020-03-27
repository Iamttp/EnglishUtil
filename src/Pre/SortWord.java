package Pre;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SortWord {
    public List<String> words = new ArrayList<>();

    public SortWord() {
        try {
            initWord(words, "a.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initWord(List<String> words, String file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String temp;
        while ((temp = br.readLine()) != null)
            if (!temp.equals("")) {
                temp = temp.split("\\s+")[0];
                words.add(temp);
            }
        br.close();


        FileWriter bw = new FileWriter("p" + file, true);
        // Levenshtein 距离，又称编辑距离,许可的编辑操作包括将一个字符替换成另一个字符，插入一个字符，删除一个字符。
        for (int i = 0; i < words.size(); i++) {
            List<Float> list = new ArrayList<>();
            for (String compWord : words)
                list.add(levenshtein(words.get(i), compWord));
            float p1, p2, p3;
            int i1, i2, i3;
            p1 = p2 = p3 = -1;
            i1 = i2 = i3 = -1;
            for (int j = 0; j < list.size(); j++) {
                if (j == i) continue;
                if (p1 < list.get(j)) {
                    i1 = j;
                    p1 = list.get(j);
                }
            }
            for (int j = 0; j < list.size(); j++) {
                if (j == i || j == i1) continue;
                if (p2 < list.get(j)) {
                    i2 = j;
                    p2 = list.get(j);
                }
            }
            for (int j = 0; j < list.size(); j++) {
                if (j == i || j == i1 || j == i2) continue;
                if (p3 < list.get(j)) {
                    i3 = j;
                    p3 = list.get(j);
                }
            }

            bw.write(i1 + "," + i2 + "," + i3 + "\n");
            int r = (int) (100 * i / (1.0 * words.size()));
            System.out.println(r);
        }
        bw.close();
    }

    public static float levenshtein(String str1, String str2) {
        //计算两个字符串的长度。
        int len1 = str1.length();
        int len2 = str2.length();
        //比字符长度大一个空间
        int[][] dif = new int[len1 + 1][len2 + 1];
        //赋初值，步骤B。
        for (int a = 0; a <= len1; a++) {
            dif[a][0] = a;
        }
        for (int a = 0; a <= len2; a++) {
            dif[0][a] = a;
        }
        //计算两个字符是否一样，计算左上的值
        int temp;
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    temp = 0;
                } else {
                    temp = 1;
                }
                //取三个值中最小的
                dif[i][j] = min(dif[i - 1][j - 1] + temp, dif[i][j - 1] + 1,
                        dif[i - 1][j] + 1);
            }
        }
        //计算相似度
        float similarity = 1 - (float) dif[len1][len2] / Math.max(str1.length(), str2.length());
        return similarity;
    }

    //得到最小值
    private static int min(int... is) {
        int min = Integer.MAX_VALUE;
        for (int i : is) {
            if (min > i) {
                min = i;
            }
        }
        return min;
    }

    // 产生pa文件，保存相似单词下标
    public static void main(String[] args) {
//        new SortWord();
    }
}
