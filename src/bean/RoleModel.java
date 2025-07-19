package bean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import com.google.gson.JsonObject;

public class RoleModel implements Bean {
    private String roleId;
    private String roleName;
    private String description;
    private List<String> permissions;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public RoleModel() {
        this.permissions = new ArrayList<>();
        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }

    public RoleModel(String roleName, String description, List<String> permissions) {
        this.roleName = roleName;
        this.description = description;
        this.permissions = permissions != null ? permissions : new ArrayList<>();
        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }

    // --- Getter và Setter ---
    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    // --- Ghi JSON ---
    @Override
    public JsonObject toJsonObject() {
        JsonObject obj = new JsonObject();
        obj.addProperty("roleId", roleId);
        obj.addProperty("roleName", roleName);
        obj.addProperty("description", description);
        obj.addProperty("createAt", createAt != null ? createAt.format(FORMATTER) : "");
        obj.addProperty("updateAt", updateAt != null ? updateAt.format(FORMATTER) : "");
        
        // Convert permissions list to JSON array
        com.google.gson.JsonArray permissionsArray = new com.google.gson.JsonArray();
        if (permissions != null) {
            for (String permission : permissions) {
                permissionsArray.add(permission);
            }
        }
        obj.add("permissions", permissionsArray);
        
        return obj;
    }

    // --- Đọc JSON ---
    @Override
    public void parse(JsonObject obj) throws Exception {
        if (obj == null) return;

        this.roleId = obj.has("roleId") ? obj.get("roleId").getAsString() : null;
        this.roleName = obj.has("roleName") ? obj.get("roleName").getAsString() : null;
        this.description = obj.has("description") ? obj.get("description").getAsString() : null;

        if (obj.has("createAt") && !obj.get("createAt").getAsString().isEmpty()) {
            this.createAt = LocalDateTime.parse(obj.get("createAt").getAsString(), FORMATTER);
        } else {
            this.createAt = null;
        }

        if (obj.has("updateAt") && !obj.get("updateAt").getAsString().isEmpty()) {
            this.updateAt = LocalDateTime.parse(obj.get("updateAt").getAsString(), FORMATTER);
        } else {
            this.updateAt = null;
        }

        // Parse permissions array
        this.permissions = new ArrayList<>();
        if (obj.has("permissions") && obj.get("permissions").isJsonArray()) {
            com.google.gson.JsonArray permissionsArray = obj.get("permissions").getAsJsonArray();
            for (com.google.gson.JsonElement element : permissionsArray) {
                this.permissions.add(element.getAsString());
            }
        }
    }
} 