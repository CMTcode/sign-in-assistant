package top.xmlsj.signin.script.aliyundrive.domain.entity;

import lombok.Data;

import java.util.List;

/**
 * Created on 2023/3/25.
 *
 * @author Yang YaoWei
 */
@Data
public class AliYunDriveConfig {
    private boolean enabled = false;
    private List<AliYunDriveUser> accounts;
}
