package org.example.repository;

import org.example.models.Movie;
import org.example.models.Theatre;

import java.util.HashMap;
import java.util.Map;

public class TheatreRepository {
    Map<String, Theatre> map = new HashMap<>();

    public  void save(Theatre theatre) {
        map.put(theatre.getId(), theatre);
    }

    public Theatre get(String id) {
        return map.get(id);
    }
}
