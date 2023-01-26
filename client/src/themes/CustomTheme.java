package themes;

import com.formdev.flatlaf.FlatLightLaf;

public class CustomTheme extends FlatLightLaf {
    public static boolean setup() {
        return setup( new CustomTheme());
    }
}
