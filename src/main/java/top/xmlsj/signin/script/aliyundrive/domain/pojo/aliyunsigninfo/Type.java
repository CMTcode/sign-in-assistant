package top.xmlsj.signin.script.aliyundrive.domain.pojo.aliyunsigninfo;

/**
 * Created on 2023/3/25.
 *
 * @author Yang YaoWei
 */

import java.io.IOException;

public enum Type {
    LOGO, POSTPONE, SIGN_IN, SVIP8_T, SVIP_VIDEO;

    public static Type forValue(String value) throws IOException {
        if (value.equals("logo")) return LOGO;
        if (value.equals("postpone")) return POSTPONE;
        if (value.equals("signIn")) return SIGN_IN;
        if (value.equals("svip8t")) return SVIP8_T;
        if (value.equals("svipVideo")) return SVIP_VIDEO;
        throw new IOException("Cannot deserialize Type");
    }

    public String toValue() {
        switch (this) {
            case LOGO:
                return "logo";
            case POSTPONE:
                return "postpone";
            case SIGN_IN:
                return "signIn";
            case SVIP8_T:
                return "svip8t";
            case SVIP_VIDEO:
                return "svipVideo";
        }
        return null;
    }
}
