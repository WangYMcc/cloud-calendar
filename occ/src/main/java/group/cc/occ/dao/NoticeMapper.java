package group.cc.occ.dao;

import group.cc.core.Mapper;
import group.cc.occ.model.Notice;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface NoticeMapper extends Mapper<Notice> {
    @Select("SELECT * FROM NOTICE WHERE ${key} like #{value} AND USERID = #{userId} ORDER BY STARTTIME DESC")
    public List<Notice> listByKey(@Param("key")String key, @Param("value")String value, @Param("userId")Integer userId);

    @Delete("DELETE FROM NOTICE WHERE ID in(${noticesId})")
    public void deleteBatch(@Param("noticesId")String noticesId);

    @Update("UPDATE NOTICE SET CHECKTIME = NOW() , STATE = '已读'  WHERE ID IN(${noticesId})")
    public void seeBatch(@Param("noticesId")String noticesId);

    @Select("SELECT * FROM NOTICE WHERE STATE = '未读' AND USERID = #{userId} ORDER BY STARTTIME DESC")
    public List<Notice> getAllUnreadNotice(@Param("userId")Integer userId);

    //chat

    @Select("SELECT COUNT(*) FROM CHAT WHERE RECEIVEUSERID = #{userId} AND SENDUSERID IN(SELECT U.ID FROM USER U LEFT JOIN USER_ROLE UR " +
            " ON UR.USERID = U.ID LEFT JOIN ROLE R ON R.ID = UR.ROLEID WHERE R.ORGID = #{orgId}) AND HADSEEN = '否' AND ORGID = #{orgId}")
    public Integer getAllUnreadMessage(@Param("userId")Integer userId, @Param("orgId")Integer orgId);

    @Select("SELECT COUNT(*) FROM CHAT WHERE RECEIVEUSERID = #{reveiceUserId} AND SENDUSERID = #{sendUserId} AND HADSEEN = '否' AND ORGID = #{orgId}")
    public Integer getUnreadMessageByUserId(@Param("reveiceUserId")Integer reveiceUserId, @Param("sendUserId")Integer sendUserId, @Param("orgId")Integer orgId);

    @Update("UPDATE CHAT SET HADSEEN = '是' WHERE SENDUSERID = #{sendUserId} AND RECEIVEUSERID = #{reveiceUserId} AND ORGID = #{orgId}")
    public void seeAllChat(@Param("sendUserId")Integer sendUserId, @Param("reveiceUserId")Integer reveiceUserId, @Param("orgId")Integer orgId);
}