package org.example.strategy;

import org.example.model.Catalog;
import org.example.model.Movie;
import java.util.List;

public class TitleSearchStrategy implements SearchStrategy {
    @Override
    public List<Movie> search(Catalog catalog, String title) {
        return catalog.searchByTitle(title);
    }
}
