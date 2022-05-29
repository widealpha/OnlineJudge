<template>
  <div class="userListDetails">
    <el-container>
      <el-main>
        <div class="detailsCard">
          <el-breadcrumb
            separator-class="el-icon-arrow-right"
            style="margin-bottom: 25px; font-size: 15px"
          >
            <el-breadcrumb-item :to="{ path: '/teacher/userList' }"
              >用户组列表</el-breadcrumb-item
            >
            <el-breadcrumb-item>用户组详情</el-breadcrumb-item>
          </el-breadcrumb>
          <div class="basicData">
            <div class="row">
              <div class="infoItem">
                <div class="title">名称:</div>
                <div class="input">
                  <el-input
                    v-model="info.name"
                    style="width: 100%"
                    @blur="changeInfo"
                  >
                  </el-input>
                </div>
              </div>
              <div class="infoItem">
                <div class="title" style="width: 80px">创建者:</div>
                <div class="input">
                  <el-input v-model="creatorName" disabled style="width: 100%">
                  </el-input>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="infoItem">
                <div class="title">人数:</div>
                <div class="input">
                  <el-input
                    v-model="stuList.length"
                    disabled
                    style="width: 100%"
                  >
                  </el-input>
                </div>
              </div>
              <div class="infoItem">
                <div class="title" style="width: 80px">类型:</div>
                <div class="input">
                  <el-input v-model="info.type" disabled style="width: 100%">
                  </el-input>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="infoItem">
                <div class="title">类别:</div>
                <div class="input">
                  <el-input v-model="info.type" disabled style="width: 100%">
                  </el-input>
                </div>
              </div>
              <div class="infoItem">
                <div class="title" style="width: 80px">描述:</div>
                <div class="input">
                  <el-input
                    v-model="info.introduction"
                    style="width: 100%"
                    @blur="changeInfo"
                  >
                  </el-input>
                </div>
              </div>
            </div>
          </div>
          <div class="studentList">
            <div class="stuTitle">学生列表:</div>
            <div class="tableDiv">
              <el-button
                style="display: block; margin-bottom: 15px"
                type="primary"
                @click="showImportDialog = true"
                >批量导入
              </el-button>

              <el-table
                v-loading="loading"
                :data="stuList.slice(start, end)"
                border
                class="stuTable"
                stripe
              >
                <el-table-column
                  fixed
                  label="学号"
                  min-width="180"
                  prop="stuNum"
                  show-overflow-tooltip
                >
                </el-table-column>
                <el-table-column
                  fixed
                  label="姓名"
                  min-width="100"
                  prop="name"
                  show-overflow-tooltip
                >
                </el-table-column>
                <!--                state表示学生是否绑定或者管理员是否导入该学生的学号-->
                <el-table-column
                  fixed
                  label="是否绑定"
                  min-width="100"
                  prop="state"
                  show-overflow-tooltip
                >
                  <template slot-scope="scope" style="text-align: center">
                    <span v-if="!scope.row.state">
                      <i class="iconfont icon-Shapex-copy falseStateIcon" />
                      未绑定
                    </span>
                    <span v-if="scope.row.state">
                      <i class="iconfont icon-pass-copy trueStateIcon" />
                      已绑定
                    </span>
                  </template>
                </el-table-column>
                <!--                status表示学生学生是否加入-->
                <el-table-column
                  fixed
                  label="是否加入"
                  min-width="100"
                  prop="status"
                  show-overflow-tooltip
                >
                  <template slot-scope="scope" style="text-align: center">
                    <span v-if="!scope.row.status">
                      <i class="iconfont icon-Shapex-copy falseStateIcon" />
                      未绑定
                    </span>
                    <span v-if="scope.row.status">
                      <i class="iconfont icon-pass-copy trueStateIcon" />
                      已绑定
                    </span>
                  </template>
                </el-table-column>
                <el-table-column
                  fixed
                  label="操作"
                  min-width="100"
                  prop="opeartion"
                  show-overflow-tooltip
                >
                  <template slot-scope="scope">
                    <el-button
                      size="small"
                      type="text"
                      @click.native.prevent="deleteStu(scope.$index, stuList)"
                    >
                      移除
                    </el-button>
                  </template>
                </el-table-column>
                <!--<i class="iconfont icon-Shapex" />-->
              </el-table>
            </div>
          </div>
          <el-pagination
            :page-size="pageSize"
            :total="stuList.length"
            layout="prev, pager, next"
            style="display: block; margin-top: 10px"
            @current-change="changePage"
          >
          </el-pagination>
        </div>
      </el-main>
    </el-container>
    <el-dialog :visible.sync="showImportDialog" title="导入成员">
      <excel :upload="upload" />
    </el-dialog>
  </div>
</template>

<script>
import excel from "../../components/excelImport";

