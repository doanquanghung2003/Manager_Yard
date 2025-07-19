package bean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.JsonObject;

public class UserModel implements Bean {
    private String userId;
    private String userName;
    private String password;
    private String email;
    private String fullName;
    private String roleId;
    private LocalDateTime createAt;

    private String role; // Thêm trường role để phân quyền

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    public UserModel() {
      
        this.createAt = LocalDateTime.now();
        this.role = "user"; // Mặc định là user
    }


    public UserModel(String userName, String password, String email, String fullName, String roleId, LocalDateTime createAt) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.roleId = roleId;
        this.createAt = createAt != null ? createAt : LocalDateTime.now();
        this.role = role != null ? role : "user";
    }

    // --- Getter và Setter ---
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // --- Ghi JSON ---
    @Override
    public JsonObject toJsonObject() {
        JsonObject obj = new JsonObject();
        obj.addProperty("userId", userId);
        obj.addProperty("userName", userName);
        obj.addProperty("password", password);
        obj.addProperty("email", email);
        obj.addProperty("fullName", fullName);
        obj.addProperty("roleId", roleId);
        obj.addProperty("createAt", createAt != null ? createAt.format(FORMATTER) : "");
        obj.addProperty("role", role); // Ghi role vào JSON
        return obj;
    }

    // --- Đọc JSON ---
    @Override
    public void parse(JsonObject obj) throws Exception {
        if (obj == null) return;

        this.userId = obj.has("userId") ? obj.get("userId").getAsString() : null;
        this.userName = obj.has("userName") ? obj.get("userName").getAsString() : null;
        this.password = obj.has("password") ? obj.get("password").getAsString() : null;
        this.email = obj.has("email") ? obj.get("email").getAsString() : null;
        this.fullName = obj.has("fullName") ? obj.get("fullName").getAsString() : null;
        this.roleId = obj.has("roleId") ? obj.get("roleId").getAsString() : null;

        if (obj.has("createAt") && !obj.get("createAt").getAsString().isEmpty()) {
            this.createAt = LocalDateTime.parse(obj.get("createAt").getAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } else {
            this.createAt = null;
        }
        this.role = obj.has("role") ? obj.get("role").getAsString() : "user"; // Đọc role từ JSON, mặc định user
    }
}
