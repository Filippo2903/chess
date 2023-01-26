package themes;

import com.formdev.flatlaf.FlatDarkLaf;

public class CustomTheme extends FlatDarkLaf {
    public static boolean setup() {
        return setup( new CustomTheme() );
    }
}
