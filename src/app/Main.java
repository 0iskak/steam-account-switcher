package app;

import com.formdev.flatlaf.FlatLightLaf;
import lombok.SneakyThrows;

import java.io.File;

public class Main {
    private static final String REG_AUTO_LOGIN =
            "reg add HKEY_CURRENT_USER\\Software\\Valve\\Steam /v AutoLoginUser /d {username} /f";
    private static final String REG_REMEMBER_PWD =
            "reg add HKEY_CURRENT_USER\\Software\\Valve\\Steam /v RememberPassword /t REG_DWORD /d 1 /f";
    private static final String STEAM_KILL =
            "taskkill /f /im steam.exe";
    private static final String STEAM_DIR =
            "C:\\Program Files (x86)\\Steam\\";

    private static final Runtime r = Runtime.getRuntime();

    public static void main(String[] args) {
        FlatLightLaf.setup();

        User.parse();
        GUI.setup();

        GUI.getFrame().setVisible(true);
    }

    @SneakyThrows
    public static void switchTo(User user) {
        r.exec(REG_AUTO_LOGIN.replace("{username}", user.getUsername())).waitFor();
        r.exec(REG_REMEMBER_PWD).waitFor();
        r.exec(STEAM_KILL).waitFor();
        r.exec(STEAM_DIR+"steam.exe", null, new File(STEAM_DIR));
    }
}
