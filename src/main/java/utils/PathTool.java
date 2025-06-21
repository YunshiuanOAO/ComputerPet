package utils;

public class PathTool {
    private static final String basePath = "/";
    public static String patchPicturePath(String imagePath) {
        System.out.println(basePath + imagePath);
        return basePath + imagePath;
    }
}
