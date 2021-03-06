package group.cc.df.dao;

import group.cc.core.Mapper;
import group.cc.df.model.DfSharedDynamicForm;

import java.util.List;
import java.util.Map;

public interface DfSharedDynamicFormMapper extends Mapper<DfSharedDynamicForm> {

    /**
     * 根据条件查询分享的模板表单
     * @param conditionMap
     * @return
     */
    List<DfSharedDynamicForm> findSharedDynamicFormByCondition(Map<String, Object> conditionMap);

    /**
     * 根据条件查询分享的模板表单的数量
     * @param conditionMap
     * @return
     */
    int findSharedDynamicFormCountByCondition(Map<String, Object> conditionMap);

    /**
     * 更新分享的模板表单信息
     * @param sharedDynamicForm
     * @return
     */
    int updateSharedDynamicForm(DfSharedDynamicForm sharedDynamicForm);

    /**
     * 根据表单名称获取分享的表单信息
     * 因为一条表单只会有一条分享信息,所以直接去List中的第一个元素即可
     * @param formId
     * @return
     */
    List<DfSharedDynamicForm> findSharedDynamicFormByFormId(Integer formId);

    /**
     * 根据表单Id删除分享表单信息
     * @param formId
     * @return
     */
    int deleteSharedDynamicFormByFormId(Integer formId);
}