package top.xmlsj.signin.bilbil.utils;

import java.util.Collections;

/**
 * @author ForkManTou
 * @create 2020/10/11 20:49
 */
public class HelpUtil {

    public static String userNameEncode(String userName) {
        int s1 = userName.length() / 2, s2 = (s1 + 1) / 2;
        return userName.substring(0, s2) + String.join("", Collections.nCopies(s1, "*")) +
                userName.substring(s1 + s2);
    }

}
