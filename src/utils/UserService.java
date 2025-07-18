package utils;

import bean.UserModel;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.*;
import java.util.*;

public class UserService {
    private static final String USER_FILE = "users.json";
    private static final Gson gson = new Gson();

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
        List<UserModel> users = loadUsers();
        users.add(user);
        saveUsers(users);
    }
}
