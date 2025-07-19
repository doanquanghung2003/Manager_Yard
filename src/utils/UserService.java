package utils;

import bean.UserModel;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserService {
    private static final String USER_FILE = "data/users.json";
    private static final Gson gson = new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(java.time.LocalDateTime.class, (com.google.gson.JsonSerializer<LocalDateTime>) 
            (src, typeOfSrc, context) -> new com.google.gson.JsonPrimitive(src.format(java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd"))))
        .registerTypeAdapter(java.time.LocalDateTime.class, (com.google.gson.JsonDeserializer<LocalDateTime>)
            (json, type, context) -> LocalDateTime.parse(json.getAsString() + "T00:00:00"))
        .create();

    public static List<UserModel> loadUsers() {
        try (Reader reader = new FileReader(USER_FILE)) {
            List<UserModel> users = gson.fromJson(reader, new TypeToken<List<UserModel>>(){}.getType());
            return users != null ? users : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void saveUsers(List<UserModel> users) {
        try (Writer writer = new FileWriter(USER_FILE)) {
            gson.toJson(users, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isUsernameExists(String username) {
        return loadUsers().stream().anyMatch(u -> u.getUserName().equalsIgnoreCase(username));
    }

    public static boolean checkLogin(String username, String password) {
        return loadUsers().stream().anyMatch(u -> u.getUserName().equalsIgnoreCase(username) && u.getPassword().equals(password));
    }

    public static void addUser(UserModel user) {
        List<UserModel> users = loadUsers(); // Đọc toàn bộ user hiện có
        System.out.println("Số user trước khi thêm: " + users.size());
        users.add(user);                     // Thêm user mới vào danh sách
        System.out.println("Số user sau khi thêm: " + users.size());
        saveUsers(users);     
        System.out.println(new File(USER_FILE).getAbsolutePath());               // Ghi lại toàn bộ danh sách
    }

    public static UserModel getUserByUsername(String username) {
        return loadUsers().stream()
            .filter(u -> u.getUserName().equalsIgnoreCase(username))
            .findFirst().orElse(null);
    }

    public static UserModel getUserByLogin(String username, String password) {
        try (FileReader reader = new FileReader(USER_FILE)) {
            List<UserModel> users = gson.fromJson(reader, new TypeToken<List<UserModel>>(){}.getType());
            for (UserModel user : users) {
                if (user.getUserName().equals(username) && user.getPassword().equals(password)) {
                    return user;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
