package sample;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class InstantSearch<T> {

    private Collection<T> tCollection;

    private BiFunction<T, String, Boolean> matcher;

    public InstantSearch(Collection<T> tCollection, BiFunction<T, String, Boolean> matcher) {
        this.tCollection = tCollection;
        this.matcher = matcher;
    }

    public List<T> search(String keyword) {
        return (tCollection == null || matcher == null) ?
                Collections.emptyList() :
                tCollection.stream()
                        .filter(t -> matcher.apply(t, keyword))
                        .collect(Collectors.toList());
    }
}