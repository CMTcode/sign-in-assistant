package top.xmlsj.signin.model.wangyi.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.xmlsj.signin.model.wangyi.domain.entity.MusicUser;
import top.xmlsj.signin.model.wangyi.mapper.MusicUserMapper;
import top.xmlsj.signin.model.wangyi.service.MusicUserService;


/**
 * @author CMT
 * @description 针对表【music_user】的数据库操作Service实现
 * @createDate 2022-11-04 20:29:11
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MusicUserServiceImpl extends ServiceImpl<MusicUserMapper, MusicUser> implements MusicUserService {


    @Override
    public void truncateTable() {
        getBaseMapper().truncateTable();
    }
}




