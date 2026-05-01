package com.ruoyi.dingtalk.service.biz;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.dingtalk.domain.biz.BizProfitUnit;
import com.ruoyi.dingtalk.mapper.BizProfitUnitMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 利润单元业务服务。
 *
 * 业务规则：
 * 1. 业务唯一键：
 *    直接负责人 + 开始日期 + 结束日期 + 利润单元全称 + 成交类型
 *
 * 2. 新增：
 *    - 五字段完全一致：不允许新增
 *    - 五字段不一致，但同负责人 + 同利润单元 + 同成交类型下日期区间重叠：不允许新增
 *    - 不重叠：允许新增
 *
 * 3. 修改：
 *    - 五字段完全一致但不是当前记录：不允许修改
 *    - 日期区间和其他记录重叠：不允许修改
 *    - 不冲突：允许修改
 */
@Service
@RequiredArgsConstructor
public class BizProfitUnitService {

    private final BizProfitUnitMapper mapper;

    public BizProfitUnit getById(Long id) {
        return mapper.selectBizProfitUnitById(id);
    }

    public List<BizProfitUnit> list(BizProfitUnit query) {
        return mapper.selectBizProfitUnitList(query);
    }

    /**
     * 新增：不使用 upsert。
     */
    @Transactional(rollbackFor = Exception.class)
    public int add(BizProfitUnit entity) {
        validateRequired(entity);
        validateDateRange(entity);
        checkUnique(entity, null);
        checkDateOverlap(entity, null);
        fillCreateInfo(entity);
        return mapper.insertBizProfitUnit(entity);
    }

    /**
     * 修改：排除当前记录后检查唯一键和日期区间。
     */
    @Transactional(rollbackFor = Exception.class)
    public int update(BizProfitUnit entity) {
        if (entity.getId() == null) {
            throw new ServiceException("主键ID不能为空");
        }

        validateRequired(entity);
        validateDateRange(entity);
        checkUnique(entity, entity.getId());
        checkDateOverlap(entity, entity.getId());
        fillUpdateInfo(entity);

        return mapper.updateBizProfitUnit(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public int delete(Long[] ids) {
        if (ids == null || ids.length == 0) {
            throw new ServiceException("请选择要删除的数据");
        }
        return mapper.deleteBizProfitUnitByIds(ids);
    }

    private void validateRequired(BizProfitUnit entity) {
        if (entity == null) {
            throw new ServiceException("利润单元数据不能为空");
        }
        if (StringUtils.isBlank(entity.getOwnerName())) {
            throw new ServiceException("直接负责人不能为空");
        }
        if (entity.getStartDate() == null) {
            throw new ServiceException("开始日期不能为空");
        }
        if (entity.getTransferDate() == null) {
            throw new ServiceException("结束日期不能为空");
        }
        if (StringUtils.isBlank(entity.getProfitUnitName())) {
            throw new ServiceException("利润单元全称不能为空");
        }
        if (StringUtils.isBlank(entity.getDealType())) {
            throw new ServiceException("成交类型不能为空");
        }
    }

    private void validateDateRange(BizProfitUnit entity) {
        if (entity.getTransferDate().before(entity.getStartDate())) {
            throw new ServiceException("结束日期不能早于开始日期");
        }
    }

    /**
     * 检查五字段完全重复。
     */
    private void checkUnique(BizProfitUnit entity, Long currentId) {
        BizProfitUnit exist = mapper.selectByImportUniqueKey(
                entity.getOwnerName(),
                entity.getStartDate(),
                entity.getTransferDate(),
                entity.getProfitUnitName(),
                entity.getDealType()
        );

        if (exist != null && (currentId == null || !currentId.equals(exist.getId()))) {
            throw new ServiceException("当前利润单元已存在，请勿重复保存。唯一键：直接负责人 + 开始日期 + 结束日期 + 利润单元全称 + 成交类型");
        }
    }

    /**
     * 检查日期区间重叠。
     *
     * 判断维度：
     * 直接负责人 + 利润单元全称 + 成交类型
     *
     * 判断规则：
     * 老开始日期 <= 新结束日期
     * 且
     * 老结束日期 >= 新开始日期
     */
    private void checkDateOverlap(BizProfitUnit entity, Long currentId) {
        BizProfitUnit overlap = mapper.selectImportOverlap(
                entity.getOwnerName(),
                entity.getProfitUnitName(),
                entity.getDealType(),
                entity.getStartDate(),
                entity.getTransferDate(),
                currentId
        );

        if (overlap != null) {
            throw new ServiceException(
                    "日期区间冲突：直接负责人[" + entity.getOwnerName() + "]，利润单元[" + entity.getProfitUnitName() + "]，成交类型[" + entity.getDealType() + "]，"
                            + "当前区间[" + formatDate(entity.getStartDate()) + " ~ " + formatDate(entity.getTransferDate()) + "] "
                            + "与已有记录ID[" + overlap.getId() + "]区间["
                            + formatDate(overlap.getStartDate()) + " ~ " + formatDate(overlap.getTransferDate()) + "]重叠"
            );
        }
    }

    private void fillCreateInfo(BizProfitUnit entity) {
        String username = getUsernameQuietly();
        entity.setCreateBy(username);
        entity.setUpdateBy(username);
        if (StringUtils.isBlank(entity.getStatus())) {
            entity.setStatus("0");
        }
    }

    private void fillUpdateInfo(BizProfitUnit entity) {
        entity.setUpdateBy(getUsernameQuietly());
    }

    private String getUsernameQuietly() {
        try {
            return SecurityUtils.getUsername();
        } catch (Exception e) {
            return "system";
        }
    }

    private String formatDate(java.util.Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
}