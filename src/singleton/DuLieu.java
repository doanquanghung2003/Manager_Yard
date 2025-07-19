package singleton;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import bean.BookingModel;
import bean.ServicesModel;
import bean.TimeSlot;
import bean.UserModel;
import bean.YardModel;

import utils.JsonUtils;

import java.util.*;
import java.util.stream.Collectors;

public class DuLieu {

    public static DuLieu instance;

    private ArrayList<YardModel> yardList;
    private ArrayList<BookingModel> bookingList;
    private ArrayList<TimeSlot> timeSlotLists;
    private ArrayList<ServicesModel> servicesList;
    private ArrayList<UserModel> usersList;
    private ArrayList<bean.RoleModel> rolesList;
    
    public DuLieu() {
        yardList = new ArrayList<>();
        bookingList = new ArrayList<>();
        timeSlotLists = new ArrayList<>();
        servicesList = new ArrayList<>();
        usersList = new ArrayList<>();
        rolesList = new ArrayList<>();
    }

    public static DuLieu getInstance() {
        if (instance == null) {
            instance = new DuLieu();
        }
        return instance;
    }

    public ArrayList<YardModel> getYards() {
        return yardList;
    }
public ArrayList<UserModel> getUsers(){
	return usersList;
}

public ArrayList<bean.RoleModel> getRoles(){
	return rolesList;
}
    public ArrayList<BookingModel> getBookings() {
        return bookingList;
    }

    public ArrayList<TimeSlot> getTimeSlot() {
        return timeSlotLists;
    }

    public ArrayList<ServicesModel> getServices() {
        return servicesList;
    }

    public void addYard(YardModel yard) {
        yardList.add(yard);
    }
public void registerUser(UserModel user) {
	usersList.add(user);
}

public void addRole(bean.RoleModel role) {
	rolesList.add(role);
}
    public void addBooking(BookingModel booking) {
        bookingList.add(booking);
    }

    public void addTimeSlot(TimeSlot timeSlot) {
        timeSlotLists.add(timeSlot);
    }

    public void addService(ServicesModel service) {
        servicesList.add(service);
    }

    public YardModel findYardById(String id) {
        return yardList.stream().filter(y -> y.getYardId().equals(id)).findFirst().orElse(null);
    }

    public ServicesModel findServiceById(String id) {
        return servicesList.stream().filter(s -> s.getServiceId().equals(id)).findFirst().orElse(null);
    }
    
    public bean.RoleModel findRoleById(String id) {
        return rolesList.stream().filter(r -> r.getRoleId().equals(id)).findFirst().orElse(null);
    }
    public String getYardNameById(String yardId) {
        for (YardModel y : getYards()) {
            if (y.getYardId().equals(yardId)) return y.getYardName();
        }
        return "Không rõ";
    }

    public double getYardPriceById(String yardId) {
        for (YardModel y : getYards()) {
            if (y.getYardId().equals(yardId)) return y.getYardPrice();
        }
        return 0;
    }

