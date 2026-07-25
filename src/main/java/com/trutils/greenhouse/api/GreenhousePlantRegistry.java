package com.trutils.greenhouse.api;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GreenhousePlantRegistry {
    private static final List<GreenhousePlantHandler> HANDLERS = new CopyOnWriteArrayList<>();

    public static void register(GreenhousePlantHandler handler) {
        HANDLERS.add(handler);
    }

    public static List<GreenhousePlantHandler> getHandlers() {
        return Collections.unmodifiableList(HANDLERS);
    }
}
