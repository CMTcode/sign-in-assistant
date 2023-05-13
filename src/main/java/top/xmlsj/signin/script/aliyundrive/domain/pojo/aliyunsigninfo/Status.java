package top.xmlsj.signin.script.aliyundrive.domain.pojo.aliyunsigninfo;

/**
 * Created on 2023/3/25.
 *
 * @author Yang YaoWei
 */

import java.io.IOException;

public enum Status {
    MISS, NORMAL;

    public static Status forValue(String value) throws IOException {
        if (value.equals("miss")) {
            return MISS;
        }
        if (value.equals("normal")) {
            return NORMAL;
        }
        throw new IOException("Cannot deserialize Status");
    }

    public String toValue() {
        switch (this) {
            case MISS:
                return "miss";
            case NORMAL:
                return "normal";
        }
        return null;
    }
}
