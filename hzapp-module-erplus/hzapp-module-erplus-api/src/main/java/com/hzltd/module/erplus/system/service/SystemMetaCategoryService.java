package com.hzltd.module.erplus.system.service;

import com.hzltd.module.erplus.system.enums.CrossPlatformEnum;
import com.hzltd.module.erplus.system.model.CrossMetaCategoryModel;

import java.util.List;

public interface SystemMetaCategoryService {

    CrossMetaCategoryModel getCrossMetaCategoryByPlatformCategoryCode(CrossPlatformEnum crossPlatform, String categoryCode);

    List<CrossMetaCategoryModel> getCrossMetaCategoryByPlatform(CrossPlatformEnum crossPlatform);
}
