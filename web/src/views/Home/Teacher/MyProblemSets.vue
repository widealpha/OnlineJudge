<template>
  <div class="MyproblemSets">
    <el-card>
      <!-- 题目集列表 -->
      <div slot="header">
        <el-row style="height: 5vh">
          <el-col :span="12" style="text-align: left; line-height: 5vh">
            <span>我的题目集</span>
          </el-col>
          <el-col :span="12" style="text-align: right; line-height: 5vh">
            <el-button @click="cloning = true" size="mini" type="primary" plain>
              克隆题目集
            </el-button>
            <el-button @click="adding = true" size="mini" type="primary">
              创建题目集
            </el-button>
          </el-col>
        </el-row>
      </div>
      <div :v-if="problemSets.length !== 0">
        <ul style="list-style: none" class="ul" v-loading="loading">
          <li v-for="(item, index) in problemSets" :key="index">
            <problem-set
              :name="item.name"
              :problemSetId="item.id"
              :type="item.type"
              :open="item.isPublic"
              :creatorId="item.creatorId"
              :beginTime="item.beginTime"
              :endTime="item.endTime"
            ></problem-set>
          </li>
        </ul>
      </div>
    </el-card>
    <!-- 克隆题目集 -->
    <el-dialog
      title="克隆题目集"
      :visible.sync="cloning"
      style="text-align: left"
    >
      <el-form
        label-position="left"
        :model="cloneForm"
        :rules="cloneRules"
        label-width="100px"
        ref="cloneForm"
      >
        <el-form-item label="题目id：" prop="problemSetId">
          <el-input
            v-model="cloneForm.problemSetId"
            placeholder="请输入要克隆的题目集id"
          ></el-input>
        </el-form-item>
        <el-form-item label="克隆码：" prop="cloneCode">
          <el-input
            v-model="cloneForm.cloneCode"
            placeholder="请输入该题目的克隆码"
          ></el-input>
        </el-form-item>
        <el-button type="primary" @click="cloneProblemSet" class="submit"
          >提交</el-button
        >
      </el-form>
    </el-dialog>
    <!-- 创建题目集 -->
    <div style="text-align: left">
      <el-dialog title="创建题目集" :visible.sync="adding">
        <el-form
          label-position="left"
          :model="infoForm"
          :rules="infoRules"
          label-width="100px"
          ref="infoForm"
        >
          <el-form-item label="名称" prop="name">
            <el-input
              v-model="infoForm.name"
              placeholder="请输入题目集名称"
            ></el-input>
          </el-form-item>
          <el-form-item label="题目集类型：">
            <el-select v-model="infoForm.type" placeholder="请选择题目集类型">
              <el-option
                v-for="item in typeOptions"
                :key="item.type"
                :label="item.label"
                :value="item.type"
              >
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="是否公开：">
            <el-switch
              v-model="infoForm.isPublic"
              active-text="公开"
              inactive-text="不公开"
              active-color="#13ce66"
              inactive-color="#ff4949"
              active-value="1"
              inactive-value="0"
            >
            </el-switch>
          </el-form-item>

          <el-form-item label="时间范围" prop="timeRange">
            <el-date-picker
              v-model="infoForm.timeRange"
              type="datetimerange"
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
            >
            </el-date-picker>
          </el-form-item>
          <el-form-item label="公告" prop="introduction">
            <el-input
              v-model="infoForm.introduction"
              type="textarea"
              placeholder="请输入题目集公告"
            ></el-input>
          </el-form-item>
        </el-form>
        <el-button type="primary" @click="createProblemSet" class="submit"
          >提交</el-button
        >
      </el-dialog>
    </div>
  </div>
</template>

