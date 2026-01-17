package org.example.models;

import java.util.HashMap;
import java.util.Map;

public class Theatre {
    String id;
    String name;
    Map<String, Screen> screens = new HashMap<>();

    public Theatre(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public boolean addScreen(Screen screen) {
        if (screens.containsKey(screen.getId())) {
            return false;
        }
        screens.put(screen.getId(), screen);
        return true;
    }

    public Screen getScreen(String screenId) {
        return screens.get(screenId);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Map<String, Screen> getScreens() {
        return screens;
    }
}
