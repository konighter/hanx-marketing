package com.hzltd.module.amz.spapi.controller.admin.fullfil.vo;

import com.hzltd.module.erplus.spapi.model.common.PriceModel;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;
import software.amazon.spapi.models.fulfillment.inbound.v2024_03_20.Incentive;
import software.amazon.spapi.models.fulfillment.inbound.v2024_03_20.PlacementOption;

import java.util.List;
import java.util.Optional;

@Data
public class AmzPlacementOption extends PlacementOption {

    private List<AmzShipment> shipmentDetails = Lists.newArrayList();

    public AmzPlacementOption addShipmentDetail(String shipmentId, AmzShipment shipment) {
        shipmentDetails.add( shipment);
        return this;
    }

    public PriceModel getPlaceFee() {
        Optional<Incentive> fee = this.getFees().stream().filter(f -> "FEE".equals(f.getType()) && "Placement Services".equals(f.getTarget())).findAny();
        if (fee.isPresent()) {
            Incentive f = fee.get();
            return new PriceModel().setCurrency(f.getValue().getCode()).setAmount(f.getValue().getAmount()).setType(f.getTarget());
        } else {
            return null;
        }
    }


}
