<template>
  <div id="app">
    <!-- iconfont -->
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
    async getMyInfo() {
      let _token = localStorage.getItem("token");
      let info = await this.$ajax.post(
        "/user/info",
        {},
        {
          headers: {
            Authorization: `Bearer ${_token}`,
          },
        }
      );

      if (info.data.code === 0) {
        this.$store.commit("setMyInfo", info.data.data);
        this.$store.commit("setToken", _token);
        this.$store.commit("setNoToken", false);
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

    if (token != null) {
      await this.getMyInfo();
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
