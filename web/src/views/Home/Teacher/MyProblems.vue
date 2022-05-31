<template>
  <div>
    <div class="head" style="height: 5vh">
      <el-pagination
        :total="total"
        layout="prev,total, pager, next, jumper, sizes, slot"
        :current-page.sync="page"
        :page-size.sync="size"
        :page-sizes="[10, 20, 50, 100]"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      >
        <el-button type="text" @click="search()"
          >根据id查找<i class="el-icon-search"></i
        ></el-button>
      </el-pagination>
    </div>
    <el-table
      v-loading="loading"
      :data="problemsList"
      style="width: 100%"
      border
      stripe
    >
      <el-table-column type="expand" width="50">
        <template slot-scope="props">
          <el-card>
            <div slot="header">题目描述</div>
            <mavon-editor
              :value="props.row.description"
              :toolbarsFlag="false"
              :editable="false"
              :subfield="false"
              :defaultOpen="'preview'"
              fontSize="16px"
            />
          </el-card>
        </template>
      </el-table-column>
      <el-table-column prop="id" label="题目ID"> </el-table-column>
      <el-table-column prop="name" label="标题" :show-overflow-tooltip="true">
      </el-table-column>
      <el-table-column prop="creator" label="作者ID"> </el-table-column>
      <el-table-column prop="difficulty" label="难度"> </el-table-column>
      <el-table-column prop="modifiedTime" label="最后修改时间">
      </el-table-column>
      <el-table-column prop="typeName" label="类别"> </el-table-column>
      <el-table-column prop="passRadio" label="通过率">
        <template slot-scope="props">
          {{ Number(props.row.passRadio * 100).toFixed(1) + "%" }}
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button
            type="text"
            size="small"
            style="color: red"
            @click="remove(scope.$index)"
          >
            移除
          </el-button>
          <el-button type="text" size="small" @click="update(scope.row.id)">
            更新
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-backtop></el-backtop>
  </div>
</template>

<script>
import Vue from "vue";
import mavonEditor from "mavon-editor";
import "mavon-editor/dist/css/index.css";
Vue.use(mavonEditor);
export default {
  data() {
    return {
      size: 10,
      page: 1,
      total: 0,
      problems: [],
      problemsList: [],
      loading: false,
    };
  },
  methods: {
    // 具体分页操作
    pageList() {
      this.problemsList = this.problems.filter(
        (item, index) =>
          index < this.page * this.size && index >= this.size * (this.page - 1)
      );
      this.total = this.problems.length;
    },

    //处理切换页码
    handleSizeChange(val) {
      console.log(`每页 ${val} 条`);
      this.size = val;
      this.pageList();
    },
    handleCurrentChange(val) {
      console.log(`当前页: ${val}`);
      this.page = val;
      this.pageList();
    },

    search() {
      this.$prompt("查找", "请输入所需查询的题目id", {
        confirmButtonText: "查找",
        cancelButtonText: "取消",
        inputValidator: (value) => {
          // 点击按钮时，对文本框里面的值进行验证
          if (isNaN(value)) {
            return "仅允许输入数字";
          }
        },
      }).then(async ({ value }) => {
        let res = await this.$ajax.post(
          "/problem/getProblemById",
          {
            id: value,
          },
          {
            headers: {
              Authorization: `Bearer ${this.$store.state.token}`,
            },
          }
        );
        if (res.data.code === 0 && res.data.message === "ok") {
          this.isUpdating = true;
          this.initForm(res.data.data);
        }
      });
    },
    async remove(index) {
      let res = await this.$ajax.post(
        "/problem/deleteAProblem",
        {
          problemId: this.problems[index].id,
        },
        {
          headers: {
            Authorization: `Bearer ${this.$store.state.token}`,
          },
        }
      );
      if (res.data.message == "success") {
        this.$message({
          message: "成功移除",
          type: "success",
          duration: 1000,
        });
      }
      this.problems.splice(index, 1);
    },
    update(problemId) {
      problemId;
      this.$router.push({
        name: "newProblem",
        query: { problemId: problemId },
        params: { step: 1 },
      });
    },
  },
  // 身份校验
  beforeCreate() {
    if (
      this.$store.state.token === null ||
      !this.$store.state.myInfo.roles.includes("ROLE_TEACHER")
    ) {
      this.$router.replace("/");
    }
  },
  //获取所有我创建的题目
  async created() {
    if (!this.$store.state.myInfo.roles.includes("ROLE_TEACHER")) {
      this.$router.replace("/");
    } else {
      this.loading = true;
      let res = await this.$ajax.post(
        "/problem/allMyCreateProblems",
        {},
        {
          headers: {
            Authorization: `Bearer ${this.$store.state.token}`,
          },
        }
      );

      this.problems = res.data.data;
      this.total = res.data.data.length;
      this.pageList();
      this.loading = false;
    }
  },
};
</script>