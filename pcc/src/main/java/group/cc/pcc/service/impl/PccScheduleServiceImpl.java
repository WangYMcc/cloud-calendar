package group.cc.pcc.service.impl;

import com.yl.common.util.StringUtil;
import group.cc.pcc.dao.*;
import group.cc.pcc.model.PccSchedule;
import group.cc.pcc.model.PccText;
import group.cc.pcc.model.builder.*;
import group.cc.pcc.model.dto.PccScheduleComplete;
import group.cc.pcc.model.dto.PccScheduleDto;
import group.cc.pcc.service.PccScheduleService;
import group.cc.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author yuanli
 * @date 2018/12/23
 */
@Service
@Transactional
public class PccScheduleServiceImpl extends AbstractService<PccSchedule> implements PccScheduleService {

    @Resource
    private PccScheduleRemindMapper pccScheduleRemindMapper;

    @Resource
    private PccTextMapper pccTextMapper;

    @Resource
    private PccScheduleTextMapper pccScheduleTextMapper;

    @Resource
    private PccScheduleFileMapper pccScheduleFileMapper;

    @Resource
    private PccScheduleUserMapper pccScheduleUserMapper;

    @Resource
    private PccScheduleMapper pccScheduleMapper;

    @Resource
    private PccScheduleAdditionalTypeMapper pccScheduleAdditionalTypeMapper;

    @Override
    public List<Map<String, Object>> dayCount(Date startDate, Date endDate, Integer pccUserId) {

        /*Example example = new Example(PccSchedule.class);
        Example.Criteria criteria = example.createCriteria();*/

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

        return pccScheduleMapper.dayCount(simpleDateFormat.format(startDate), simpleDateFormat.format(endDate), pccUserId);
    }

    @Override
    @Transactional
    public void add(PccScheduleDto pccScheduleDto) {

        // 创建时间
        pccScheduleDto.getPccSchedule().setCreateTime(new Date());

        // 插入 pccSchedule
        pccScheduleMapper.insert(pccScheduleDto.getPccSchedule());

        Integer insertPccScheduleId = pccScheduleDto.getPccSchedule().getId();

        // 批量插入中间表 pcc_schedule_remind
        pccScheduleRemindMapper.insertList(PccScheduleRemindBuilder
                .build(pccScheduleDto.getRemindServices(), insertPccScheduleId));

        // 批量插入中间表 pcc_schedule_user
        pccScheduleUserMapper.insertList(PccScheduleUserBuilder
                .build(pccScheduleDto.getScheduleReceivers(), insertPccScheduleId));

        // 批量插入中间表 pcc_schedule_additional_type
        pccScheduleAdditionalTypeMapper.insertList(PccScheduleAdditionalTypeBuilder
                .build(pccScheduleDto.getAdditionalInfoTypes(), insertPccScheduleId));

    }

    @Override
    public List<Map<String, Object>> relationList(Integer pccUserId) {
        return pccScheduleMapper.relationList(pccUserId);
    }

    @Override
    public List<Map<String, Object>> createList(Integer pccUserId) {
        return pccScheduleMapper.createList(pccUserId);
    }

    @Override
    public List<Map<String, Object>> untreated(Integer pccUserId) {
        return pccScheduleMapper.untreatedList(pccUserId);
    }

    @Override
    public List<Map<String, Object>> treated(Integer pccUserId) {
        return pccScheduleMapper.treatedList(pccUserId);
    }

    @Override
    @Transactional
    public void complete(PccScheduleComplete pccScheduleComplete) {
        Integer[] pccFileIds = pccScheduleComplete.getFileIds();

        if (pccFileIds != null && pccFileIds.length != 0) {
            // 批量存储 pccScheduleFile
            pccScheduleFileMapper.insertList(PccScheduleFileBuilder
                    .build(pccScheduleComplete.getFileIds(),
                            pccScheduleComplete.getPccScheduleId(),
                            pccScheduleComplete.getPccUserId()));
        }

        String text = pccScheduleComplete.getText();

        if(text != null && !text.equals(StringUtil.EMPTY)) {
            // 存储 pccText
            PccText pccText = PccTextBuilder.build(pccScheduleComplete.getText());
            pccTextMapper.insert(pccText);
            // 存储 pccScheduleText
            pccScheduleTextMapper.insert(PccScheduleTextBuilder.build(pccScheduleComplete.getPccScheduleId(),
                    pccScheduleComplete.getPccUserId(), pccText.getId()));
        }
        // 更新 pcc_schedule_user 表为完成任务
        pccScheduleUserMapper.complete(pccScheduleComplete.getPccUserId(),
                pccScheduleComplete.getPccScheduleId(), new Date());
    }