export default {
  name: "userListDetails",
  components: {
    excel,
  },
  created() {
    if (
      this.$store.state.token === null ||
      !this.$store.state.myInfo.roles.includes("ROLE_TEACHER")
    ) {
      this.$router.replace("/");
    }
  },
  data() {
    return {
      id: null,
      creatorName: "",
      start: 0,
      pageSize: 4,
      loading: false,
      showImportDialog: false,
      end: null,
      info: {},
      stuList: [],
    };
  },
  async mounted() {
    this.getId();
    await this.getInfo();
    await this.getUserInfoById();
    await this.getStuList();
    this.end =
      this.stuList.length > this.pageSize ? this.pageSize : this.stuList.length;
  },
  methods: {
    async getUserInfoById() {
      let res = await this.$ajax.post(
        "/user/minorInfo",
        { userId: this.info.creatorId },
        {
          headers: {
            Authorization: `Bearer ${this.$store.state.token}`,
          },
        }
      );
      if (res.data.code == 0) {
        this.creatorName = res.data.data.username;
      } else {
        this.$message.error("获取创建者信息失败");
      }
    },
    async getStuList() {
      let res = await this.$ajax.post(
        "/userGroup/getUserGroupMembers",
        {
          id: this.id,
        },
        {
          headers: {
            Authorization: `Bearer ${this.$store.state.token}`,
          },
        }
      );
      if (res.data.code == 0) {
        this.stuList = res.data.data;
      } else {
        this.$message.error("获取学生列表失败");
      }
    },

    async getInfo() {
      let res = await this.$ajax.post(
        "/userGroup/getUserGroupInfo",
        {
          id: this.id,
        },
        {
          headers: {
            Authorization: `Bearer ${this.$store.state.token}`,
          },
        }
      );
      let data = res.data.data;
      //  (data);
      if (res.data.code === 0) {
        this.info = data;
      } else {
        this.$message.error("获取用户组信息失败");
      }
    },
    async upload(data) {
      data.append("id", this.id);
      let res = await this.$ajax.post("/usergroup/addStudent", data, {
        headers: {
          Authorization: `Bearer ${this.$store.state.token}`,
          "Content-Type": "multipart/form-data",
        },
        responseType: "blob", //服务器返回的数据类型
      });
      await this.getInfo();
      return res;
    },
    async changeInfo() {
      let res = await this.$ajax.post(
        "/usergroup/alterUsergroupInfo",
        {
          id: this.id,
          name: this.info.name,
          introduce: this.info.introduce,
        },
        {
          headers: {
            Authorization: `Bearer ${this.$store.state.token}`,
          },
        }
      );
      if (res.data.code === 0) {
        this.$message.success(res.data.message);
      } else {
        await this.getInfo();
        this.$message.error(res.data.message);
      }
    },
    async deleteStu(index, rows) {
      let res = await this.$ajax.post(
        "/usergroup/deleteStudent",
        {
          id: this.id,
          userId: this.stuList[index].id,
          stuNum: this.stuList[index].stuNum,
        },
        {
          headers: {
            Authorization: `Bearer ${this.$store.state.token}`,
          },
        }
      );
      //  (res);
      if (res.status === 200) {
        if (res.data.code === 0) {
          this.$message.success("删除学生成功!");
          rows.splice(index, 1);
        } else {
          this.$message.error("删除学生失败," + res.data.message);
        }
      } else {
        this.$message.error("网络错误");
      }
    },
    changePage(currentPage) {
      this.start = (currentPage - 1) * this.pageSize;
      this.end =
        this.start + this.pageSize > this.stuList.length
          ? this.stuList.length
          : this.start + this.pageSize;
    },
    getId() {
      this.id = parseInt(this.$route.query.userid || "0");
      //  (this.id);
    },
  },
};
</script>

<style lang="less" scoped>
.userListDetails {
  .detailsCard {
    position: relative;
    box-sizing: border-box;
    padding: 30px 30px 30px 30px;
    box-shadow: 0 1px 20px -6px rgba(0, 0, 0, 0.5);
    border-radius: 10px;
    margin-top: 10px;

    .basicData {
      margin-bottom: 40px;
      //background: red;

      .infoItem {
        position: relative;
        left: 0;
        display: inline-block;
        text-align: left;
        width: 50%;
        height: 70px;
        //background: #42b983;
        line-height: 70px;

        .title {
          display: inline-block;
          text-align: right;
          width: 70px;
          //background: yellow;
          box-sizing: border-box;
          padding-right: 20px;
        }

        .input {
          //background: orange;
          display: inline-block;
          //position: absolute;
          //left: 150px;
          //right: 0;
          width: calc(100% - 100px);
        }
      }
    }

    .studentList {
      text-align: left;
      box-sizing: border-box;
      padding-right: 20px;
      position: relative;
      display: flex;

      .stuTitle {
        position: relative;

        vertical-align: top;
        width: 100px;
      }

      .tableDiv {
        width: calc(100% - 100px);

        .stuTable {
          display: inline-block;

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
        }
      }
    }
  }
}
</style>
