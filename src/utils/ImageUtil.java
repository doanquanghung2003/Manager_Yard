package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class ImageUtil {
    public static String chooseImageAndSave(ImageView imageView, String saveFolder) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn hình ảnh");

        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Hình ảnh", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
              
                File dir = new File(saveFolder);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

              
                String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                File destFile = new File(dir, fileName);

                // Sao chép file
                Files.copy(selectedFile.toPath(), destFile.toPath());

                // Hiển thị ảnh
                Image image = new Image(destFile.toURI().toString());
                imageView.setImage(image);

                System.out.println("Đã lưu ảnh vào: " + destFile.getAbsolutePath());
                return destFile.getAbsolutePath(); 

            } catch (Exception e) {
                System.err.println(" Lỗi khi lưu ảnh.");
                e.printStackTrace();
            }
        } else {
            System.out.println("Không chọn ảnh nào.");
        }

        return null;
    }
}
