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
      :header-cell-style="{ 'text-align': 'center' }"
      style="width: 100%"
      stripe
    >
      <el-table-column prop="id" label="题目ID" width="70" align="center"> </el-table-column>
      <el-table-column prop="name" label="标题" :show-overflow-tooltip="true" align="center">
      </el-table-column>
      <el-table-column prop="creator" label="作者ID" width="70" align="center">
      </el-table-column>
      <el-table-column prop="difficulty" label="难度" width="70" align="center">
      </el-table-column>
      <el-table-column prop="modifiedTime" label="最后修改时间" align="center">
        <template slot-scope="scope">
          <i class="el-icon-time"></i>
          <span style="margin-left: 10px">{{ scope.row.modifiedTime }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="typeName" label="类别" width="100" align="center">
        <template slot-scope="scope">
          <div slot="reference">
            <el-tag
              size="medium"
              effect="dark"
              :type="
                scope.row.typeName == '编程题'
                  ? 'success'
                  : scope.row.typeName == '选择题'
                  ? ''
                  : 'warning'
              "
              >{{ scope.row.typeName }}</el-tag
            >
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="passRate" label="通过率" width="70" align="center">
        <template slot-scope="props">
          {{ Number(props.row.passRate * 100).toFixed(1) + "%" }}
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        align="right"
     
      >
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="primary"
            plain
            @click="update(scope.row.id)"
          >
            更新
          </el-button>
          <el-button size="mini" type="danger" @click="remove(scope.$index)" style="margin-right:20px">
            移除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-backtop></el-backtop>
  </div>
</template>

<script>
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
       (`每页 ${val} 条`);
      this.size = val;
      this.pageList();
    },
    handleCurrentChange(val) {
       (`当前页: ${val}`);
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
          "/problem/info",
          {
            problemId: value,
          },
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          }
        );
         (res);
        if (res.data.code === 0) {
          this.problemsList = [res.data.data];
        }
      });
    },
    async remove(index) {
      let res = await this.$ajax.post(
        "/problem/deleteProblem",
        {
          problemId: this.problemsList[index].id,
        },
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );
      if (res.data.message == "success") {
        await this.getAllMyCreateProblems();
        this.$message({
          message: "成功移除",
          type: "success",
          duration: 1000,
        });
      }
    },
    update(problemId) {
      this.$router.push({
        name: "newProblem",
        query: { problemId: problemId },
        params: { step: 1 },
      });
    },
    async getAllMyCreateProblems() {
      this.loading = true;
      let res = await this.$ajax.post(
        "/problem/allMyCreateProblems",
        {},
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );

      this.problems = res.data.data;
      this.total = res.data.data.length;
      this.pageList();
      this.loading = false;
    },
  },
  // 身份校验
  beforeCreate() {
    if (
      localStorage.getItem("token") === null ||
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
      await this.getAllMyCreateProblems();
    }
  },
};
</script>