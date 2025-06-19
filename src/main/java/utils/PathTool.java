package utils;

public class PathTool {
    private static final String basePath = "src/main/resources/";
    public static String patchPicturePath(String imagePath) {
        return basePath + imagePath;
    }
}
