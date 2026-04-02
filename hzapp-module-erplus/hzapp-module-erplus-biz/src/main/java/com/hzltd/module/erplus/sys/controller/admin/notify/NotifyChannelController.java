package com.hzltd.module.erplus.sys.controller.admin.notify;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.dal.dataobject.notify.NotifyChannelDO;
import com.hzltd.module.erplus.service.notify.ChannelNotifyService;
import com.hzltd.module.erplus.sys.controller.admin.notify.vo.NotifyChannelPageReqVO;
import com.hzltd.module.erplus.sys.controller.admin.notify.vo.NotifyChannelSaveReqVO;
import com.hzltd.module.erplus.system.model.NotifyMessage;
import com.hzltd.module.erplus.system.service.ChannelNotifySendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import static com.hzltd.framework.common.pojo.CommonResult.success;

/**
 * 通知渠道管理 Controller
 */
@Tag(name = "管理后台 - 通知渠道")
@RestController
@RequestMapping("/erplus/notify-channel")
public class NotifyChannelController {

    @Resource
    private ChannelNotifyService channelNotifyService;

    @Resource
    private ChannelNotifySendService notifySendService;

    @PostMapping("/create")
    @Operation(summary = "创建通知渠道")
    public CommonResult<Long> createChannel(@Valid @RequestBody NotifyChannelSaveReqVO reqVO) {
        return success(channelNotifyService.createChannel(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改通知渠道")
    public CommonResult<Boolean> updateChannel(@Valid @RequestBody NotifyChannelSaveReqVO reqVO) {
        channelNotifyService.updateChannel(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除通知渠道")
    public CommonResult<Boolean> deleteChannel(@RequestParam("id") Long id) {
        channelNotifyService.deleteChannel(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取通知渠道")
    public CommonResult<NotifyChannelDO> getChannel(@RequestParam("id") Long id) {
        return success(channelNotifyService.getChannel(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获取通知渠道分页")
    public CommonResult<PageResult<NotifyChannelDO>> getChannelPage(@Valid NotifyChannelPageReqVO reqVO) {
        return success(channelNotifyService.getChannelPage(reqVO));
    }

    @PostMapping("/test-send")
    @Operation(summary = "测试发送通知")
    public CommonResult<Boolean> testSend(@RequestParam("id") Long id) {
        NotifyChannelDO channel = channelNotifyService.getChannel(id);
        if (channel == null) {
            return CommonResult.error(400, "渠道不存在");
        }
        NotifyMessage message = NotifyMessage.info("测试通知", 
                "这是一条测试通知消息\n**渠道名称**: " + channel.getName() + "\n**时间**: " + java.time.LocalDateTime.now());
        notifySendService.send(message);
        return success(true);
    }
}
