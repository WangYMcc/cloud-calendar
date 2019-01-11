package group.cc.pcc.service.impl;

import group.cc.pcc.dao.PccUserMapper;
import group.cc.pcc.model.PccUser;
import group.cc.pcc.service.PccUserService;
import group.cc.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


/**
 * @author yuanli
 * @date 2018/11/19
 */
@Service
@Transactional
public class PccUserServiceImpl extends AbstractService<PccUser> implements PccUserService {
    @Resource
    private PccUserMapper pccUserMapper;

    public PccUser get(PccUser pccUser) {
        return pccUserMapper.selectOne(pccUser);
    }

    @Override
    public List<PccUser> friends(Integer pccUserId) {
        return pccUserMapper.friends(pccUserId);
    }

    @Override
    public List<Map<String, Object>> friendsDetail(Integer pccUserId) {
        return pccUserMapper.friendsDetail(pccUserId);
    }
}
