package top.xmlsj.signin.model.aliyundrive.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName aliyundrive_user
 */
@TableName(value = "aliyundrive_user")
@Data
public class AliYunDriveUser implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    /**
     *
     */
    private String name;
    /**
     *
     */
    private String refreshToken;
    /**
     *
     */
    private Integer isAuthenticated = 0;
    /**
     *
     */
    private String token;

    /**
     *
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    /**
     *
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;
}
