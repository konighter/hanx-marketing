package com.hzltd.module.amz.spapi.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import com.hzltd.module.amz.spapi.controller.admin.vo.AmzListingFieldRuleVO;
import com.hzltd.module.amz.spapi.controller.admin.vo.AmzListingFormConfigVO;
import com.hzltd.module.amz.spapi.controller.admin.vo.AmzListingFormFieldVO;
import com.hzltd.module.amz.spapi.controller.admin.vo.LogicExpressionVO;
import com.hzltd.module.erplus.system.model.CrossMetaCategoryModel;
import com.hzltd.module.erplus.system.service.SystemMetaCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class AmazonListingSchemaServiceTest {

    @InjectMocks
    private AmazonListingSchemaService service;

    @Mock
    private SystemMetaCategoryService metaCategoryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGenerateFormConfig_LinkageRules() {
        // Load example schema from the UI directory
        String schemaPath = "/Users/work/proj/repo/hzapp/hzapp-erplus/hzapp-ui/hzapp-ui-admin-vue3-tiny/src/app/erplus/views/product/listing/schema/example_schema.json";
        if (!FileUtil.exist(schemaPath)) {
            System.out.println("Warning: example_schema.json not found at " + schemaPath);
            return;
        }
        String schemaContent = FileUtil.readString(schemaPath, CharsetUtil.UTF_8);

        // Mock metaCategoryService
        CrossMetaCategoryModel model = new CrossMetaCategoryModel();
        model.setExtra(schemaContent);
        when(metaCategoryService.getCrossMetaCategoryByPlatformCategoryCode(any(), eq("ART_CRAFT_KIT")))
                .thenReturn(model);

        // Execute
        AmzListingFormConfigVO config = service.generateFormConfig("ART_CRAFT_KIT");

        // Verify
        assertNotNull(config);
        assertNotNull(config.getFields());

        // 1. Verify Parentage Rule (parentage_level -> child_parent_sku_relationship)
        // Finding a field that should have been marked as required by a linkage rule
        Optional<AmzListingFormFieldVO> childSkuRel = config.getFields().stream()
                .filter(f -> f.getId().equals("child_parent_sku_relationship"))
                .findFirst();
        
        assertTrue(childSkuRel.isPresent(), "child_parent_sku_relationship should exist");
        // Assert logic structure for Parentage Rule (now flattened to match UI fields)
        AmzListingFieldRuleVO rule = childSkuRel.get().getLinkages().get(0);
        LogicExpressionVO logic = rule.getConditionLogic();
        assertNotNull(logic, "Logic expression should be structured");
        assertEquals("EQ", logic.getOperator());
        assertEquals("parentage_level.0.value", logic.getField());
        assertEquals("parent", logic.getValue());
        
        // Verify flattened rendered string format
        assertEquals("parentage_level.0.value == 'parent'", rule.getCondition());

        // 2. Verify GTIN Exemption Rule / Visibility
        Optional<AmzListingFormFieldVO> identifier = config.getFields().stream()
                .filter(f -> f.getId().equals("externally_assigned_product_identifier"))
                .findFirst();
        
        assertTrue(identifier.isPresent());
        boolean hasNegationRule = identifier.get().getLinkages().stream()
                .anyMatch(r -> r.getConditionLogic() != null && "NOT".equals(r.getConditionLogic().getOperator()));
        assertTrue(hasNegationRule, "Should have a NOT(!) rule for identifier visibility/exemption");

        // 3. Verify standard requirement rule
        boolean hasRequiredRule = config.getFields().stream()
                .anyMatch(f -> !f.getLinkages().isEmpty() &&
                          f.getLinkages().stream().anyMatch(r -> "requirement".equals(r.getType())));
        assertTrue(hasRequiredRule, "Should have requirement linkage rules generated from then blocks");
        
        System.out.println("Test passed: linkage rules successfully parsed and validated.");
    }

    @Test
    public void testGenerateFormConfig_AggressiveFlattening() {
        String schemaPath = "/Users/work/proj/repo/hzapp/hzapp-erplus/hzapp-ui/hzapp-ui-admin-vue3-tiny/src/app/erplus/views/product/listing/schema/example_schema.json";
        if (!FileUtil.exist(schemaPath)) return;
        String schemaContent = FileUtil.readString(schemaPath, CharsetUtil.UTF_8);

        CrossMetaCategoryModel model = new CrossMetaCategoryModel();
        model.setExtra(schemaContent);
        when(metaCategoryService.getCrossMetaCategoryByPlatformCategoryCode(any(), eq("ART_CRAFT_KIT")))
                .thenReturn(model);

        AmzListingFormConfigVO config = service.generateFormConfig("ART_CRAFT_KIT");

        // 1. Verify flattening of item_package_dimensions
        boolean hasDimensionLength = config.getFields().stream()
                .anyMatch(f -> f.getId().contains("item_package_dimensions.0.length.value"));
        assertTrue(hasDimensionLength, "item_package_dimensions should be flattened into sub-properties");

        // 2. Verify flattening of purchasable_offer
        boolean hasOfferCurrency = config.getFields().stream()
                .anyMatch(f -> f.getId().contains("purchasable_offer.0.currency"));
        assertTrue(hasOfferCurrency, "purchasable_offer should be flattened into sub-properties like currency");

        // 3. Verify aggressive flattening even for TRUE_ARRAY if they match value-wrapper pattern
        Optional<AmzListingFormFieldVO> hzReg = config.getFields().stream()
                .filter(f -> f.getId().equals("supplier_declared_dg_hz_regulation.0.value"))
                .findFirst();
        assertTrue(hzReg.isPresent(), "Field with maxItems > 1 but value-wrapper items should be flattened to .0.value");
        assertEquals("enum", hzReg.get().getType(), "Flattened child should have correct type");
        assertFalse(hzReg.get().getOptions().isEmpty(), "Options should still be extracted");
        assertTrue(hzReg.get().getOptions().stream().anyMatch(o -> o.getValue().equals("ghs")));

        // 4. Verify mapping table accuracy
        assertNotNull(config.getFieldMapping());
        assertEquals("brand_name.0.value", config.getFieldMapping().get("brand_name"), "Simple wrapper should map to .value");
        assertEquals("item_package_dimensions.0", config.getFieldMapping().get("item_package_dimensions"), "Complex object should map to .0 base");
        assertEquals("supplier_declared_dg_hz_regulation.0.value", config.getFieldMapping().get("supplier_declared_dg_hz_regulation"), "Should correctly map multi-item compatible field to first item wrapper");

        System.out.println("Aggressive flattening and options extraction verification passed.");
    }

    @Test
    public void testNestedFieldsParsing() {
        String schemaPath = "/Users/work/proj/repo/hzapp/hzapp-erplus/hzapp-ui/hzapp-ui-admin-vue3-tiny/src/app/erplus/views/product/listing/schema/example_schema.json";
        if (!FileUtil.exist(schemaPath)) return;
        String schemaContent = FileUtil.readString(schemaPath, CharsetUtil.UTF_8);

        CrossMetaCategoryModel model = new CrossMetaCategoryModel();
        model.setExtra(schemaContent);
        when(metaCategoryService.getCrossMetaCategoryByPlatformCategoryCode(any(), eq("ART_CRAFT_KIT")))
                .thenReturn(model);

        AmzListingFormConfigVO config = service.generateFormConfig("ART_CRAFT_KIT");

        // 1. Verify purchasable_offer.0.our_price exists as a collapsed wrapper
        Optional<AmzListingFormFieldVO> ourPriceField = config.getFields().stream()
                .filter(f -> f.getId().equals("purchasable_offer.0.our_price"))
                .findFirst();
        assertTrue(ourPriceField.isPresent(), "purchasable_offer.0.our_price should exist");
        
        // our_price items are collapsed because schedule is simple (no time)
        // So the children of our_price should be a LEAF node with isAmazonWrapper
        assertTrue(ourPriceField.get().getChildren().size() > 0);
        AmzListingFormFieldVO firstChild = ourPriceField.get().getChildren().get(0);
        assertEquals("LEAF", determineTypeIndicator(firstChild), "Child of our_price should be collapsed to LEAF");
        assertTrue(Boolean.TRUE.equals(firstChild.getExtra().get("isAmazonWrapper")), "Collapsed child should have isAmazonWrapper: true");

        // 2. Verify purchasable_offer.0.discounted_price remains an array/nested (has start_at/end_at)
        Optional<AmzListingFormFieldVO> discPrice = config.getFields().stream()
                .filter(f -> f.getId().equals("purchasable_offer.0.discounted_price"))
                .findFirst();
        assertTrue(discPrice.isPresent());
        // Since it has start_at/end_at, the schedule remains nested/array
        boolean hasScheduleChild = discPrice.get().getChildren().get(0).getChildren().stream()
                .anyMatch(f -> f.getName().equals("schedule"));
        assertTrue(hasScheduleChild, "discounted_price should keep schedule nesting due to time fields");

        // 3. Verify list_price.0.value_with_tax exists as a leaf
        boolean hasListPriceValue = config.getFields().stream()
                .anyMatch(f -> f.getId().equals("list_price.0.value_with_tax"));
        assertTrue(hasListPriceValue, "list_price.0.value_with_tax should exist");

        System.out.println("Pragmatic schedule handling verification passed.");
    }

    @Test
    public void testRefinements() {
        // 1. Load 3D_PRINTER schema to test Smart Title Selection, Schedule Flattening and Multi-Select
        String schemaPath = "/Users/work/proj/repo/hzapp/hzapp-erplus/hzapp-ui/hzapp-ui-admin-vue3-tiny/src/app/erplus/views/product/listing/schema/3D_print.json";
        if (FileUtil.exist(schemaPath)) {
            String schemaContent = FileUtil.readString(schemaPath, CharsetUtil.UTF_8);
            CrossMetaCategoryModel model = new CrossMetaCategoryModel();
            model.setExtra(schemaContent);
            when(metaCategoryService.getCrossMetaCategoryByPlatformCategoryCode(any(), eq("3D_PRINTER")))
                    .thenReturn(model);
            
            AmzListingFormConfigVO config = service.generateFormConfig("3D_PRINTER");
            
            // Output to example_resp.json for verification
            String outPath = "/Users/work/proj/repo/hzapp/hzapp-erplus/hzapp-ui/hzapp-ui-admin-vue3-tiny/src/app/erplus/views/product/listing/schema/example_resp.json";
            cn.hutool.json.JSONUtil.toJsonStr(config);
            FileUtil.writeString(cn.hutool.json.JSONUtil.toJsonPrettyStr(config), outPath, CharsetUtil.UTF_8);
            System.out.println("Updated example_resp.json with latest parsing results.");
            
            // A. Verify Title Logic for included_components
            Optional<AmzListingFormFieldVO> incComp = config.getFields().stream()
                    .filter(f -> f.getId().equals("included_components"))
                    .findFirst();
            if (incComp.isPresent()) {
                assertEquals("Included Components", incComp.get().getTitle());
                assertEquals("array", incComp.get().getType());
            }

            // B. Verify Schedule Flattening for our_price
            // Deep path in schema is purchasable_offer.our_price.0.schedule.0.value_with_tax
            String ourPricePath = config.getFieldMapping().get("our_price");
            // Since our_price is nested in purchasable_offer, we look inside that child
            Optional<AmzListingFormFieldVO> purchOffer = config.getFields().stream()
                    .filter(f -> f.getId().equals("purchasable_offer"))
                    .findFirst();
            if (purchOffer.isPresent()) {
                Optional<AmzListingFormFieldVO> ourPriceRef = purchOffer.get().getChildren().stream()
                        .filter(f -> f.getName().equals("our_price"))
                        .findFirst();
                if (ourPriceRef.isPresent()) {
                    AmzListingFormFieldVO f = ourPriceRef.get();
                    assertTrue((Boolean) f.getExtra().get("isAmazonWrapper"));
                    // The valuePath should reflect the deep schedule path
                    String valuePath = (String) f.getExtra().get("valuePath");
                    assertNotNull(valuePath);
                    assertTrue(valuePath.contains("schedule.0.value_with_tax"));
                }
            }

            // C. Verify Multi-Select Array Integrity
            // recommended_browse_nodes has maxItems: 2 in 3D_PRINTER
            Optional<AmzListingFormFieldVO> browseNodes = config.getFields().stream()
                    .filter(f -> f.getId().equals("recommended_browse_nodes"))
                    .findFirst();
            if (browseNodes.isPresent()) {
                assertEquals("array", browseNodes.get().getType(), "Multi-select must be array type");
                assertTrue((Boolean) browseNodes.get().getExtra().get("isMultiSelect"));
            }
        }
    }

    private String determineTypeIndicator(AmzListingFormFieldVO field) {
        if (field.getChildren() != null && !field.getChildren().isEmpty()) {
            return "NESTED";
        }
        return "LEAF";
    }
}
