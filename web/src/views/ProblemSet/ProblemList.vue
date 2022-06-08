<template>
  <div>
    <el-card>
      <el-button
        type="primary"
        v-if="this.$store.state.myInfo.roles.includes(`ROLE_TEACHER`)"
        @click="openDialog = true"
        style="float:left;margin-bottom:30px"
        >添加题目</el-button
      ><div style="margin:20px 0">题目列表</div>
      <el-table v-loading="loading" :data="problems" style="width=100%">
        <el-table-column label="id" prop="id" />
        <el-table-column label="标题" prop="name" />
        <el-table-column label="类型" prop="typeName" />
        <el-table-column label="难度" prop="difficultyName" />
        <el-table-column label="操作" prop="difficultyName">
          <template slot-scope="scope">
            <el-button
              @click="goToProblems(scope.row.id)"
              type="primary"
              size="small"
              >做题</el-button
            >
            <el-button
              @click="deleteProblems(scope.row.id)"
              type="danger"
              size="small"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog
      title="添加题目"
      :visible.sync="openDialog"
      style="text-align:left"
    >
      <el-input
        v-model="addingProblemId"
        @input="checkIfNull"
        placeholder="请输入要加入的题目id"
      ></el-input>
      <div slot="footer">
        <el-button @click="openDialog = false">取 消</el-button>
        <el-button type="primary" :disabled="noProblemId" @click="addProblem"
          >确 定</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      loading: false,
      problems: [],
      noProblemId: true,
      addingProblemId: "",
      openDialog: false,
    };
  },
  methods: {
    async deleteProblems(id) {
      this.loading = true;
      const res = await this.$ajax.post(
        "/problem/deleteProblem",
        {
          problemId: id,
        },
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );
      this.loading = false;
      if (res.data.code === 0) {
        this.$message.success("删除成功");
        this.getProblemset();
      } else {
        this.$message.error("删除失败");
      }
    },

    async addProblem() {
      this.loading = true;
      this.openDialog = false;
      let res = await this.$ajax.post(
        "/problemSet/addProblemToProblemSet",
        {
          problemSetId: this.$store.state.problemSetInfo.id,
          problemId: Number(this.addingProblemId),
        },
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );

      if (res.data.code == 0) {
        await this.getProblemset();
      } else {
        this.$message.error("添加失败");
      }
      this.loading = false;
    },
    // 获取到所有的题目集信息
    async getProblemset() {
      let res = await this.$ajax.post(
        "/problemSet/getProblemSetInfo",
        {
          problemSetId: this.$store.state.problemSetInfo.id,
        },
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );

      if (res.data.code == 0) {
        
        const data = res.data.data;
        console.log(data);
        const title = data.name;
        const introduction = data.introduction;
        const creatorId = data.creatorId;
        const open = data.isPublic;
        const endTime = data.endTime;
        const beginTime = data.beginTime;
        const problemDtos = data.problemDtos;
        const type = data.type;
        const isMyProblemSet = creatorId == this.$store.state.myInfo.userId;
        await this.$store.commit("setProblemSetInfo", {
          title,
          introduction,
          creatorId,
          open,
          endTime,
          beginTime,
          problemDtos,
          type,
          isMyProblemSet,
        });
        this.problems = data.problemDtos;
      }
    },
    checkIfNull() {
      if (this.addingProblemId == "") {
        this.noProblemId = true;
      } else {
        this.noProblemId = false;
      }
    },
    goToProblems(id) {
      this.$router.push({ name: "Question", params: { problemId: id } });
    },
  },
  async created() {
    await this.getProblemset();
    this.problems = this.$store.state.problemSetInfo.problemDtos;
    console.log(this.$store.state.problemSetInfo.problemDtos);
  },
};
</script>
