package utils;

public class PathTool {
    private static final String basePath = "/";
    public static String patchPicturePath(String imagePath) {
        return basePath + imagePath;
    }
}
