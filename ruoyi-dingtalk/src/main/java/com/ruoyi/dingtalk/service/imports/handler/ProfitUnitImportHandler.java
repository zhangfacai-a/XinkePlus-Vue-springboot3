package com.ruoyi.dingtalk.service.imports.handler;

import com.ruoyi.dingtalk.domain.biz.BizCompanySubject;
import com.ruoyi.dingtalk.domain.biz.BizProfitUnit;
import com.ruoyi.dingtalk.mapper.BizCompanySubjectMapper;
import com.ruoyi.dingtalk.mapper.BizProfitUnitMapper;
import com.ruoyi.dingtalk.service.imports.ImportHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static com.ruoyi.dingtalk.service.imports.ImportValueUtils.*;
import static org.apache.logging.log4j.util.Strings.isBlank;

/**
 * PROFIT_UNIT 利润单元导入。
 *
 * 业务唯一键：
 * 1. 直接负责人 owner_name
 * 2. 开始日期 start_date
 * 3. 结束日期 transfer_date
 * 4. 利润单元全称 profit_unit_name
 * 5. 成交类型 deal_type
 *
 * 导入策略：
 * - 5 个业务唯一键完全一致：更新已有记录的其他字段。
 * - 5 个业务唯一键不完全一致：准备新增。
 * - 新增前检查：同一直接负责人 + 同一利润单元全称 + 同一成交类型下，日期区间不能重叠。
 * - 日期区间重叠：本行导入失败，不新增。
 * - 日期区间不重叠：新增。
 */
@Component
@RequiredArgsConstructor
public class ProfitUnitImportHandler implements ImportHandler {
    private final BizProfitUnitMapper mapper;
    private final BizCompanySubjectMapper companySubjectMapper;

    @Override
    public String supportTemplateCode() {
        return "PROFIT_UNIT";
    }

    @Override
    public void importRow(Map<String, Object> row, Integer rowNo, Long batchId) {
        String ownerName = required(row, "owner_name", "直接负责人");
        Date startDate = requiredFirstDate(row, "开始日期", "start_date");

        /*
         * 正确配置：Excel“结束日期” -> transfer_date。
         * 这里保留 end_date / stop_date 兜底，是为了兼容历史模板配置错误。
         */
        Date transferDate = requiredFirstDate(row, "结束日期", "transfer_date", "end_date", "stop_date");

        String profitName = required(row, "profit_unit_name", "利润单元全称");
        String dealType = required(row, "deal_type", "成交类型");

        if (transferDate.before(startDate)) {
            throw new RuntimeException("结束日期不能早于开始日期");
        }

        Long companySubjectId = null;
        String companyName = str(row, "company_name");
        if (!isBlank(companyName)) {
            BizCompanySubject subject = companySubjectMapper.selectByCompanyName(companyName);
            if (subject != null) {
                companySubjectId = subject.getId();
            }
        }

        BizProfitUnit entity = new BizProfitUnit();
        entity.setCompanySubjectId(companySubjectId);

        entity.setAccountantName(str(row, "accountant_name"));
        entity.setAccountingStartDate(date(row, "accounting_start_date"));

        entity.setProfitUnitName(profitName);
        entity.setProfitUnitCode(str(row, "profit_unit_code"));

        entity.setPlatform(str(row, "platform"));
        entity.setBrandName(str(row, "brand_name"));
        entity.setCategoryName(str(row, "category_name"));

        entity.setDeptName(str(row, "dept_name"));
        entity.setDeptOwnerJobNo(str(row, "dept_owner_job_no"));
        entity.setDeptOwnerName(str(row, "dept_owner_name"));

        entity.setRegionName(str(row, "region_name"));
        entity.setProjectGroup(str(row, "project_group"));
        entity.setProjectOwnerName(str(row, "project_owner_name"));

        entity.setOwnerJobNo(str(row, "owner_job_no"));
        entity.setOwnerName(ownerName);

        entity.setStoreAuthId(str(row, "store_auth_id"));
        entity.setStoreCode(str(row, "store_code"));
        entity.setStoreName(str(row, "store_name"));

        entity.setAccountId(str(row, "account_id"));
        entity.setDealType(dealType);
        entity.setLiveRoomType(str(row, "live_room_type"));
        entity.setAccountingBasis(str(row, "accounting_basis"));

        entity.setRegisterMobile(str(row, "register_mobile"));
        entity.setRegisterRealName(str(row, "register_real_name"));
        entity.setRegisterReferrer(str(row, "register_referrer"));
        entity.setRegisterDate(date(row, "register_date"));
        entity.setDepositAmount(decimal(row, "deposit_amount"));

        entity.setStartDate(startDate);
        entity.setTransferDate(transferDate);

        /*
         * 正确模板下：结束日期=transfer_date，停运日期=stop_date。
         * 如果 transfer_date 没有配置、靠 stop_date 兜底成“结束日期”，就不要再把 stop_date 当停运日期写入。
         */
        entity.setStopDate(str(row, "transfer_date") == null ? null : date(row, "stop_date"));

        entity.setIsEnabled(str(row, "is_enabled"));
        entity.setIsAuthorized(str(row, "is_authorized"));

        entity.setQianchuanAccountId(str(row, "qianchuan_account_id"));
        entity.setQianchuanAccountName(str(row, "qianchuan_account_name"));
        entity.setQianchuanMobile(str(row, "qianchuan_mobile"));
        entity.setQianchuanCompanySubject(str(row, "qianchuan_company_subject"));
        entity.setQianchuanAuth(str(row, "qianchuan_auth"));

        entity.setVerifyRecord(str(row, "verify_record"));
        entity.setUid(str(row, "uid"));

        entity.setStatus("0");
        entity.setCreateBy("excel_import");
        entity.setUpdateBy("excel_import");

        BizProfitUnit exists = mapper.selectByImportUniqueKey(
                ownerName,
                startDate,
                transferDate,
                profitName,
                dealType
        );

        if (exists != null) {
            entity.setId(exists.getId());
            mapper.updateBizProfitUnit(entity);
            return;
        }

        BizProfitUnit overlap = mapper.selectImportOverlap(
                ownerName,
                profitName,
                dealType,
                startDate,
                transferDate,
                null
        );

        if (overlap != null) {
            throw new RuntimeException(
                    "日期区间冲突：直接负责人[" + ownerName + "]，利润单元[" + profitName + "]，成交类型[" + dealType + "]，"
                            + "当前区间[" + formatDate(startDate) + " ~ " + formatDate(transferDate) + "] 与已有记录ID[" + overlap.getId() + "]区间["
                            + formatDate(overlap.getStartDate()) + " ~ " + formatDate(overlap.getTransferDate()) + "]重叠"
            );
        }

        mapper.insertBizProfitUnit(entity);
    }

    private String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
}
