package utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

public class ImageUtil {
    /**
     * Chọn ảnh và lưu vào thư mục chỉ định, đồng thời set preview cho ImageView
     */
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
                Files.copy(selectedFile.toPath(), destFile.toPath());
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

    /**
     * Tạo ImageView preview
     */
    public static ImageView createImagePreview() {
        ImageView imgPreview = new ImageView();
        imgPreview.setFitWidth(80);
        imgPreview.setFitHeight(80);
        return imgPreview;
    }

    /**
     * Tạo nút chọn ảnh và lưu, đồng thời set path vào TextField
     */
    public static Button createChooseImageButton(ImageView imgPreview, TextField thumbnailField) {
        Button btnChooseImg = new Button("Chọn ảnh");
        btnChooseImg.setOnAction(ev -> {
            String savedPath = chooseImageAndSave(imgPreview, "src/images");
            if (savedPath != null) {
                thumbnailField.setText(savedPath);
            }
        });
        return btnChooseImg;
    }

    /**
     * Tạo preview nhiều ảnh
     */
    public static HBox createImagesPreview(List<String> yardImages) {
        HBox imagesPreview = new HBox(5);
        for (String imgPath : yardImages) {
            try {
                ImageView iv = new ImageView(new Image(new File(imgPath).toURI().toString()));
                iv.setFitWidth(40); 
                iv.setFitHeight(30);
                imagesPreview.getChildren().add(iv);
            } catch (Exception ex) {
                System.err.println("Error loading image: " + imgPath + " - " + ex.getMessage());
            }
        }
        return imagesPreview;
    }

    /**
     * Tạo nút thêm nhiều ảnh
     */
    public static Button createAddImagesButton(List<String> yardImages, HBox imagesPreview) {
        Button btnAddImages = new Button("Thêm ảnh con");
        btnAddImages.setOnAction(ev -> addImagesToPreview(yardImages, imagesPreview));
        return btnAddImages;
    }

    /**
     * Thêm nhiều ảnh vào preview
     */
    public static void addImagesToPreview(List<String> yardImages, HBox imagesPreview) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh phụ");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Hình ảnh", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        List<File> files = fileChooser.showOpenMultipleDialog(null);
        if (files != null) {
            for (File file : files) {
                String savedPath = saveImageFile(file);
                if (savedPath != null) {
                    yardImages.add(savedPath);
                    addImageToPreview(savedPath, imagesPreview);
                }
            }
        }
    }

    /**
     * Lưu file ảnh vào src/images
     */
    public static String saveImageFile(File file) {
        try {
            Path dest = Paths.get("src/images", System.currentTimeMillis() + "_" + file.getName());
            Files.copy(file.toPath(), dest);
            return dest.toString();
        } catch (Exception ex) {
            System.err.println("Error saving image file: " + ex.getMessage());
            return null;
        }
    }

    /**
     * Thêm một ảnh vào preview
     */
    public static void addImageToPreview(String savedPath, HBox imagesPreview) {
        try {
            ImageView iv = new ImageView(new Image(new File(savedPath).toURI().toString()));
            iv.setFitWidth(40); 
            iv.setFitHeight(30);
            imagesPreview.getChildren().add(iv);
        } catch (Exception ex) {
            System.err.println("Error adding image to preview: " + savedPath + " - " + ex.getMessage());
        }
    }
}
