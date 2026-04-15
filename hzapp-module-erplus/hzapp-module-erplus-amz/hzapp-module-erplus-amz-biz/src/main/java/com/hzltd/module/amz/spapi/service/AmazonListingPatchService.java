package com.hzltd.module.amz.spapi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.hzltd.framework.common.util.json.JsonUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Utility Service to generate JSON Patch operations for Amazon Listings SP-API.
 * Compares old vs new attributes and generates a list of 'replace', 'add' or 'delete' operations.
 */
@Slf4j
@Service
public class AmazonListingPatchService {

    @Data
    public static class PatchOperation {
        private String op;    // replace, add, delete
        private String path;  // e.g. /attributes/brand
        private List<Map<String, Object>> value; // Amazon specific: array of {value, marketplace_id}
    }

    /**
     * Generates a list of patches by comparing the stored JSON blob with the new submission.
     */
    public List<PatchOperation> generatePatches(String oldExtraJson, Map<String, Object> newAttributes) {
        List<PatchOperation> patches = new ArrayList<>();
        JsonNode oldNode = JsonUtils.parseTree(oldExtraJson != null ? oldExtraJson : "{}");

        // Iterate through new attributes (Map from UI)
        for (Map.Entry<String, Object> entry : newAttributes.entrySet()) {
            String attrCode = entry.getKey();
            JsonNode newValueNode = JsonUtils.parseTree(JsonUtils.toJsonString(entry.getValue()));

            if (!oldNode.has(attrCode)) {
                // Add new attribute
                patches.add(createOperation("add", attrCode, entry.getValue()));
            } else if (!oldNode.get(attrCode).equals(newValueNode)) {
                // Update existing attribute (replace)
                patches.add(createOperation("replace", attrCode, entry.getValue()));
            }
        }

        // Check for deletions (attributes in old that are NOT in new)
        Iterator<String> oldFields = oldNode.fieldNames();
        while (oldFields.hasNext()) {
            String field = oldFields.next();
            if (!newAttributes.containsKey(field)) {
                patches.add(createOperation("delete", field, null));
            }
        }

        return patches;
    }

    private PatchOperation createOperation(String op, String attrCode, Object value) {
        PatchOperation patch = new PatchOperation();
        patch.setOp(op);
        patch.setPath("/attributes/" + attrCode);
        
        if (value != null) {
            // SP-API requires attributes to be wrapped in a list of {value: ..., marketplace_id: ...}
            // unless the UI submission already did it.
            if (value instanceof List) {
                patch.setValue((List<Map<String, Object>>) value);
            } else {
                // Fallback / safety wrapping logic (though the UI should handle this normally)
                // In actual implementation, we might need a more robust mapper here.
            }
        }
        
        return patch;
    }
}
