package com.ruoyi.dingtalk.mapper;

import com.ruoyi.dingtalk.domain.biz.BizStoreAuth;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/** 店铺授权 Mapper */
public interface BizStoreAuthMapper {
    BizStoreAuth selectBizStoreAuthById(Long id);
    List<BizStoreAuth> selectBizStoreAuthList(BizStoreAuth query);
    int insertBizStoreAuth(BizStoreAuth entity);
    int updateBizStoreAuth(BizStoreAuth entity);
    int deleteBizStoreAuthByIds(@Param("ids") Long[] ids);

    BizStoreAuth selectByPlatformAndStore(@Param("platform") String platform, @Param("storeName") String storeName, @Param("storeCode") String storeCode);
}
