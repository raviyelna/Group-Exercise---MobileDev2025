package com.example.comicreaderapp.utils;

import java.util.ArrayList;
import java.util.List;

public class SearchHelper {

    public interface SearchCondition<T> {
        boolean match(T item, String keyword);
    }

    public static <T> List<T> filter(
            List<T> source,
            String keyword,
            SearchCondition<T> condition
    ) {
        List<T> result = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            result.addAll(source);
            return result;
        }

        String key = keyword.toLowerCase().trim();

        for (T item : source) {
            if (condition.match(item, key)) {
                result.add(item);
            }
        }
        return result;
    }
}

