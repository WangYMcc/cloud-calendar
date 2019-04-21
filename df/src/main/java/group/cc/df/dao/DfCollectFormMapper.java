package group.cc.df.dao;

import group.cc.core.Mapper;
import group.cc.df.model.DfCollectForm;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DfCollectFormMapper extends Mapper<DfCollectForm> {

    /**
     * 保存收集表单
     * @param collectForm
     * @return
     */
    int saveCollectForm(DfCollectForm collectForm);

    /**
     * 根据用户Id及表单Id查询对应的收集记录
     * 因为一张表单每个人只能填写一次,所以查询出来的List要么为空,要么就只有一条数据
     * @param employeeId
     * @return
     */
    List<DfCollectForm> findCollectFormByEmployeeIdAndFormId(@Param("employeeId") Integer employeeId,
                                                             @Param("formId") Integer formId);

    /**
     * 查询符合条件的收集表单信息
     * @param conditionMap
     * @return
     */
    List<DfCollectForm> findCollectFormByCondition(Map<String, Object> conditionMap);

    /**
     * 查询符合条件的收集表单的数量
     * @param conditionMap
     * @return
     */
    int findCollectFormCountByCondition(Map<String, Object> conditionMap);
}