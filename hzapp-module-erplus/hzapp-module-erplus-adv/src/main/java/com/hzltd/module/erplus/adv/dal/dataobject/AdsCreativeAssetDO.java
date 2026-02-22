package com.hzltd.module.erplus.adv.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * 广告创意素材 DO
 *
 * @author hzadd
 */
@TableName(value = "ads_creative_asset", autoResultMap = true)
@KeySequence("ads_creative_asset_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsCreativeAssetDO extends BaseDO {

    @TableId
    private Long id;
    /** 关联广告ID */
    private Long adId;
    /** 素材类型: IMAGE / VIDEO / HTML */
    private String assetType;
    /** 素材文件URL */
    private String assetUrl;
    /** 素材指纹 (MD5/SHA256) */
    private String assetHash;
    /** 像素宽度 */
    private Integer width;
    /** 像素高度 */
    private Integer height;
    /** 视频时长 (秒) */
    private Integer durationSeconds;
    /** 文件大小 (字节) */
    private Long fileSizeBytes;
    /** 轮播/展示顺序 */
    private Integer sortOrder;
    /** 素材状态: ACTIVE / REPLACED / DELETED */
    private String status;
}
