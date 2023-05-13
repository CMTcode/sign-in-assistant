package top.xmlsj.signin.script.wangyi.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

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


}
