package com.hzltd.module.amz.adv.controller.admin;

import com.hzltd.module.amz.adv.controller.admin.vo.AmzAdvKeywordRespVO;
import com.hzltd.module.amz.dal.dataobject.AmzAdvCampaignDO;
import com.hzltd.module.amz.dal.dataobject.AmzAdvKeywordDO;
import com.hzltd.module.amz.dal.mapper.AmzAdvCampaignMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AmzAdvOperationControllerTest {

    @InjectMocks
    private AmzAdvOperationController amzAdvOperationController;

    @Mock
    private AmzAdvCampaignMapper amzAdvCampaignMapper;

    @Test
    public void testBuildKeywordRespVO_CalculateBids() throws Exception {
        // Setup mock Keyword
        AmzAdvKeywordDO keywordDO = new AmzAdvKeywordDO();
        keywordDO.setId(100L);
        keywordDO.setBid(2.0); // Base bid: 2.0
        keywordDO.setCampaignId("CAMP-123");

        // Setup mock Campaign with Placement bidding
        AmzAdvCampaignDO campaignDO = new AmzAdvCampaignDO();
        campaignDO.setCampaignId("CAMP-123");
        
        AmzAdvCampaignDO.DynamicBidding dynamicBidding = new AmzAdvCampaignDO.DynamicBidding();
        AmzAdvCampaignDO.PlacementBidding pbTop = new AmzAdvCampaignDO.PlacementBidding();
        pbTop.setPlacement("PLACEMENT_TOP_OF_SEARCH");
        pbTop.setPercentage(50); // +50%
        
        AmzAdvCampaignDO.PlacementBidding pbProduct = new AmzAdvCampaignDO.PlacementBidding();
        pbProduct.setPlacement("PLACEMENT_PRODUCT_PAGE");
        pbProduct.setPercentage(25); // +25%
        
        dynamicBidding.setPlacementBidding(java.util.Arrays.asList(pbTop, pbProduct));
        campaignDO.setDynamicBidding(dynamicBidding);

        when(amzAdvCampaignMapper.selectOne(AmzAdvCampaignDO::getCampaignId, "CAMP-123"))
            .thenReturn(campaignDO);

        // Access private method via reflection
        Method method = AmzAdvOperationController.class.getDeclaredMethod("buildKeywordRespVO", AmzAdvKeywordDO.class);
        method.setAccessible(true);
        AmzAdvKeywordRespVO resultVO = (AmzAdvKeywordRespVO) method.invoke(amzAdvOperationController, keywordDO);

        // Assertions
        Assertions.assertNotNull(resultVO);
        Assertions.assertEquals(2.0, resultVO.getBid());
        // 2.0 * (1 + 0.5) = 3.0
        Assertions.assertEquals(3.0, resultVO.getCalculatedTopBid());
        // 2.0 * (1 + 0.25) = 2.5
        Assertions.assertEquals(2.5, resultVO.getCalculatedProductPageBid());
    }
}
