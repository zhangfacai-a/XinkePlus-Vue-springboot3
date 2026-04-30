package com.ruoyi.dingtalk.service.imports.handler;

import com.ruoyi.dingtalk.domain.biz.BizCompanySubject;
import com.ruoyi.dingtalk.domain.biz.BizProfitUnit;
import com.ruoyi.dingtalk.mapper.BizCompanySubjectMapper;
import com.ruoyi.dingtalk.mapper.BizProfitUnitMapper;
import com.ruoyi.dingtalk.service.imports.ImportHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import static com.ruoyi.dingtalk.service.imports.ImportValueUtils.*;
import static org.apache.logging.log4j.util.Strings.isBlank;

/** 利润单元导入 */
@Component
@RequiredArgsConstructor
public class ProfitUnitImportHandler implements ImportHandler {
    private final BizProfitUnitMapper mapper;
    private final BizCompanySubjectMapper companySubjectMapper;
    @Override public String supportTemplateCode() { return "PROFIT_UNIT"; }

    @Override
    public void importRow(Map<String, Object> row, Integer rowNo, Long batchId) {

        String profitName = required(row, "profit_unit_name", "利润单元全称");

        String dealType = str(row, "deal_type");
        Date startDate = date(row, "start_date");
        Date transferDate = date(row, "transfer_date");

        if (startDate != null && transferDate != null && transferDate.before(startDate)) {
            throw new RuntimeException("转交日期不能早于开始日期");
        }

        Long companySubjectId = null;
        String companyName = str(row, "company_name");

        if (!isBlank(companyName)) {
            BizCompanySubject subject = companySubjectMapper.selectByCompanyName(companyName);
            if (subject != null) {
                companySubjectId = subject.getId();
            }
        }

        BizProfitUnit entity = null;

        if (startDate != null && transferDate != null) {
            entity = mapper.selectOverlap(profitName, dealType, startDate, transferDate, null);

            if (entity != null) {
                throw new RuntimeException(
                        "利润单元【" + profitName + "】成交类型【" + dealType + "】日期区间重叠"
                );
            }
        }

        entity = new BizProfitUnit();

        entity.setCompanySubjectId(companySubjectId);
        entity.setStartDate(startDate);
        entity.setTransferDate(transferDate);

        entity.setProfitUnitName(profitName);
        entity.setDealType(dealType);

        entity.setPlatform(str(row, "platform"));
        entity.setBrandName(str(row, "brand_name"));
        entity.setDeptName(str(row, "dept_name"));
        entity.setDeptOwnerName(str(row, "dept_owner_name"));
        entity.setOwnerName(str(row, "owner_name"));
        entity.setCategoryName(str(row, "category_name"));

        entity.setStoreAuthId(str(row, "store_auth_id"));
        entity.setStoreCode(str(row, "store_code"));
        entity.setStoreName(str(row, "store_name"));

        entity.setAccountId(str(row, "account_id"));
        entity.setLiveRoomType(str(row, "live_room_type"));
        entity.setIsEnabled(str(row, "is_enabled"));
        entity.setIsAuthorized(str(row, "is_authorized"));

        entity.setStatus("0");
        entity.setCreateBy("excel_import");
        entity.setAccountantName(str(row, "accountant_name"));
        entity.setAccountingStartDate(date(row, "accounting_start_date"));
        entity.setDeptOwnerJobNo(str(row, "dept_owner_job_no"));
        entity.setDeptOwnerName(str(row, "dept_owner_name"));
        entity.setRegionName(str(row, "region_name"));
        entity.setProjectGroup(str(row, "project_group"));
        entity.setProjectOwnerName(str(row, "project_owner_name"));
        entity.setOwnerJobNo(str(row, "owner_job_no"));
        entity.setCategoryName(str(row, "category_name"));
        entity.setStoreAuthId(str(row, "store_auth_id"));
        entity.setStoreCode(str(row, "store_code"));
        entity.setStoreName(str(row, "store_name"));
        entity.setAccountId(str(row, "account_id"));
        entity.setDealType(str(row, "deal_type"));
        entity.setLiveRoomType(str(row, "live_room_type"));
        entity.setAccountingBasis(str(row, "accounting_basis"));
        entity.setRegisterMobile(str(row, "register_mobile"));
        entity.setRegisterRealName(str(row, "register_real_name"));
        entity.setRegisterReferrer(str(row, "register_referrer"));
        entity.setRegisterDate(date(row, "register_date"));
        entity.setDepositAmount(decimal(row, "deposit_amount"));
        entity.setStartDate(date(row, "start_date"));
        entity.setTransferDate(date(row, "transfer_date"));
        entity.setStopDate(date(row, "stop_date"));
        entity.setIsEnabled(str(row, "is_enabled"));
        entity.setIsAuthorized(str(row, "is_authorized"));
        entity.setQianchuanAccountId(str(row, "qianchuan_account_id"));
        entity.setQianchuanAccountName(str(row, "qianchuan_account_name"));
        entity.setQianchuanMobile(str(row, "qianchuan_mobile"));
        entity.setQianchuanCompanySubject(str(row, "qianchuan_company_subject"));
        entity.setQianchuanAuth(str(row, "qianchuan_auth"));
        entity.setVerifyRecord(str(row, "verify_record"));
        entity.setUid(str(row, "uid"));

        mapper.insertBizProfitUnit(entity);
    }
}
