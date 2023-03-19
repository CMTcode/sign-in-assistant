package top.xmlsj.signin.wangyi.util;


import java.nio.charset.StandardCharsets;

/**
 * Created on 2022/12/17.
 *
 * @author Yang YaoWei
 */

public class URIEncoder {
    public static final String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_.!~*'()";

    /**
     * Description:
     *
     * @param str
     * @return
     * @see
     */
    public static String encodeURI(String str) {
        String isoStr = new String(str.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        char[] chars = isoStr.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (char aChar : chars) {
            if ((aChar <= 'z' && aChar >= 'a') || (aChar <= 'Z' && aChar >= 'A')
                    || aChar == '-' || aChar == '_' || aChar == '.' || aChar == '!'
                    || aChar == '~' || aChar == '*' || aChar == '\'' || aChar == '('
                    || aChar == ')' || aChar == ';' || aChar == '/' || aChar == '?'
                    || aChar == ':' || aChar == '@' || aChar == '&' || aChar == '='
                    || aChar == '+' || aChar == '$' || aChar == ',' || aChar == '#'
                    || (aChar <= '9' && aChar >= '0')) {
                sb.append(aChar);
            } else {
                sb.append("%");
                sb.append(Integer.toHexString(aChar));
            }
        }
        return sb.toString();
    }

    /**
     * Description:
     *
     * @param input
     * @return
     * @see
     */
    public static String encodeURIComponent(String input) {
        if (null == input || "".equals(input.trim())) {
            return input;
        }

        int l = input.length();
        StringBuilder o = new StringBuilder(l * 3);
        for (int i = 0; i < l; i++) {
            String e = input.substring(i, i + 1);
            if (!ALLOWED_CHARS.contains(e)) {
                byte[] b = e.getBytes(StandardCharsets.UTF_8);
                o.append(getHex(b));
                continue;
            }
            o.append(e);
        }
        return o.toString();
    }

    private static String getHex(byte[] buf) {
        StringBuilder o = new StringBuilder(buf.length * 3);
        for (byte b : buf) {
            int n = (int) b & 0xff;
            o.append("%");
            if (n < 0x10) {
                o.append("0");
            }
            o.append(Long.toString(n, 16).toUpperCase());
        }
        return o.toString();
    }
}
