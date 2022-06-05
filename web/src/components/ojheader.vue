<template>
  <div class="OJHeader">
    <el-header
      style="
        background: #fff;
        height: 3.5em;
        line-height: 3.6em;
        padding-left: 0px;
        overflow: hidden;
      "
    >
      <!-- 左侧logo start -->
      <div class="logo"></div>
      <div class="title">· 山东大学程序设计能力提升平台</div>
      <!-- 左侧logo end -->
      <!-- 右侧 start -->
      <div v-if="noToken" style="float: right">
        <el-button plain @click="Register">注册</el-button>
        <el-button @click="Login" type="primary">登录</el-button>
      </div>
      <div v-else style="float: right">
        <el-dropdown class="drop" :show-timeout="0">
          <div
            style="margin-right: 30px"
            @mouseenter="nameColor = 'dodgerblue'"
            @mouseleave="nameColor = '#000'"
          >
            <i
              class="iconfont icon-user-filling"
              style="
                color: #0d4990;
                font-size: 33px;
                float: right;
                margin-left: 10px;
              "
            >
            </i>
            <el-link
              :underline="false"
              :style="{
                'font-size': '1.3em',
                color: nameColor,
              }"
              >{{ username }}</el-link
            >
          </div>
          <el-dropdown-menu>
            <el-dropdown-item @click.native="myInfo"
              ><i class="el-icon-user"></i>个人信息</el-dropdown-item
            >
            <el-dropdown-item @click.native="Logout"
              ><i class="el-icon-switch-button"></i>退出登录</el-dropdown-item
            >
          </el-dropdown-menu>
        </el-dropdown>
      </div>
      <!-- 右侧 end -->
    </el-header>
  </div>
</template>

<script>
export default {
  name: "ojheader",
  data() {
    return {
      avatar: require("@/assets/icon/student.svg"),
      nameColor: "#000",
    };
  },

  methods: {
    Logout() {
      Promise.all([
        

        this.$store.commit("setMyInfo", {
          headImage: null,
          nickname: null,
          realName: null,
          stuNum: null,
          userId: undefined,
          username: null,
        }),
      ]).then(() => {
        localStorage.removeItem("token");
        this.$router.replace({ path: "/login" });
      });
    },
    myInfo() {
      this.$router.push({ path: "/info" });
    },
    Login() {
      this.$router.push({ path: "/login" });
    },
    Register() {
      this.$router.push({ path: "/register" });
    },
  },
  computed: {
    noToken() {
      return localStorage.getItem("token") === null;
    },
    username() {
      return this.$store.getters.usernameToShow;
    },
    isTeacher() {
      return this.$store.state.myInfo.roles.includes("ROLE_TEACHER");
    },
    isAdmin() {
      return this.$store.state.myInfo.roles.includes("ROLE_ADMIN");
    },
  },
};
</script>

<style lang="less" scoped>
.OJHeader {
  .drop:hover {
    cursor: pointer;
  }
  .logo {
    float: left;
    background-image: url("../assets/icon/logo.png");
    height: 100%;
    width: 130px;
    margin-left: 50px;
    background-size: contain;
    background-repeat: no-repeat;
    background-position-y: center;
  }
  .title {
    color: #0d4990;
    // color: #334270;
    font-weight: bolder;
    font-size: larger;
    float: left;
    letter-spacing: 1px;
  }
}
</style>
