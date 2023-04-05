package top.xmlsj.signin.model.wangyi.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Arrays;
import java.util.Date;

/**
 * Created on 2023/3/16.
 *
 * @author Yang YaoWei
 */
@Data
@TableName("music_user")
public class MusicUser {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String name;
    private String token;
    private String cookie;
    private Integer level;
    private Integer isAuthenticated = 0;
    /**
     *
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     *
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    public void setCookie(String cookie) {
        this.cookie = cookie;
        if (cookie.contains("__csrf=")) {
            this.token = ((String) Arrays.stream(this.cookie.trim().split(";")).filter(a -> a.contains("__csrf=")).toArray()[0]).trim().split("=")[1];
        }
    }


}
