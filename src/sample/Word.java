package sample;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Word {
    public List<String> words = new ArrayList<>();
    public List<String> words2 = new ArrayList<>();

    public Word() {
        initWord(words, "a.txt");
        initWord(words2, "dict.txt");
    }

    public static void initWord(List<String> words, String file) {
        BufferedReader br;//构造一个BufferedReader类来读取文件
        try {
            br = new BufferedReader(new FileReader(file));
            String temp;
            while ((temp = br.readLine()) != null)
                if (!temp.equals(""))
                    words.add(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 通用的查找函数
    public static List<String> search(String keyword, Collection<String> tCollection, BiFunction<String, String, Boolean> matcher) {
        return (tCollection == null || matcher == null) ?
                Collections.emptyList() :
                tCollection.stream()
                        .filter(t -> matcher.apply(t, keyword))
                        .collect(Collectors.toList());
    }
}
