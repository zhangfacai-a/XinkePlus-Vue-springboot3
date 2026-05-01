package com.ruoyi.dingtalk.service.imports.handler;

import com.ruoyi.dingtalk.domain.biz.BizProfitUnit;
import com.ruoyi.dingtalk.domain.biz.BizStoreAuth;
import com.ruoyi.dingtalk.mapper.BizProfitUnitMapper;
import com.ruoyi.dingtalk.mapper.BizStoreAuthMapper;
import com.ruoyi.dingtalk.service.imports.ImportHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import static com.ruoyi.dingtalk.service.imports.ImportValueUtils.*;

/** 店铺授权导入 */
@Component
@RequiredArgsConstructor
public class StoreAuthImportHandler implements ImportHandler {
    private final BizStoreAuthMapper mapper;
    private final BizProfitUnitMapper profitUnitMapper;
    @Override public String supportTemplateCode() { return "STORE_AUTH"; }

    @Override
    public void importRow(Map<String, Object> row, Integer rowNo, Long batchId) {
        String storeName = required(row, "store_name", "店铺名称");
        String platform = str(row, "platform");
        String storeCode = str(row, "store_code");
        BizStoreAuth entity = mapper.selectByPlatformAndStore(platform, storeName, storeCode);
        if (entity == null) entity = new BizStoreAuth();
        entity.setPlatform(platform);
        entity.setStoreCode(storeCode);
        entity.setStoreName(storeName);
        entity.setAuthAccount(str(row, "auth_account"));
        entity.setAuthSubject(str(row, "auth_subject"));
        entity.setAuthStatus(str(row, "auth_status") == null ? "0" : str(row, "auth_status"));
        entity.setOpenTime(date(row, "open_time"));
        entity.setExpireTime(date(row, "expire_time"));
        entity.setRemark(str(row, "remark"));
        entity.setStatus("0");

        String profitUnitName = str(row, "profit_unit_name");
        if (profitUnitName != null) {
            BizProfitUnit unit = profitUnitMapper.selectByName(profitUnitName);
            if (unit == null) throw new IllegalArgumentException("利润单元不存在：" + profitUnitName);
            entity.setProfitUnitId(unit.getId());
            entity.setCompanySubjectId(unit.getCompanySubjectId());
        }

        if (entity.getId() == null) {
            entity.setCreateBy("excel_import");
            mapper.insertBizStoreAuth(entity);
        } else {
            entity.setUpdateBy("excel_import");
            mapper.updateBizStoreAuth(entity);
        }
    }
}
