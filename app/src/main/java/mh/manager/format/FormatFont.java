package mh.manager.format;

import java.io.UnsupportedEncodingException;

/**
 * Created by man.ha on 7/27/2017.
 */

public class FormatFont {
    public String formatFont(String strFont){
        try {
            return new String(strFont.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return strFont;
    }
}
