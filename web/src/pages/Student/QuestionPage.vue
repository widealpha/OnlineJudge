<template>
  <div class="questionpage">
    <el-card>
      <div slot="header" style="text-align: center">
        <span id="back" @click="backToProblems"
          ><i class="el-icon-arrow-left"></i>返回题目列表</span
        >
        <span style="font-weight: bolder; color: gray">{{ title }}</span>
      </div>
      <div>
        <el-col :span="24">
          <div class="questiondetails">
            <question-details
              :problemId.sync="problemId"
              :title.sync="title"
              ref="questionDetails"
            ></question-details>
          </div>
          <el-divider><i class="el-icon-monitor"></i></el-divider>
          <div class="input" style="margin-top: 20px">
            <judge-input :problemId="problemId"></judge-input>
          </div>
        </el-col>
      </div>
    </el-card>
  </div>
</template>

<script>
import JudgeInput from "../../components/solveProblems/judgeInput.vue";
import QuestionDetails from "../../components/solveProblems/questionDetails.vue";
export default {
  name: "QuestionPage",
  components: {
    JudgeInput,
    QuestionDetails,
  },
  data() {
    return {
      problemId: 0,
      title: "标题",
    };
  },
  methods: {
    backToProblems() {
      this.$router.push({ path: "/student/problems" });
    },
  },
  beforeCreate() {
    if (localStorage.getItem("token") === null) {
      this.$router.replace("/");
    }
  },
  created() {
    this.problemId = Number(this.$route.params.problemId);
    this.$nextTick(() => {
      this.$refs.questionDetails.getDetails(this.problemId);
    });
  },
  watch: {
    $route(to) {
      if (to.params.problemId) {
        this.problemId = Number(to.params.problemId);
        this.$refs.questionDetails.getDetails(this.problemId);
      }
    },
  },
};
</script>

<style lang="less" scoped>
#back {
  float: left;
}
#back:hover {
  color: dodgerblue;
  cursor: pointer;
}
</style>
