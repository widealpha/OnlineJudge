<template>
  <div>
    <el-card>
      <el-tabs v-model="activeName" @tab-click="handleClick">
         <el-tab-pane label="所有的用户组" name="1"><AllUserGroup /></el-tab-pane>
        <el-tab-pane label="我的用户组" name="2"><MyUserGroup /></el-tab-pane>
        <el-tab-pane label="公共用户组" name="3"><PublicUserGroup/></el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import AllUserGroup from "@/components/UserGroup/allUserGroup.vue";
import   MyUserGroup  from "@/components/UserGroup/myUserGroup";
import PublicUserGroup from "@/components/UserGroup/publicUserGroup";
export default {
  components: {
  MyUserGroup,
  PublicUserGroup,
  AllUserGroup
  },
  name:"userGroup",
  data() {
    return {
      activeName: "1",
    };
  },
  methods: {
    handleClick(tab, event) {
      console.log(tab, event);
    },
  },
  created() {
    if (!this.$store.state.myInfo.roles.includes("ROLE_TEACHER")) {
      this.$router.replace(
        `/problem-set/${this.$store.state.problemSetInfo.problemSetId}/`
      );
    }

  },
};
</script>
