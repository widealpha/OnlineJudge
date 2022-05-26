<template>
  <div class="list">
    <el-container>
      <link
        href="//at.alicdn.com/t/font_2807167_dodqs5cvnm7.css"
        rel="stylesheet"
      />
      <el-main>
        <el-button
          v-if="isTeacher"
          type="primary"
          @click="showAddUserList = true"
        >
          添加用户组
        </el-button>
        <el-table
          v-loading="loading"
          :data="userList"
          border
          stripe
          style="width: 100%"
        >
          <el-table-column
            fixed
            label="名称"
            prop="name"
            show-overflow-tooltip
            width="150"
          >
          </el-table-column>
          <el-table-column
            fixed
            label="描述"
            prop="introduce"
            show-overflow-tooltip
          >
          </el-table-column>
          <el-table-column
            fixed
            label="人数"
            prop="num"
            show-overflow-tooltip
            width="100"
          >
          </el-table-column>
          <el-table-column
            fixed
            label="类型"
            prop="type"
            show-overflow-tooltip
            width="100"
          >
          </el-table-column>
          <el-table-column
            fixed
            label="创建时间"
            prop="create_time"
            show-overflow-tooltip
            width="200"
          >
          </el-table-column>
          <el-table-column
            v-if="!isTeacher"
            fixed
            label="是否绑定"
            min-width="100"
            prop="bind"
            show-overflow-tooltip
          >
            <template slot-scope="scope" style="text-align: center">
              <span
                v-if="scope.row.bind === 0"
                style="cursor: pointer"
                @click="bindToUserList(scope.row.id)"
              >
                <i
                  class="iconfont icon-click"
                  style="color: #179cff; font-size: 25px"
                />
                点击绑定
              </span>
              <span v-if="scope.row.bind === 1">
                <i class="iconfont icon-pass-copy trueStateIcon" />
                已绑定
              </span>
            </template>
          </el-table-column>
          <el-table-column v-if="isTeacher" label="操作" width="150">
            <template slot-scope="scope">
              <el-button
                size="small"
                type="text"
                @click="handleClick(scope.row)"
                >查看</el-button
              >
              <el-button
                size="small"
                type="text"
								style="color: #ff4d4f"
                @click.native.prevent="deleteRow(scope.$index)"
              >
                移除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-main>
    </el-container>
    <el-dialog :visible.sync="comfirmDel" title="确认删除">
      <el-input v-model="userListName" placeholder="请输入该用户组名称">
      </el-input>
      <div class="deleteBtn" @click="checkDel">确认删除</div>
    </el-dialog>
    <div v-show="showAddUserList" class="add">
      <el-dialog :visible.sync="showAddUserList" title="添加用户组">
        <el-input v-model="createUserListName" placeholder="请输入用户组名称">
        </el-input>
        <el-input v-model="createUserListType" placeholder="请输入用户组类别">
        </el-input>
        <el-input v-model="createUserListDesc" placeholder="请输入用户组描述">
        </el-input>
        <el-button
          style="margin-left: 10px"
          type="primary"
          @click="addUserList"
        >
          添加用户组
        </el-button>
      </el-dialog>
    </div>
  </div>
</template>

