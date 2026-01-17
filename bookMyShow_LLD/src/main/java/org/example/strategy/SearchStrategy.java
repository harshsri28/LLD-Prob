package org.example.strategy;

import org.example.model.Catalog;
import org.example.model.Movie;
import java.util.List;

public interface SearchStrategy {
    List<Movie> search(Catalog catalog, String query);
}
