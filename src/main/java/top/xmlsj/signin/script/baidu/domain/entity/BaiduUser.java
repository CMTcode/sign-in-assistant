package top.xmlsj.signin.script.baidu.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName baidu_user
 */
@TableName(value = "baidu_user")
@Data
public class BaiduUser implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;
    /**
     *
     */
    @TableField(value = "name")
    private String name;
    /**
     *
     */
    @TableField(value = "cookie")
    private String cookie;
    /**
     *
     */
    @TableField(value = "bduss")
    private String bduss;
    /**
     *
     */
    @TableField(value = "is_authenticated")
    private Integer isAuthenticated;
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
