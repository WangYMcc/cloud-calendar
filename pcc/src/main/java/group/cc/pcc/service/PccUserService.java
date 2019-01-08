package group.cc.pcc.service;
import group.cc.pcc.model.PccUser;
import group.cc.core.Service;

import java.util.List;


/**
 * @author yuanli
 * @date 2018/11/19
 */
public interface PccUserService extends Service<PccUser> {
    PccUser get(PccUser pccUser);

    List<PccUser> friends(Integer pccUserId);
}
