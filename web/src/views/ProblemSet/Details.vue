<template>
  <div>
    <!-- 题目集详情页面 -->
    <el-card>
      <div slot="header">
        <el-row style="height: 5vh">
          <el-col :span="12" style="text-align: left; line-height: 5vh">
            <span style="font-weight: bolder; color: gray">{{
              problemSetInfo.name
            }}</span>
          </el-col>
          <el-col :span="12" style="text-align: right; line-height: 5vh">
            <el-button
              v-if="problemSetInfo.isMyProblemSet"
              icon="el-icon-edit"
              style="float: right"
              @click="alteringInfo = true"
              >更新</el-button
            >
          </el-col>
        </el-row>
      </div>
      <div>
        <el-row>
          <el-col :span="12">
            <el-descriptions title="基本信息" :column="1">
              <el-descriptions-item label="作者id">
                <el-tag size="small">{{ problemSetInfo.creatorId }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="开始时间">
                {{ problemSetInfo.beginTime }}
              </el-descriptions-item>
              <el-descriptions-item label="结束时间">
                {{ problemSetInfo.endTime }}
              </el-descriptions-item>
              <el-descriptions-item label="题目集状态">
                <el-tag
                  size="small"
                  :type="problemSetStatus === '进行中' ? '' : 'info'"
                  >{{ problemSetStatus }}</el-tag
                >
              </el-descriptions-item>
            </el-descriptions>
          </el-col>
          <el-col :span="12">
            <el-card style="text-align: left">
              <div slot="header">
                <span style="font-weight: bolder; color: gray">题目集公告</span>
              </div>
              {{ problemSetInfo.introduction }}
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-card>
    <el-card style="margin-top: 10px">
      <div slot="header">
        <el-row style="height: 5vh">
          <el-col :span="12" style="text-align: left">
            <span style="font-weight: bolder; color: gray; line-height: 5vh"
              >题目列表</span
            >
          </el-col>
          <el-col :span="12" style="text-align: right">
            <el-button
              v-if="problemSetInfo.isMyProblemSet"
              style="float: right"
              @click="
                $router.push({
                  path: `/problem-set/${$route.params.problemSetId}/allProblems`,
                })
              "
              >题目管理</el-button
            >
          </el-col>
        </el-row>
      </div>
      <div>
        <el-table :data="problemSetInfo.problems">
          <el-table-column type="index" label="标号"></el-table-column>
          <el-table-column prop="id" label="题号"></el-table-column>
          <el-table-column prop="name" label="标题">
            <template slot-scope="props">
              <el-link
                type="primary"
                @click="getToProblem(props.$index + 1, props.row)"
                >{{ props.row.title }}</el-link
              >
            </template>
          </el-table-column>
          <el-table-column prop="score" label="分数"> </el-table-column>
        </el-table>
      </div>
    </el-card>
    <!-- 更新题目集信息 -->
    <div class="others">
      <el-dialog
        title="题目集概况"
        :append-to-body="true"
        :visible.sync="alteringInfo"
        @open="initInfoForm"
      >
        <el-form
          label-position="top"
          :model="infoForm"
          :rules="infoRules"
          ref="infoForm"
        >
          <el-form-item label="新名称" prop="name">
            <el-input v-model="infoForm.name" class="input"></el-input>
          </el-form-item>
          <el-form-item label=" 新时间范围" prop="timeRange">
            <el-date-picker
              style="width: 100%"
              v-model="infoForm.timeRange"
              type="datetimerange"
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
            >
            </el-date-picker>
          </el-form-item>

          <el-form-item label="新公告" prop="introduction">
            <el-input
              v-model="infoForm.introduction"
              type="textarea"
              class="input"
            ></el-input>
          </el-form-item>

          <el-form-item label="是否公开">
            <el-select v-model="infoForm.openValue">
              <el-option
                v-for="item in infoForm.open"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
        <el-button type="primary" @click="alterInfo">提交</el-button>
      </el-dialog>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      alteringInfo: false,
      infoForm: {
        timeRange: "",
        name: "",
        introduction: "",

        openValue: 0,

        open: [
          { value: 0, label: "非公开" },
          { value: 1, label: "公开" },
        ],
      },
      infoRules: {
        timeRange: [
          {
            required: true,
            message: "请选择题目集的开始时间与结束时间",
            trigger: ["blur", "change"],
          },
        ],
        name: [
          {
            required: true,
            message: "请输入题目集的名称",
            trigger: ["blur", "change"],
          },
        ],
        introduction: [
          {
            required: true,
            message: "请输入题目集的公告",
            trigger: ["blur", "change"],
          },
        ],
        status: [
          {
            required: true,
            message: "请选择题目集状态",
            trigger: ["blur", "change"],
          },
        ],
      },
      statuses: [
        { label: "完全公开", value: 1 },
        { label: "教师间公开", value: 2 },
        { label: "私有", value: 3 },
      ],
    };
  },
  methods: {
    //进入问题详情
    getToProblem(index, problem) {
      this.$router.push({
        path: `/problem-set/${this.$route.params.problemSetId}/problems/${problem.id}`,
      });
    },
    changeIndex(index) {
      this.menuIndex = index;
    },
    //更新题目集信息之前初始化
    async initInfoForm() {
      this.infoForm.name = this.problemSetInfo.title;
      this.infoForm.introduction = this.problemSetInfo.introduction;
      this.infoForm.timeRange = [
        new Date(this.problemSetInfo.beginTime),
        new Date(this.problemSetInfo.endTime),
      ];

      this.infoForm.openValue = this.problemSetInfo.open;
    },
    // 修改题目集信息
    alterInfo() {
      this.$refs.infoForm.validate(async (valid) => {
        if (valid) {
          const request = {
            id: this.$route.params.problemSetId,
            name: this.infoForm.name,
            introduction: this.infoForm.introduction,
            beginTime: this.formatDate(this.infoForm.timeRange[0]),
            endTime: this.formatDate(this.infoForm.timeRange[1]),
            isPublic: this.infoForm.openValue,
          };

          let res = await this.$ajax.post(
            "/problemSet/alterProblemSetInfo",
            request,
            {
              headers: {
                Authorization: `Bearer ${this.$store.state.token}`,
              },
            }
          );

          if (res.data.code == 0) {
            this.$notify({
              title: "成功",
              message: "题目集信息修改成功",
              type: "success",
            });
            this.$store.commit("setProblemSetInfo", {
              name: this.infoForm.name,
              introduction: this.infoForm.introduction,
              beginTime: this.formatDate(this.infoForm.timeRange[0]),
              endTime: this.formatDate(this.infoForm.timeRange[1]),
              open: this.infoForm.openValue,
            });
            this.alteringInfo = false;
          } else {
            this.$notify({
              title: "失败",
              message: `${res.data.message}`,
              type: "error",
            });
          }
        }
      });
    },
    formatDate(date) {
      let year = date.getFullYear();
      let month = date.getMonth() + 1;
      let day = date.getDate();
      let hour = date.getHours();
      let min = date.getMinutes();
      let sec = date.getSeconds();
      return (
        year +
        "-" +
        (month < 10 ? "0" + month : month) +
        "-" +
        (day < 10 ? "0" + day : day) +
        " " +
        (hour < 10 ? "0" + hour : hour) +
        ":" +
        (min < 10 ? "0" + min : min) +
        ":" +
        (sec < 10 ? "0" + sec : sec)
      );
    },
  },
  computed: {
    problemSetInfo() {
      console.log(this.$store.state.problemSetInfo);
      return this.$store.state.problemSetInfo;
    },
    isMyProblemSet() {
      return this.$store.state.problemSetInfo.isMyProblemSet;
    },
    problemSetStatus() {
      let beginTime = new Date(this.problemSetInfo.beginTime);
      let endTime = new Date(this.problemSetInfo.endTime);
      let nowTime = new Date();
      if (nowTime >= endTime) {
        return "已关闭";
      } else if (nowTime <= beginTime) {
        return "未开始";
      } else {
        return "进行中";
      }
    },
  },
};
</script>