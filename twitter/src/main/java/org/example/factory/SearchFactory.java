package org.example.factory;

import org.example.strategy.searchStrategy.ContentSearchStrategy;
import org.example.strategy.searchStrategy.HashtagSearchStrategy;
import org.example.strategy.searchStrategy.SearchStrategy;
import org.example.strategy.searchStrategy.UserSearchStrategy;

public class SearchFactory {

    public static SearchStrategy getSearchStrategy(String type) {
        switch (type.toUpperCase()) {
            case "CONTENT":
                return new ContentSearchStrategy();
            case "HASHTAG":
                return new HashtagSearchStrategy();
            case "USER":
                return new UserSearchStrategy();
            default:
                throw new IllegalArgumentException("Unknown search type: " + type);
        }
    }
}