<script>
import problemSet from "../../../components/ProblemSets/problemSet.vue";
export default {
  components: {
    problemSet,
  },
  data() {
    return {
      cloneForm: {},
      cloning: false,
      problemSets: [],
      adding: false,
      loading: false,
      infoForm: {
        timeRange: "",
        name: "",
        introduction: "",
        isPublic: 0,
        type: "",
      },
      typeOptions: [
        { type: 1, label: "练习" },
        { type: 2, label: "测验" },
        { type: 3, label: "竞赛" },
      ],
      cloneRules: {
        problemSetId: [
          {
            required: true,
            message: "请输入要复制的题目id",
            trigger: "blur",
          },
        ],
        cloneCode: [
          {
            required: true,
            message: "请输入该题目的克隆码",
            trigger: "blur",
          },
        ],
      },
      infoRules: {
        timeRange: [
          {
            required: true,
            message: "请选择题目集的开始时间与结束时间",
            trigger: "blur",
          },
        ],
        name: [
          {
            required: true,
            message: "请输入题目集的名称",
            trigger: "blur",
          },
        ],
        introduction: [
          {
            required: true,
            message: "请输入题目集的公告",
            trigger: "blur",
          },
        ],
        type: [
          {
            required: true,
            message: "请选择题目类型",
            trigger: "change",
          },
        ],
      },
    };
  },
  methods: {
    //克隆题目集
    async cloneProblemSet() {
      this.$refs.cloneForm.validate(async (valid) => {
        Number(this.cloneForm.problemSetId);
        if (valid) {
          let res = await this.$ajax.post(
            "/problemSet/cloneProblemSet",
            {
              problemSetId: Number(this.cloneForm.problemSetId),
              cloneCode: this.cloneForm.cloneCode,
            },
            {
              headers: {
                Authorization: `Bearer ${localStorage.getItem("token")}`,
              },
            }
          );

          if (res.data.code == 0) {
            this.$notify({
              title: "成功",
              message: "成功克隆题目集",
              type: "success",
            });
            await this.getSelfCreatedProblemSet();
            this.cloning = false;
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
    // 查看我创建的题目集
    async getSelfCreatedProblemSet() {
      this.loading = true;
      let res = await this.$ajax.post(
        "/problemSet/getSelfCreatedProblemSet",
        {},
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );

      if (res.data.code == 0) {
        this.problemSets = res.data.data;
      }
      this.loading = false;
    },
    createProblemSet() {
      this.$refs.infoForm.validate(async (valid) => {
        if (valid) {
          this.adding = false;
          //练习
          if (this.infoForm.type === 1) {
            let res = await this.$ajax.post(
              "/problemSet/createExerciseProblemSet",
              {
                name: this.infoForm.name,
                introduction: this.infoForm.introduction,
                type: this.infoForm.type,
                isPublic: this.infoForm.isPublic,
                beginTime: this.formatDate(this.infoForm.timeRange[0]),
                endTime: this.formatDate(this.infoForm.timeRange[1]),
              },
              {
                headers: {
                  Authorization: `Bearer ${localStorage.getItem("token")}`,
                },
              }
            );

            if (res.data.code == 0) {
              this.$message.success("创建成功");
            } else {
              this.$message.error("创建失败");
            }
          }
          //测验
          if (this.infoForm.type === 2) {
            let res = await this.$ajax.post(
              "/problemSet/createTestProblemSet",
              {
                name: this.infoForm.name,
                introduction: this.infoForm.introduction,
                type: this.infoForm.type,
                isPublic: this.infoForm.isPublic,
                beginTime: this.formatDate(this.infoForm.timeRange[0]),
                endTime: this.formatDate(this.infoForm.timeRange[1]),
              },
              {
                headers: {
                  Authorization: `Bearer ${localStorage.getItem("token")}`,
                },
              }
            );
            if (res.data.code == 0) {
              this.$message.success("创建成功");
            } else {
              this.$message.error("创建失败");
            }
          }
          //竞赛
          if (this.infoForm.type === 3) {
            let res = await this.$ajax.post(
              "/problemSet/createCompetitionProblemSet",
              {
                name: this.infoForm.name,
                teamNum: 20,
                competitionType: 0,
                introduction: this.infoForm.introduction,
                type: this.infoForm.type,
                isPublic: this.infoForm.isPublic,
                beginTime: this.formatDate(this.infoForm.timeRange[0]),
                endTime: this.formatDate(this.infoForm.timeRange[1]),
              },
              {
                headers: {
                  Authorization: `Bearer ${localStorage.getItem("token")}`,
                },
              }
            );
            if (res.data.code == 0) {
              this.$message.success("创建成功，正在导出学生名单");
              let res1 = await this.$ajax.post(
                "/problemSet/exportCompetitionAccounts",
                {
                  problemSetId: res.data.data.problemSetId,
                },
                {
                  headers: {
                    Authorization: `Bearer ${localStorage.getItem("token")}`,
                  },
                  responseType: "blob", // 以 blob 类型接收后端发回的响应数据
                }
              );

              const content = res1.data; // 接收响应内容
              const blob = new Blob([content]); // 构造一个blob对象来处理数据
              let fileName = `名单.xlsx`;
              // 对于<a>标签，只有 Firefox 和 Chrome（内核） 支持 download 属性
              // IE10以上支持blob但是依然不支持download
              if ("download" in document.createElement("a")) {
                //支持a标签download的浏览器
                const link = document.createElement("a"); // 创建a标签
                link.download = fileName; // a标签添加属性
                link.style.display = "none";
                link.href = URL.createObjectURL(blob);
                document.body.appendChild(link);
                link.click(); // 执行下载
                URL.revokeObjectURL(link.href); // 释放url
                document.body.removeChild(link); // 释放标签
              } else {
                // 其他浏览器
                navigator.msSaveBlob(blob, fileName);
              }
            } else {
              this.$message.error("创建失败");
            }
          }
          if (res.data.code == 0) {
            this.$notify({
              title: "成功",
              message: "成功创建题目集",
              type: "success",
            });
            this.getSelfCreatedProblemSet();
            this.adding = false;
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
  created() {
    if (!this.$store.state.myInfo.roles.includes("ROLE_TEACHER")) {
      this.$router.replace("/");
    } else {
      this.getSelfCreatedProblemSet();
    }
  },
};
</script>

<style lang="less">
.MyproblemSets {
  .ul li {
    margin-block: 20px;
  }
  .submit {
    display: block;
    margin: 0 auto !important;
  }
}
.el-dialog {
  display: flex;
  flex-direction: column;
  margin: 0 !important;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.el-dialog .el-dialog__body {
  flex: 1;
  overflow: auto;
}
</style>