    public String getServiceNamesByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) return "Không có";
        return getServices().stream()
            .filter(s -> ids.contains(s.getServiceId()))
            .map(ServicesModel::getServiceName)
            .collect(Collectors.joining(", "));
    }

    // -----------------------------------
    // Lưu file
    // -----------------------------------
    public void saveYardToFile(String fileName) {
        try {
            File file = new File(fileName);

            Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, (com.google.gson.JsonSerializer<LocalDateTime>)
                    (src, typeOfSrc, context) -> new com.google.gson.JsonPrimitive(src.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))))
                .create();

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(gson.toJson(yardList)); 
            }

            System.out.println(" Đã lưu " + yardList.size() + " sân vào " + fileName);
        } catch (Exception e) {
            System.err.println(" Lỗi khi lưu file sân:");
            e.printStackTrace();
        }
    }
    public void saveUserToFile(String fileName) {
        try {
            File file = new File(fileName);
            JsonArray json = JsonUtils.toJsonArray(usersList);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(gson.toJson(json));
            }
            System.out.println(" Đã lưu " + usersList.size() + "  vào " + fileName);
        } catch (Exception e) {
            System.err.println(" Lỗi khi lưu file:");
            e.printStackTrace();
        }
    }


    public void saveRoleToFile(String fileName) {
        try {
            File file = new File(fileName);

            Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, (com.google.gson.JsonSerializer<LocalDateTime>)
                    (src, typeOfSrc, context) -> new com.google.gson.JsonPrimitive(
                        src.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                    ))
                .create();

            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(rolesList, writer);
            }

            System.out.println(" Đã lưu " + rolesList.size() + " vai trò vào file: " + fileName);
        } catch (Exception e) {
            System.err.println(" Lỗi khi lưu file vai trò: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void saveBookingToFile(String fileName) {
        try {
            File file = new File(fileName);
            JsonArray json = JsonUtils.toJsonArray(bookingList);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(gson.toJson(json));
            }
            System.out.println(" Đã lưu " + bookingList.size() + " đặt sân vào " + fileName);
        } catch (Exception e) {
            System.err.println(" Lỗi khi lưu file đặt sân:");
            e.printStackTrace();
        }
    }

    public void saveServiceToFile(String fileName) {
        try {
            File file = new File(fileName);
            JsonArray json = JsonUtils.toJsonArray(servicesList);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(gson.toJson(json));
            }
            System.out.println(" Đã lưu " + servicesList.size() + " dịch vụ vào " + fileName);
        } catch (Exception e) {
            System.err.println("Lỗi khi lưu file dịch vụ:");
            e.printStackTrace();
        }
    }

    public void saveTimeSlotToFile(String fileName) {
        try {
            File file = new File(fileName);
            JsonArray json = JsonUtils.toJsonArray(timeSlotLists);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(gson.toJson(json));
            }
            System.out.println(" Đã lưu " + timeSlotLists.size() + " time slots vào " + fileName);
        } catch (Exception e) {
            System.err.println(" Lỗi khi lưu file time slots:");
            e.printStackTrace();
        }
    }

    // -----------------------------------
    // Load file
    // -----------------------------------
    public void loadYardsFromFile(String fileName) {
        File file = new File(fileName);
        System.out.println(" Đang đọc file: " + file.getAbsolutePath());

        if (!file.exists()) {
            System.out.println(" File không tồn tại.");
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(reader);

            if (!element.isJsonArray()) {
                System.out.println(" File không chứa mảng JSON.");
                return;
            }

            JsonArray json = element.getAsJsonArray();

            Gson gson = new GsonBuilder()
            	    .registerTypeAdapter(LocalDateTime.class,
            	        (JsonDeserializer<LocalDateTime>) (json1, type, context) -> {
            	            String value = json1.getAsString();
            	            if (value == null || value.trim().isEmpty()) {
            	                return null; // Trả về null nếu rỗng
            	            }

            	            DateTimeFormatter[] formats = {
            	                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            	                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            	            };

            	            for (DateTimeFormatter f : formats) {
            	                try {
            	                    return LocalDateTime.parse(value, f);
            	                } catch (Exception ignored) {}
            	            }

            	            throw new RuntimeException("Không thể parse LocalDateTime: " + value);
            	        })
            	    .create();


            List<YardModel> loadedList = new ArrayList<>();
            for (JsonElement el : json) {
                YardModel yard = gson.fromJson(el, YardModel.class);
                loadedList.add(yard);
            }

            int addedCount = 0;
            for (YardModel newYard : loadedList) {
                boolean exists = yardList.stream()
                    .anyMatch(y -> y.getYardId().equals(newYard.getYardId()));
                if (!exists) {
                    yardList.add(newYard);
                    addedCount++;
                }
            }

            System.out.println(" Đã thêm " + addedCount + " sân từ file.");
        } catch (Exception e) {
            System.out.println(" Lỗi đọc file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadBookingsFromFile(String fileName) {
        File file = new File(fileName);
        System.out.println(" Đọc dữ liệu booking từ: " + file.getAbsolutePath());

        if (!file.exists() || file.length() == 0) {
            System.out.println("⚠ File không tồn tại hoặc rỗng.");
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            JsonParser parser = new JsonParser();
            JsonArray json = parser.parse(reader).getAsJsonArray();
            List<BookingModel> loadedList = JsonUtils.toBeans(json, BookingModel.class);

            for (BookingModel booking : loadedList) {
                if (bookingList.stream().noneMatch(b -> b.getBookingId().equals(booking.getBookingId()))) {
                    bookingList.add(booking);
                }
            }

            System.out.println(" Đã load " + bookingList.size() + " bookings.");
        } catch (Exception e) {
            System.out.println(" Không thể đọc file: " + e.getMessage());
        }
    }

    public void loadTimeSlotsFromFile(String fileName) {
        File file = new File(fileName);
        System.out.println(" Đọc dữ liệu time slot từ: " + file.getAbsolutePath());

        try (FileReader reader = new FileReader(file)) {
            JsonParser parser = new JsonParser();
            JsonArray json = parser.parse(reader).getAsJsonArray();
            List<TimeSlot> loadedList = JsonUtils.toBeans(json, TimeSlot.class);
            for (TimeSlot slot : loadedList) {
                if (timeSlotLists.stream().noneMatch(t -> t.getSlotId().equals(slot.getSlotId()))) {
                    timeSlotLists.add(slot);
                }
            }

            System.out.println(" Đã load " + timeSlotLists.size() + " time slots.");
        } catch (Exception e) {
            System.out.println(" Không thể đọc file: " + e.getMessage());
        }
    }

    public void loadServicesFromFile(String fileName) {
        File file = new File(fileName);
        System.out.println(" Đọc dữ liệu dịch vụ từ: " + file.getAbsolutePath());

        try (FileReader reader = new FileReader(file)) {
            JsonParser parser = new JsonParser();
            JsonArray json = parser.parse(reader).getAsJsonArray();
            List<ServicesModel> loadedList = JsonUtils.toBeans(json, ServicesModel.class);
            for (ServicesModel s : loadedList) {
                if (servicesList.stream().noneMatch(exist -> exist.getServiceId().equals(s.getServiceId()))) {
                    servicesList.add(s);
                }
            }

            System.out.println(" Đã load " + servicesList.size() + " dịch vụ.");
        } catch (Exception e) {
            System.out.println(" Không thể đọc file: " + e.getMessage());
        }
    }
    public void loadUsersFromFile(String fileName) {
        File file = new File(fileName);
        System.out.println(" Đọc dữ liệu dịch vụ từ: " + file.getAbsolutePath());

        try (FileReader reader = new FileReader(file)) {
            JsonParser parser = new JsonParser();
            JsonArray json = parser.parse(reader).getAsJsonArray();
            List<UserModel> loadedList = JsonUtils.toBeans(json, UserModel.class);
            for (UserModel user : loadedList) {
                if (usersList.stream().noneMatch(exist -> exist.getUserId().equals(user.getUserId()))) {
                    usersList.add(user);
                }
            }

            System.out.println(" Đã load " + usersList.size() + " người dùng.");
        } catch (Exception e) {
            System.out.println(" Không thể đọc file: " + e.getMessage());
        }
    }

    public void loadRolesFromFile(String fileName) {
        File file = new File(fileName);
        System.out.println(" Đọc dữ liệu vai trò từ: " + file.getAbsolutePath());

        if (!file.exists()) {
            System.out.println("File " + fileName + " không tồn tại, tạo file mới với vai trò mặc định");
            createDefaultRoles();
            saveRoleToFile(fileName);
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            JsonParser parser = new JsonParser();
            JsonArray json = parser.parse(reader).getAsJsonArray();
            List<bean.RoleModel> loadedList = JsonUtils.toBeans(json, bean.RoleModel.class);
            for (bean.RoleModel role : loadedList) {
                if (rolesList.stream().noneMatch(exist -> exist.getRoleId().equals(role.getRoleId()))) {
                    rolesList.add(role);
                }
            }

            System.out.println(" Đã load " + rolesList.size() + " vai trò.");
        } catch (Exception e) {
            System.out.println(" Không thể đọc file vai trò: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createDefaultRoles() {
        // Admin role with all permissions
        bean.RoleModel adminRole = new bean.RoleModel();
        adminRole.setRoleId("admin-role");
        adminRole.setRoleName("Administrator");
        adminRole.setDescription("Quản trị viên hệ thống với tất cả quyền hạn");
        adminRole.setPermissions(Arrays.asList(
            "yard.view", "yard.create", "yard.edit", "yard.delete",
            "booking.view", "booking.create", "booking.edit", "booking.delete", "booking.approve",
            "service.view", "service.create", "service.edit", "service.delete",
            "user.view", "user.create", "user.edit", "user.delete",
            "role.view", "role.create", "role.edit", "role.delete",
            "stats.view", "stats.export",
            "system.settings", "system.backup", "system.restore"
        ));
        rolesList.add(adminRole);

        // Manager role with limited permissions
        bean.RoleModel managerRole = new bean.RoleModel();
        managerRole.setRoleId("manager-role");
        managerRole.setRoleName("Manager");
        managerRole.setDescription("Quản lý với quyền hạn quản lý sân và đặt sân");
        managerRole.setPermissions(Arrays.asList(
            "yard.view", "yard.create", "yard.edit",
            "booking.view", "booking.create", "booking.edit", "booking.approve",
            "service.view", "service.create", "service.edit",
            "user.view",
            "stats.view"
        ));
        rolesList.add(managerRole);

        // Staff role with basic permissions
        bean.RoleModel staffRole = new bean.RoleModel();
        staffRole.setRoleId("staff-role");
        staffRole.setRoleName("Nhân viên");
        staffRole.setDescription("Nhân viên với quyền hạn cơ bản");
        staffRole.setPermissions(Arrays.asList(
            "yard.view",
            "booking.view", "booking.create",
            "service.view"
        ));
        rolesList.add(staffRole);
    }
}
