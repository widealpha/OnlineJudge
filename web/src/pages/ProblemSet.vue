<template>
  <div>
    <!-- 题目集详情 -->
    <el-container v-loading="initing">
      <el-aside
        style="width: 15%; height: 100vh; position: fixed; overflow-y: overlay"
      >
        <el-menu
          :default-active="menuIndex"
          @select="changeIndex"
          style="height: 100vh"
        >
          <el-button type="text" @click="backToProblemsetList"
            ><i class="el-icon-arrow-left"></i>题目集列表</el-button
          >
          <el-menu-item index="0" @click="goToDetails">
            <i class="el-icon-menu" />
            <span slot="title">题目集详情</span>
          </el-menu-item>
          <el-menu-item index="-1" @click="goToProblemList">
            <i class="el-icon-menu" />
            <span slot="title">题目列表</span>
          </el-menu-item>
          <!-- <el-menu-item
            v-if="problemSetInfo.isMyProblemSet"
            index="-2"
            @click="goToAllProblemList"
          >
            <i class="el-icon-menu" />
            <span slot="title">题目管理</span>
          </el-menu-item> -->
          <el-menu-item
            v-if="problemSetInfo.isMyProblemSet"
            index="-3"
            @click="goToUserGroups"
          >
            <i class="el-icon-menu" />
            <span slot="title">用户组管理</span>
          </el-menu-item>

          <el-menu-item
            index="-4"
            @click="goToRank"
            v-if="this.$store.state.problemSetInfo.type == 3"
          >
            <i class="el-icon-menu" />
            <span slot="title">竞赛成绩排行</span>
          </el-menu-item>

          <el-divider></el-divider>
          <div style="display: flex; padding: 0 1em; flex-wrap: wrap">
            <button
              v-for="(item, index) in problemSetInfo.problems"
              :key="index + 1"
              :class="{
                problemBtn: true,
                passed: item.selfComplication === 2,
                failed: item.selfComplication === 3,
              }"
              :style="
                menuIndex === (index + 1).toString()
                  ? {}
                  : { borderColor: 'rgba(0,0,0,0)' }
              "
              @click="getToProblem(index + 1, item)"
            >
              <div class="passStatusCover">
                {{
                  (function(passedStatus) {
                    switch (passedStatus) {
                      case 1:
                        return index + 1;
                      case 2:
                        return "✔";
                      case 3:
                        return "✘";
                    }
                  })(item.selfComplication)
                }}
              </div>
              <div class="problemIndex">{{ index + 1 }}</div>
            </button>
          </div>
        </el-menu>
      </el-aside>
      <div
        style="
          height: calc(100vh - 40px);
          position: relative;
          left: 15%;
          padding: 1%;
          width: 83%;
          overflow-y: overlay;
        "
      >
        <keep-alive>
          <router-view />
        </keep-alive>
      </div>
    </el-container>
  </div>
</template>

