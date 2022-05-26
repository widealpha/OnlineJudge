<template>
  <div>
    <el-switch
      class="switch"
      v-model="darkStyle"
      active-color="dimgray"
      active-icon-class="el-icon-moon"
      inactive-color="gold"
      inactive-icon-class="el-icon-sunny"
      @change="themeChange"
    ></el-switch>
    <el-card
      class="login-card"
      :style="{
        background: darkStyle
          ? 'rgba(10, 10, 10, 0.7)'
          : 'rgba(250, 250, 250, 0.7)',
      }"
    >
      <div slot="header">
        <el-link
          @click="goBack"
          :style="{
            position: 'absolute',
            left: '5%',
            color: darkStyle ? 'white' : '',
          }"
          ><i class="el-icon-back"></i>后退</el-link
        >
        <span
          :style="{
            color: darkStyle ? 'white' : 'dodgerblue',
            'font-size': '20px',
          }"
          >山大OJ登录页</span
        >
      </div>
      <div>
        <el-input
          class="input"
          @keydown.enter.native="login"
          placeholder="请输入账户"
          prefix-icon="el-icon-user"
          v-model="userId"
        ></el-input
        ><br />
        <el-input
          class="input"
          @keydown.enter.native="login"
          placeholder="请输入密码"
          prefix-icon="el-icon-key"
          v-model="userPsw"
          show-password
        ></el-input
        ><br />
        <el-button
          class="btn"
          @mouseover.native="mouseHover"
          @mouseleave.native="mouseLeave"
          @click="login"
          size="medium"
          type="primary"
          :style="darkStyle ? darkThemeBtnStyle : ''"
          :loading="loading"
          >登录</el-button
        >
        <el-button class="btn" @click="register" size="medium" plain
          >注册</el-button
        >
      </div>
    </el-card>
    <ribbons :darkStyle.sync="darkStyle" ref="ribbons" />
  </div>
</template>

<script>
import ribbons from "../components/Ribbons.vue";

export default {
  components: { ribbons },
  name: "Login",
  data() {
    return {
      userId: "",
      userPsw: "",
      loading: false,
      darkStyle: false,
      darkThemeBtnStyle:
        "background-color: gold; border-color: gold; color: #1f1f1f",
    };
  },
  methods: {
    mouseHover() {
      if (this.darkStyle) {
        this.darkThemeBtnStyle =
          "background-color: yellow; border-color: yellow; color: #1f1f1f";
      }
    },
    mouseLeave() {
      if (this.darkStyle) {
        this.darkThemeBtnStyle =
          "background-color: gold; border-color: gold; color: #1f1f1f";
      }
    },
    async login() {
      this.loading = true;
      let res = await this.$ajax.post(
        "/user/login",
        {
          username: this.userId,
          password: this.userPsw,
        },
        {
          headers: {
            // "token": 'UmamusumePrettyDerby',
          },
        }
      );
      if (res.status == 200) {
        const _token = res.data.data;
        // 请求个人信息
        let info = await this.$ajax.post(
          "/user/info",
          {},
          {
            headers: {
              Authorization: `Bearer ${_token}`,
            },
          }
        );

        this.$store.commit("setMyInfo", info.data.data);

        localStorage.setItem("token", _token);
        this.$message({
          title: "登录成功",
          message: "登录成功！正在为您跳转页面...",
          type: "success",
          duration: 1000,
          showClose: false,
          onClose: () => {
            this.$store.commit("setToken", _token);
            this.$store.commit("setNoToken", false);
         

            this.$router.push("/");
          },
        });
      } else {
        this.$alert(
          `错误代码${res.data.code}:${res.data.message}`,
          "登录失败",
          {
            type: "error",
          }
        );
      }
      this.loading = false;
    },
    register() {
      this.$router.push({ path: "/register" });
    },
    goBack() {
      this.$router.go(-1);
    },
    themeChange() {
      this.$nextTick(() => {
        this.$refs.ribbons.themeChange();
      });
    },
  },
  beforeCreate() {
    if (this.$store.state.token !== null) {
      this.$router.replace("/");
    }
  },
};
</script>

<style scoped>
div {
  text-align: center;
}
.switch {
  position: absolute;
  right: 5%;
  top: 5%;
}
.login-card {
  width: 50%;
  height: 50%;
  position: absolute;
  left: 25%;
  top: 25%;
}
.input {
  position: relative;
  width: 50%;
  margin-top: 5%;
  z-index: 2;
}
.btn {
  width: 15%;
  margin-top: 5%;
  margin-inline: 5%;
}
</style>
