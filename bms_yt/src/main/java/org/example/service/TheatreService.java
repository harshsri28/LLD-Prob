package org.example.service;

import org.example.models.Theatre;
import org.example.repository.TheatreRepository;

public class TheatreService {
    TheatreRepository theatreRepo;

    public TheatreService(TheatreRepository theatreRepo) {
        this.theatreRepo = theatreRepo;
    }

    public Theatre createTheatre(String id, String name){
        Theatre theatre = new Theatre(id, name);
        theatreRepo.save(theatre);
        return theatre;
    }

    public Theatre getTheatre(String theatreId){
        return theatreRepo.get(theatreId);
    }
}
