package io.github.uniclog.jsticky.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.Scanner;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

public class FileServiceWrapper {
    public static <T> T saveObjectAsText(T object, String path) {
        try (var writer = new FileWriter(path)) {
            writer.write(object.toString());
            return object;
        } catch (Exception e) {
            return null;
        }
    }

    public static String loadObjectFromTextFile(String path) {
        try (var reader = new FileReader(path); var scan = new Scanner(reader)) {
            var builder = new StringBuilder();
            while (scan.hasNextLine()) {
                builder.append(scan.nextLine()).append("\n");
            }
            return builder.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getFileName(String path) {
        if ("".equals(path) || path.lastIndexOf("\\") == -1)
            return "";

        var begin = path.lastIndexOf("\\") + 1;
        var end = path.indexOf(".", begin);
        return end == -1 ? path.substring(begin) : path.substring(begin, end);
    }

    public static <T> T saveObjectAsJson(String path, T object) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(object, object.getClass());
        return nonNull(saveObjectAsText(json, path)) ? object : null;
    }

    public static <T> T loadObjectFromJson(String path, Type objectType) {
        try {
            return new Gson().fromJson(requireNonNull(loadObjectFromTextFile(path)), objectType);
        } catch (NullPointerException | JsonSyntaxException e) {
            return null;
        }
    }
}
