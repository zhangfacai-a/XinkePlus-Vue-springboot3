package com.ruoyi.dingtalk.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 钉钉同步 Mapper。
 *
 * 这里直接操作若依原生表 sys_dept、sys_user。
 * 为了不引入过多实体类，返回使用 Map，写入参数也拆成简单字段。
 */
@Mapper
public interface DingTalkSyncMapper {

    Map<String, Object> selectDeptByDingTalkDeptId(@Param("dingtalkDeptId") Long dingtalkDeptId);

    Map<String, Object> selectDeptByDeptId(@Param("deptId") Long deptId);

    Long selectDeptIdByDingTalkDeptId(@Param("dingtalkDeptId") Long dingtalkDeptId);

    int insertDept(@Param("parentId") Long parentId,
                   @Param("ancestors") String ancestors,
                   @Param("deptName") String deptName,
                   @Param("orderNum") Integer orderNum,
                   @Param("dingtalkDeptId") Long dingtalkDeptId,
                   @Param("createBy") String createBy);

    int updateDept(@Param("deptId") Long deptId,
                   @Param("parentId") Long parentId,
                   @Param("ancestors") String ancestors,
                   @Param("deptName") String deptName,
                   @Param("orderNum") Integer orderNum,
                   @Param("status") String status,
                   @Param("updateBy") String updateBy);

    int disableDeptNotIn(@Param("dingtalkDeptIds") List<Long> dingtalkDeptIds,
                         @Param("updateBy") String updateBy);

    Map<String, Object> selectUserByDingTalkUserId(@Param("dingtalkUserId") String dingtalkUserId);

    int insertUser(@Param("deptId") Long deptId,
                   @Param("userName") String userName,
                   @Param("nickName") String nickName,
                   @Param("email") String email,
                   @Param("phonenumber") String phonenumber,
                   @Param("password") String password,
                   @Param("dingtalkUserId") String dingtalkUserId,
                   @Param("dingtalkUnionid") String dingtalkUnionid,
                   @Param("dingtalkDeptId") Long dingtalkDeptId,
                   @Param("jobNumber") String jobNumber,
                   @Param("postName") String postName,
                   @Param("now") LocalDateTime now,
                   @Param("createBy") String createBy);

    int updateUserActive(@Param("userId") Long userId,
                         @Param("deptId") Long deptId,
                         @Param("nickName") String nickName,
                         @Param("email") String email,
                         @Param("phonenumber") String phonenumber,
                         @Param("dingtalkUnionid") String dingtalkUnionid,
                         @Param("dingtalkDeptId") Long dingtalkDeptId,
                         @Param("jobNumber") String jobNumber,
                         @Param("postName") String postName,
                         @Param("now") LocalDateTime now,
                         @Param("updateBy") String updateBy);

    int increaseMissingCountForAbsentUsers(@Param("activeUserIds") List<String> activeUserIds,
                                           @Param("now") LocalDateTime now,
                                           @Param("updateBy") String updateBy);

    int disableMissingUsers(@Param("threshold") Integer threshold,
                            @Param("now") LocalDateTime now,
                            @Param("updateBy") String updateBy);

    int markUserDimission(@Param("dingtalkUserId") String dingtalkUserId,
                          @Param("dimissionTime") LocalDateTime dimissionTime,
                          @Param("updateBy") String updateBy);
}
