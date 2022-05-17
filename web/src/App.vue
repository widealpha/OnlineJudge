<template>
  <div id="app">
    <!-- iconfont  -->
    <link
      rel="stylesheet"
      href="//at.alicdn.com/t/font_3405352_lj553jmowze.css"
    />
    <router-view />
  </div>
</template>

<script>
export default {
  methods: {
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
    console.log(
      "███████╗██████╗ ██╗   ██╗ ██████╗      ██╗\n██╔════╝██╔══██╗██║   ██║██╔═══██╗     ██║\n███████╗██║  ██║██║   ██║██║   ██║     ██║\n╚════██║██║  ██║██║   ██║██║   ██║██   ██║\n███████║██████╔╝╚██████╔╝╚██████╔╝╚█████╔╝\n╚══════╝╚═════╝  ╚═════╝  ╚═════╝  ╚════╝ \n"
    );
    if (localStorage.getItem("darkTheme") == null) {
      localStorage.setItem("darkTheme", false);
    }

    let token = localStorage.getItem("token");
    let jwt = require("jsonwebtoken");
    const TOKEN = jwt.decode(token);

    if (TOKEN != null) {
      let exp = TOKEN.exp * 1000;
      if (exp <= new Date().getTime()) {
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
        this.$store.dispatch("setToken", token);
        if (TOKEN.ROLE.indexOf("ROLE_ADMIN") != -1) {
          this.$store.dispatch("setIsAdmin", true);
        } else if (TOKEN.ROLE.indexOf("ROLE_TEACHER") != -1) {
          this.$store.dispatch("setIsTeacher", true);
        }
        await this.getMyInfo(`Bearer ${token}`);
      }
    } else {
      this.$router.push("/login");
    }

    const createTime = new Date().valueOf();
    const dom = document.querySelector(".preLoading");
    setTimeout(() => {
      dom.style.opacity = 0;
    }, 500 - createTime + window.sduojInitTime);
    setTimeout(() => {
      document.body.removeChild(dom);
    }, 800 - createTime + window.sduojInitTime);
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
</style>
