<template>
  <div class="home">
    <el-tabs v-model="tabVal">
      <el-tab-pane label="公开题目集" name="publicSets">
        <public-problem-sets ref="publicProblemSets" />
      </el-tab-pane>
      <el-tab-pane label="我的题目集" name="mySets" v-if="hasToken">
        <my-problem-sets ref="myProblemSets" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import PublicProblemSets from "../../components/ProblemSets/publicProblemSets.vue";
import MyProblemSets from "../../components/ProblemSets/myProblemSets.vue";
export default {
  components: {
    PublicProblemSets,
    MyProblemSets,
  },
  data() {
    return {
      tabVal: "publicSets",
    };
  },
  created() {
    
    if (this.hasToken) {
      this.$nextTick(() => {
        this.$refs.publicProblemSets.getPublicProblemSets();
        this.$refs.myProblemSets.getMyProblemSets();
      });
    } else {
      this.$router.push("/login");
    }
  },
  computed: {
    hasToken() {
      return localStorage.getItem("token") !== null;
    },
  },
};
</script>

<style scoped></style>
