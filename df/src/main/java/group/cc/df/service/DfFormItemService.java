package group.cc.df.service;
import group.cc.df.model.DfFormItem;
import group.cc.core.Service;

import java.util.List;


/**
 * @author gxd
 * @date 2019/01/10
 */
public interface DfFormItemService extends Service<DfFormItem> {
    List<DfFormItem> findDfFormItemsByFieldId(Integer fieldId);
}
