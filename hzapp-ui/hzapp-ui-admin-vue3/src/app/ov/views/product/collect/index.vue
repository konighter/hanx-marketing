<script setup lang="ts">
import {createCollectTask} from "@/app/ov/api/collect";


const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗
const collectForm = ref({
  collectType: 'byLink',
  collectPlatform: '',
  links: '',
  keywords: ''
})

const clearTab = function () {
  collectForm.value.links = ''
  collectForm.value.keywords = ''
}

const submitCollect = async function () {
  await createCollectTask(collectForm.value)
  message.success('采集任务创建成功')
}


</script>

<template>
  <ContentWrap>
    <div>
      <el-tabs v-model="collectForm.collectType" @tabClick="clearTab">
        <el-tab-pane label="链接采集" name="byLink" >
          <el-input type="textarea" rows="10" placeholder="请输入链接,每行一个链接"
                    v-model="collectForm.links" />

        </el-tab-pane>
        <el-tab-pane label="关键字采集" name="byKeyword">
          <el-input type="textarea" rows="10" placeholder="请输入链接,每行一个关键字"
                    v-model="collectForm.keywords" />
        </el-tab-pane>
      </el-tabs>

      <el-radio-group
          v-model="collectForm.collectPlatform">
        <el-radio-button value="1688">
          <Icon class="mr-5px" icon="ep:plus" />
        </el-radio-button>
        <el-radio-button value="1699">
          <Icon class="mr-5px" icon="ep:plus" />
        </el-radio-button>
      </el-radio-group>

      <div class="fa-align-center">
        <el-button class="is-align-center" type="primary" @click="submitCollect">开始采集</el-button>
      </div>

    </div>


  </ContentWrap>
</template>

<style scoped lang="scss">

</style>
