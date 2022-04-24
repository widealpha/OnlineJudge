<template>
  <div id="app" style="background: #EEF2F4">
    <router-view/>
  </div>
</template>

<script>

export default {
  methods: {
    showLogo() {
      console.log(
          "███████╗██████╗ ██╗   ██╗ ██████╗      ██╗\n██╔════╝██╔══██╗██║   ██║██╔═══██╗     ██║\n███████╗██║  ██║██║   ██║██║   ██║     ██║\n╚════██║██║  ██║██║   ██║██║   ██║██   ██║\n███████║██████╔╝╚██████╔╝╚██████╔╝╚█████╔╝\n╚══════╝╚═════╝  ╚═════╝  ╚═════╝  ╚════╝ \n"
      );
    },
    setTheme() {
      !localStorage.getItem("darkTheme") && localStorage.setItem("darkTheme", false);
    },

    setPreTime() {
      const createTime = new Date().valueOf();
      const dom = document.querySelector(".preLoading");
      setTimeout(() => {
        dom.style.opacity = 0;
      }, 500 - createTime + window.sduojInitTime);
      setTimeout(() => {
        document.body.removeChild(dom);
      }, 800 - createTime + window.sduojInitTime);
    },
    async checkIdentity(token) {
      const jwt = require("jsonwebtoken");
      const TOKEN = jwt.decode(token);
      const {dispatch} = this.$store
      const expFlag = TOKEN.exp * 1000 <= new Date().getTime();
      if (expFlag) {
        this.$message({
          message: "您的身份认证已过期，请重新登陆",
          type: "warning",
          duration: 1000,
          onClose: () => {
            localStorage.removeItem("token");
            this.$router.push("/login");
          },
        });
      } else {
        dispatch("setToken", token);
        switch (true) {
          case TOKEN.ROLE.indexOf("ROLE_ADMIN") !== -1:
            dispatch("setIsAdmin", true);
            break;
          case TOKEN.ROLE.indexOf("ROLE_TEACHER") !== -1:
            dispatch("setIsTeacher", true);
            break;
        }
        await this.getMyInfo(`Bearer ${token}`);
      }
    },
    async getMyInfo(authorization) {
      let res = await this.$ajax.get(
          "/user/myUserInfo",
          {},
          {
            headers: {
              Authorization: authorization,
            },
          }
      );
      if (res.data.code === 0 && res.data.message === "success") {
        this.$store.dispatch("setMyInfo", res.data.data);
      }
    },
  },

  async created() {
    this.showLogo();
    this.setTheme();
    const {push} = this.$router;
    let token = localStorage.getItem("token");
    if (!token) return push("/login") && this.setPreTime();
    await this.checkIdentity(token);
    this.setPreTime();
  },
};
</script>

<style lang="less">
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-thumb {
  background-color: #a8a8a8;
  border-radius: 3px;
}

.el-main {
  padding: 20px 10px 10px 10px !important;
}
</style>
