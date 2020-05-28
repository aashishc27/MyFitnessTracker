package helper;


import android.content.Context;


import java.io.File;
import java.util.Arrays;
import java.util.List;

public class DeviceUtils {

    public Boolean isDeviceRooted(Context context) {
        if(isrooted1())return true;
        else if (checkRootMethodOne()) return checkRootMethodOne();
        else return checkRootMethodTwo();
    }

    private boolean isrooted1() {

        File file = new File("/system/app/Superuser.apk");
        return file.exists();
    }

    private Boolean checkRootMethodOne() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private Boolean checkRootMethodTwo() {
        List<String> paths = Arrays.asList( "/system/app/Superuser.apk",
                "/sbin/su",
                "/system/bin/su",
                "/system/xbin/su",
                "/data/local/xbin/su",
                "/data/local/bin/su",
                "/system/sd/xbin/su",
                "/system/bin/failsafe/su",
                "/data/local/su",
                "/su/bin/su");
        for (int i = 0;i<paths.size();i++) {
            if (new File(paths.get(i)).exists()) return true;
        }
        return false;
    }
}