package top.xmlsj.signin.aliyundrive.domain.pojo.aliyunsigninfo;


import java.io.IOException;

/**
 * Created on 2023/3/25.
 *
 * @author Yang YaoWei
 */
public enum Themes {
    AQUAMARINE, BLUE, ORANGE;

    public static Themes forValue(String value) throws IOException {
        if (value.equals("aquamarine")) return AQUAMARINE;
        if (value.equals("blue")) return BLUE;
        if (value.equals("orange")) return ORANGE;
        throw new IOException("Cannot deserialize Themes");
    }

    public String toValue() {
        switch (this) {
            case AQUAMARINE:
                return "aquamarine";
            case BLUE:
                return "blue";
            case ORANGE:
                return "orange";
        }
        return null;
    }
}