<script>
export default {
  name: "problemSet",
  data() {
    return {
      initing: true,
      menuIndex: undefined,
      alteringInfo: false,
      showUserGroup: false,
      problemDetail: {},
      openedUserGroups: [],
      myUserGroups: [],
      delGroups: [],
      addGroups: [],
      showProblems: false,
    };
  },
  methods: {
    // 获取到所有的题目集信息
    async getProblemset() {
      let res = await this.$ajax.post(
        "/problemSet/getProblemSetInfo",
        {
          problemSetId: this.problemSetInfo.problemSetId,
        },
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );
      console.log(res);
      if (res.data.code == 0) {
        const data = res.data.data;
        console.log(data);
        const title = data.name;
        const introduction = data.introduction;
        const creatorId = data.creatorId;
        const open = data.isPublic;
        const endTime = data.endTime;
        const beginTime = data.beginTime;
        const id = data.id;
        const problemDtos = data.problemDtos;
        const type = data.type;
        const isMyProblemSet = creatorId == this.$store.state.myInfo.userId;

        await this.$store.commit("setProblemSetInfo", {
          title,
          introduction,
          creatorId,
          open,
          id,
          endTime,
          beginTime,
          problemDtos,
          type,
          isMyProblemSet,
        });

        this.changeMenuIndex();
        this.initing = false;
      }
    },
    changeIndex(index) {
      this.menuIndex = index;
    },
    backToProblemsetList() {
      this.$router.push({ path: "/teacher/myproblemsets" });
    },
    goToDetails() {
      this.$router.push({
        path: `/problem-set/${this.problemSetInfo.problemSetId}`,
      });
    },
    goToProblemList() {
      this.$router.push({
        path: `/problem-set/${this.problemSetInfo.problemSetId}/problems`,
      });
    },
    goToAllProblemList() {
      this.$router.push({
        path: `/problem-set/${this.problemSetInfo.problemSetId}/allProblems`,
      });
    },
    goToUserGroups() {
      this.$router.push({
        path: `/problem-set/${this.problemSetInfo.problemSetId}/userGroups`,
      });
    },
    goToRank() {
      this.$router.push({
        path: `/problem-set/${this.problemSetInfo.problemSetId}/problemSetRank`,
      });
    },
    getToProblem(index, problem) {
      if (this.menuIndex != index) {
        this.menuIndex = index.toString();
        this.$router.push({
          path: `/problem-set/${this.problemSetInfo.problemSetId}/problems/${problem.id}`,
        });
      }
    },
    changeMenuIndex() {
      let path = this.$route.path;
      if (path.indexOf("problems/") !== -1) {
        let index = 0;
        this.problemSetInfo.problems.forEach((element) => {
          index++;
          if (element.id == this.$route.params.problemId) {
            this.menuIndex = index.toString();
          }
        });
      } else if (path.indexOf("problems") !== -1) {
        this.menuIndex = "-1";
      } else if (path.indexOf("allProblems") !== -1) {
        this.menuIndex = "-2";
      } else if (path.indexOf("userGroups") !== -1) {
        this.menuIndex = "-3";
      } else if (path.indexOf("problemSetRank") !== -1) {
        this.menuIndex = "-4";
      } else {
        this.menuIndex = "0";
      }
    },
  },
  beforeCreate() {
    if (localStorage.getItem("token") === null) {
      this.$router.replace("/");
    }
  },
  async created() {
    this.$store.commit("setProblemSetInfo", {
      problemSetId: Number(this.$route.params.problemSetId),
    });

    await this.getProblemset();
  },
  beforeDestroy() {
    this.$store.commit("setProblemSetInfo", {
      problems: [],
      // 是否公开
      open: 0,
      // 题目
      title: "",
      // 介绍
      introduction: "",
      creatorId: 0,
      beginTime: "2000-01-01 00:00:00",
      endTime: "2000-01-01 00:00:00",
      // 竞赛，作业还是测试
      type: 0,
      isMyProblemSet: false,
      problemSetId: 0,
    });
  },
  watch: {
    async $route(to, from) {
      if (to.path != from.path) {
        if (to.params.problemSetId != from.params.problemSetId) {
          await this.$store.commit("setProblemSetInfo", {
            problemSetId: Number(this.$route.params.problemSetId),
          });

          this.getProblemset();
        } else {
          this.changeMenuIndex();
        }
      }
    },
  },
  computed: {
    problemSetInfo() {
      return this.$store.state.problemSetInfo;
    },
  },
};
</script>

<style scoped>
.el-aside .el-button--text {
  color: black;
}
.el-aside .el-button--text:hover {
  color: red;
}

.problemBtn {
  background: hsl(0, 0%, 97%);
  border: none;
  cursor: pointer;
  font-size: 1rem;
  width: 1.75rem;
  height: 1.75rem;
  margin-inline: 0.2rem;
  margin-block: 0.15rem;
  border-radius: 0.15rem;
  color: #2794f8;
  border: 0.1em solid #2794f8;
  transition: 0.35s;
  outline: none;
  line-height: 1.75rem;
}

.problemBtn:hover {
  background: hsl(0, 0%, 90%);
}

.problemBtn:hover .passStatusCover {
  display: none;
}

.problemIndex {
  display: none;
}

.problemBtn:hover .problemIndex {
  display: block;
}

.passed {
  color: limegreen;
  border: 0.1em solid limegreen;
}

.failed {
  color: red;
  border: 0.1em solid red;
}

.input {
  width: 100%;
}
</style>
