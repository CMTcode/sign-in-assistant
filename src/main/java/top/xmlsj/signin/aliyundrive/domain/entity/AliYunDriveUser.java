package top.xmlsj.signin.aliyundrive.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * Created on 2023/3/25.
 *
 * @author Yang YaoWei
 */
@Data
@TableName("aliyundrive_user")
public class AliYunDriveUser implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String name;
    private String refreshToken;
    private String token;
    private Integer isAuthenticated = 0;
}
