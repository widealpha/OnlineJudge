<template>
  <el-card style="text-align: left; color: #4f4f4f" shadow="hover">
    <!-- 题目集列表的小卡片 -->
    <div slot="header">
      <span style="height: 100%">
        <i
          class="el-icon-collection"
          style="
            color: dodgerblue;
            vertical-align: middle;
            margin-right: 5px;
            font-size: 130%;
          "
        ></i
        ><el-link
          type="primary"
          style="vertical-align: middle; font-weight: bold; font-size: 101%"
          @click="toCertainProblemSet"
          >{{ name }}</el-link
        >
      </span>

      <el-tag
        style="float: right"
        effect="dark"
        :type="getStatus() == '进行中' ? '' : 'info'"
      >
        {{ getStatus() }}
      </el-tag>
      <el-tag
        style="float: right; margin-inline: 10px"
        effect="dark"
        :type="open == 1 ? 'success' : 'warning'"
      >
        {{ getPublic() }}
      </el-tag>

      <el-tag
        v-if="this.type != 0"
        style="float: right; margin-inline: 10px"
        effect="dark"
        :type="type == 1 ? 'success' : type == 2 ? '' : 'warning'"
      >
        {{ getType() }}
      </el-tag>
    </div>
    <div>
      <span>结束时间：{{ endTime }}</span>
      <span style="float: right; margin-left: 20px; vertical-align: middle"
        ><i
          class="el-icon-user"
          style="margin-right: 3px; vertical-align: middle; color: #409eff"
        ></i
        >作者id：<span style="color: #409eff; vertical-align: middle">{{
          creatorId
        }}</span></span
      >
      <span style="float: right; vertical-align: middle"
        ><i
          class="el-icon-price-tag"
          style="margin-right: 3px; vertical-align: middle; color: #409eff"
        ></i
        >题目集id：<span style="color: #409eff; vertical-align: middle">{{
          problemSetId
        }}</span></span
      >
    </div>
  </el-card>
</template>

<script>
export default {
  name: "problemSet",
  props: {
    // 创建者id
    creatorId: {
      type: Number,
      default: 0,
    },
    name: {
      type: String,
      default: "题目集标题",
    },
    beginTime: {
      type: String,
      default: "1970-01-01 00:00",
    },
    endTime: {
      type: String,
      default: "1970-01-01 00:00",
    },
    problemSetId: {
      type: Number,
      default: 0,
    },
    // 1练习 2测验 3竞赛
    type: {
      type: Number,
      default: 0,
    },
    // 是否公开
    open: Number,
  },
  methods: {
    getStatus() {
      let beginTime = new Date(this.beginTime);
      let endTime = new Date(this.endTime);
      let nowTime = new Date();
      if (nowTime >= endTime) {
        return "已关闭";
      } else if (nowTime <= beginTime) {
        return "未开始";
      } else {
        return "进行中";
      }
    },
    getPublic() {
      return this.open == 1 ? "公开" : "非公开";
    },

    getType() {
      switch (this.type) {
        case 1:
          return "练习";
        case 2:
          return "测验";
        case 3:
          return "竞赛";
      }
    },
    toCertainProblemSet() {
      this.$router.push({
        path: `/problem-set/${this.problemSetId}`,
      });
    },
  },
};
</script>