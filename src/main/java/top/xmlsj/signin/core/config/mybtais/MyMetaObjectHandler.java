package top.xmlsj.signin.core.config.mybtais;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created on 2023/4/4.
 *
 * @author Yang YaoWei
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", Date::new, Date.class);
        this.strictInsertFill(metaObject, "createTime", Date::new, Date.class);

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", Date::new, Date.class);
    }


}
