package top.xmlsj.signin.wangyi.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.xmlsj.signin.config.ConfigPath;
import top.xmlsj.signin.util.YmlUtil;
import top.xmlsj.signin.wangyi.domain.entity.WangYiConfig;
import top.xmlsj.signin.wangyi.service.WangYiCoreService;

/**
 * Created on 2023/3/18.
 *
 * @author Yang YaoWei
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WangYiCoreServiceImpl implements WangYiCoreService {


    @Override
    public WangYiConfig readWangYiConfig() {
        return YmlUtil.readConfig(ConfigPath.WANGYI_CONFIG_PATH, WangYiConfig.class);
    }


}
