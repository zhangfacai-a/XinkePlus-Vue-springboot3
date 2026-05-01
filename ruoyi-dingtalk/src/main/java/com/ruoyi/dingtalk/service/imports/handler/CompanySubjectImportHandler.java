package com.ruoyi.dingtalk.service.imports.handler;

import com.ruoyi.dingtalk.domain.biz.BizCompanySubject;
import com.ruoyi.dingtalk.mapper.BizCompanySubjectMapper;
import com.ruoyi.dingtalk.service.imports.ImportHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

import static com.ruoyi.dingtalk.service.imports.ImportValueUtils.*;
import static org.apache.logging.log4j.util.Strings.isBlank;

/**
 * 公司主体导入。
 *
 * 业务规则：
 * 1. 同一个公司主体允许存在多条记录。
 * 2. 但同一个公司主体的日期区间不允许重叠。
 * 3. Excel 没有 subject_name 时，用 company_name 兜底。
 */
@Component
@RequiredArgsConstructor
public class CompanySubjectImportHandler implements ImportHandler {

    private final BizCompanySubjectMapper mapper;

    @Override
    public String supportTemplateCode() {
        return "COMPANY_SUBJECT";
    }

    @Override
    public void importRow(Map<String, Object> row, Integer rowNo, Long batchId) {
        String companyName = required(row, "company_name", "公司主体");

        Date startDate = date(row, "start_date");
        Date transferDate = date(row, "transfer_date");

        if (startDate == null) {
            throw new RuntimeException("开始日期不能为空");
        }
        if (transferDate == null) {
            throw new RuntimeException("转交日期不能为空");
        }

        if (transferDate.before(startDate)) {
            throw new RuntimeException("转交日期不能早于开始日期");
        }

        if (transferDate.before(startDate)) {
            throw new RuntimeException("转交日期不能早于开始日期");
        }

        BizCompanySubject overlap = mapper.selectOverlap(companyName, startDate, transferDate, null);
        if (overlap != null) {
            throw new RuntimeException("公司主体【" + companyName + "】日期区间重叠");
        }

        String subjectName = str(row, "subject_name");
        if (isBlank(subjectName)) {
            subjectName = companyName;
        }

        BizCompanySubject entity = new BizCompanySubject();
        entity.setAccountantName(str(row, "accountant_name"));
        entity.setStartDate(startDate);
        entity.setTransferDate(transferDate);
        entity.setCompanyName(companyName);
        entity.setSubjectName(subjectName);
        entity.setTaxNo(str(row, "tax_no"));
        entity.setLegalPerson(str(row, "legal_person"));
        entity.setEstablishDate(date(row, "establish_date"));
        entity.setRegisteredCapital(str(row, "registered_capital"));
        entity.setRegisteredAddress(str(row, "registered_address"));
        entity.setBankAccount(str(row, "bank_account"));
        entity.setBankName(str(row, "bank_name"));
        entity.setBrandName(str(row, "brand_name"));
        entity.setRemark(str(row, "remark"));
        entity.setStatus("0");
        entity.setCreateBy("excel_import");

        mapper.insertBizCompanySubject(entity);
    }
}