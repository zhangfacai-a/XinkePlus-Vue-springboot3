package com.ruoyi.dingtalk.mapper;

import com.ruoyi.dingtalk.domain.biz.BizCompanySubject;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 公司主体 Mapper
 */
public interface BizCompanySubjectMapper {

    BizCompanySubject selectBizCompanySubjectById(Long id);

    List<BizCompanySubject> selectBizCompanySubjectList(BizCompanySubject query);

    int insertBizCompanySubject(BizCompanySubject entity);

    int updateBizCompanySubject(BizCompanySubject entity);

    int deleteBizCompanySubjectByIds(@Param("ids") Long[] ids);

    /**
     * 根据 公司名称 + 主体名称 查询。
     * 现在不是导入唯一判断，只保留给页面或其他逻辑使用。
     */
    BizCompanySubject selectByCompanyAndSubject(@Param("companyName") String companyName,
                                                @Param("subjectName") String subjectName);

    /**
     * 查询同一个公司主体下是否存在重叠日期区间。
     *
     * 重叠判断：
     * 已有开始日期 <= 新转交日期
     * 并且
     * 已有转交日期 >= 新开始日期
     */
    BizCompanySubject selectOverlap(@Param("companyName") String companyName,
                                    @Param("startDate") Date startDate,
                                    @Param("transferDate") Date transferDate,
                                    @Param("excludeId") Long excludeId);
    BizCompanySubject selectByCompanyName(@Param("companyName") String companyName);
}