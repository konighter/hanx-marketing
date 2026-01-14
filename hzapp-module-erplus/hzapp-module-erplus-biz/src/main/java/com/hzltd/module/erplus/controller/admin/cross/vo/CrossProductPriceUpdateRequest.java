package com.hzltd.module.erplus.controller.admin.cross.vo;

import com.hzltd.module.erplus.model.common.PriceModel;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
public class CrossProductPriceUpdateRequest {

     private Long productId;

     private PriceModel listingPrice;

     private List<PriceModel> salePrice;

     public CrossProductPriceUpdateRequest addSalePrice(PriceModel salePrice) {
          if (this.salePrice == null) {
               this.salePrice = Lists.newArrayList();
          }
         this.salePrice.add(salePrice);
         return this;
     }
}
