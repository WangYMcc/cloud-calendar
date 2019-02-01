package group.cc.df.service;
import group.cc.df.model.DfDynamicForm;
import group.cc.core.Service;

import java.util.Map;


/**
 * @author gxd
 * @date 2019/01/10
 */
public interface DfDynamicFormService extends Service<DfDynamicForm> {
    public void saveDynamicForm(Map<String, Object> dfMap);
}