    @Override
    public List<Map<String, Object>> additionalInfoList(Integer pccScheduleId) {
        return pccScheduleMapper.additionalInfoList(pccScheduleId);
    }

    @Override
    public Map<String, Object> historyCount(Integer pccUserId) {
        return pccScheduleMapper.historyCount(pccUserId);
    }

    @Override
    public List<Map<String, Object>> historyAssignCount(Integer pccUserId) {
        return pccScheduleMapper.historyAssignCount(pccUserId);
    }

    @Override
    public List<Map<String, Object>> historyAssignedCount(Integer pccUserId) {
        return pccScheduleMapper.historyAssignedCount(pccUserId);
    }

    public List<Map<String, Object>> counts(Integer pccUserId, Date startDate, Date endDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        // 获取数据
        List<Map<String, Object>> dataList = pccScheduleMapper.counts(pccUserId, simpleDateFormat.format(startDate),
                simpleDateFormat.format(endDate));

        List<List<Map<String, Object>>> re = parseToList(dataList);
        // 分析处理数据
        if(re == null) {
            return new ArrayList<>(0);
        }

        return parse(re, startDate, endDate);
    }

    private List<Map<String, Object>> parse(List<List<Map<String, Object>>> re, Date startDate, Date endDate) {
        if(re.size() != 3) {
            return new ArrayList<>();
        }
        // 发布统计数据
        Map<String, Map<String, Object>> publish = listToMap(re.get(0));
        // 接收统计数据
        Map<String, Map<String, Object>> accept = listToMap(re.get(1));
        // 完成统计数据
        Map<String, Map<String, Object>> complete = listToMap(re.get(2));

        // 创建存储容器
        int size = dayInterval(startDate, endDate) + 1;
        List<Map<String, Object>> result = new ArrayList<>(size);

        for(int i = 0; i < size; i++) {
            String time = dayPlus(startDate, i);
            Map<String, Object> item = new HashMap<>();
            item.put("time", time);
            Map<String, Object> publishItem = publish.get(time);
            Map<String, Object> acceptItem = accept.get(time);
            Map<String, Object> completeItem = complete.get(time);

            item.put("publish", publishItem == null ? 0 : publishItem.get("count"));
            item.put("accept", acceptItem == null ? 0 : acceptItem.get("count"));
            item.put("complete", completeItem == null ? 0 : completeItem.get("count"));

            result.add(item);
        }

        return result;
    }

    private int dayInterval(Date startDate, Date endDate) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(startDate);
        int startDay = ca.get(Calendar.DAY_OF_YEAR);
        int startYear = ca.get(Calendar.YEAR);
        ca.setTime(endDate);
        int endDay = ca.get(Calendar.DAY_OF_YEAR);
        int endYear = ca.get(Calendar.YEAR);

        if(startYear != endYear) {
            int timeDistance = 0 ;
            for(int i = startYear ; i < endYear ; i++) {
                if(i%4==0 && i%100!=0 || i%400==0) {
                    timeDistance += 366;
                }
                else {
                    timeDistance += 365;
                }
            }
            return timeDistance + (endDay - startDay) ;
        }

        return endDay - startDay;
    }

    private String dayPlus(Date date, int plus) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, plus);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        long time = date.getTime() + plus * (24 * 60 * 60 * 1000);
        return format.format(calendar.getTime());
    }

    private Map<String, Map<String, Object>> listToMap(List<Map<String, Object>> list) {
        Map<String, Map<String, Object>> map = new HashMap<>(list.size());
        if(list.size() == 0) {
            return map;
        }

        list.stream().forEach((item) -> {
            String time = (String) item.get("time");
            map.put(time, item);
        });

        return map;
    }

    private List<List<Map<String, Object>>> parseToList(List<Map<String, Object>> dataList) {
        if(dataList == null || dataList.size() == 0) {
            return null;
        }
        // 初始化数据容器，存储三类数据：发布数目，接受任务，完成数目
        List<List<Map<String, Object>>> re = new ArrayList<>(3);

        List<Map<String, Object>> items = new ArrayList<>();
        for(int i = 0; i < dataList.size(); i++) {
            Long count = (Long) dataList.get(i).get("count");

            if(count == 0) {
                re.add(items);
                items = new ArrayList<>();
            }
            else {
                items.add(dataList.get(i));
            }
        }
        re.add(items);
        return re;
    }
}