<script>
export default {
  name: "userList",
  components: {},
  data() {
    return {
      createUserListName: "",
      createUserListDesc: "",
      createUserListType: "",
      comfirmDel: false,
      userListName: "",
      showAddUserList: false,
      userList: [],
      loading: false,
      index: null,
    };
  },
  async mounted() {
    await this.getUserList();
  },
  created() {
    if (this.$store.state.token === null) {
      this.$router.replace("/");
    }
  },
  methods: {
    async bindToUserList(id) {
      let res = await this.$ajax.post(
        "/usergroup/bindStudent",
        {
          id: id,
        },
        {
          headers: {
            Authorization: `Bearer ${this.$store.state.token}`,
          },
        }
      );
      console.log(res);
      await this.getUserList();
    },
    async addUserList() {
      if (
        this.createUserListName.trim() &&
        this.createUserListType.trim() &&
        this.createUserListDesc.trim()
      ) {
        let res = await this.$ajax.post(
          "/userGroup/createUserGroup",
          {
            name: this.createUserListName,
            type: this.createUserListType,
            introduction: this.createUserListDesc,
            fatherId: null,
          },
          {
            headers: {
              Authorization: `Bearer ${this.$store.state.token}`,
            },
          }
        );
        console.log(res);
        if (res.data.code === 0) {
          this.showAddUserList = false;
          this.$message.success("创建用户组成功!");
          await this.getUserList();
          this.createUserListName = "";
          this.createUserListType = "";
          this.createUserListDesc = "";
        } else {
          this.$message.error("创建用户组失败," + res.data.message);
        }
      } else {
        this.$message.error("用户组信息不得为空");
      }
    },
    handleClick(row) {
      this.$router.push({
        name: "userListDetails",
        query: { userid: row.id },
      });
    },
    async checkDel() {
      let userListName = this.userListName;
      // 检查
      if (
        !userListName ||
        !userListName.trim() ||
        userListName !== this.userList[this.index].name
      ) {
        this.$message.error("分组名称不一致");
      } else {
        let res = await this.$ajax.post(
          "/userGroup/deleteUserGroup",
          {
            id: this.userList[this.index].id,
          },
          {
            headers: {
              Authorization: `Bearer ${this.$store.state.token}`,
            },
          }
        );
        if (res.data.code === 0) {
          this.$message.success(res.data.message);
          this.userList.splice(this.index, 1);
          this.comfirmDel = false;
        } else {
          this.$message.error(res.data.message);
        }
      }
      //
      this.userListName = "";
    },
    deleteRow(index) {
      this.comfirmDel = true;
      this.index = index;
    },
    // 学生查看用户组列表
    async getSelfJoinedUserList() {
      let res = await this.$ajax.post(
        "/userGroup/getSelfJoinedUserGroup",
        {},
        {
          headers: {
            Authorization: `Bearer ${this.$store.state.token}`,
          },
        }
      );
      console.log(res);
      if (res.status === 200) {
        if (res.data.code === 0) {
          this.userList = res.data.data;
        } else {
          this.$message.error(res.data.message);
        }
      } else {
        this.$message.error("获取用户列表失败");
      }
    },
    // 教师查看用户组列表
    async getSelfCreatedUserList() {
      let res = await this.$ajax.post(
        "/userGroup/getSelfCreatedUserGroup",
        {},
        {
          headers: {
            Authorization: `Bearer ${this.$store.state.token}`,
          },
        }
      );
      console.log(res);
      if (res.status === 200) {
        if (res.data.code === 0) {
          this.userList = res.data.data;
        } else {
          this.$message.error(res.data.message);
        }
      } else {
        this.$message.error("获取用户列表失败");
      }
    },
    // 获取用户列表
    async getUserList() {
      this.loading = true;
      this.$store.state.myInfo.roles.includes("ROLE_TEACHER")
        ? await this.getSelfCreatedUserList()
        : await this.getSelfJoinedUserList();

      this.loading = false;
    },
  },
  computed: {
    isTeacher() {
      return this.$store.state.myInfo.roles.includes("ROLE_TEACHER");
    },
  },
};
</script>

<style lang="less" scoped>
.list {
  .add {
    /deep/ .el-dialog {
      height: 400px;
      box-sizing: border-box;
      padding: 20px;
    }
  }

  /deep/ .el-input {
    margin: 10px 20px 10px 10px;
  }

  /deep/ .el-button {
    margin: 10px 10px 10px 0;
    float: left;
  }

  .trueStateIcon {
    color: #2d9b25;
    margin-right: 1px;
    font-size: 20px;
  }

  .falseStateIcon {
    color: red;
    margin-right: 1px;
    font-size: 20px;
  }

  .deleteBtn {
    width: 200px;
    height: 50px;
    color: #fff;
    line-height: 50px;
    cursor: pointer;
    text-align: center;
    margin: 10px auto;
    background: #179cff;
    border-radius: 10px;
  }
}
</style>
