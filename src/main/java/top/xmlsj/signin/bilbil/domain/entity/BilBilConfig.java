package top.xmlsj.signin.bilbil.domain.entity;

import lombok.Data;

import java.util.List;

/**
 * Created on 2023/3/7.
 *
 * @author Yang YaoWei
 */
@Data
public class BilBilConfig {
    private Boolean enabled;
    private List<Account> accounts;
}
