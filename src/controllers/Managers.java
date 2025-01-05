package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.DurationAdapter;
import server.LocalDateTimeAdapter;

import java.time.Duration;
import java.time.LocalDateTime;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }

    public static Gson getDefaultGson() {
        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .serializeNulls();
        return gsonBuilder.create();
    }
}