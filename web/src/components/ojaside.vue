<template>
  <div class="ojaside">
  
    <el-menu
      :default-active="pageIndex"
      :router="true"
      :uniqueOpened="true"
      active-text-color="#3898FF"
      class="el-menu-vertical-demo"
    >
      <el-menu-item index="/">
        <template #title>
          <i class="el-icon-menu" />
          <span>首页</span>
        </template>
      </el-menu-item>

      <el-submenu :disabled="noToken" index="student">
        <template #title>
          <i class="el-icon-location" />
          <span>学生相关</span>
        </template>
        <el-menu-item index="/student/problems">题目列表</el-menu-item>
        <el-menu-item index="/student/userList">用户组列表</el-menu-item>
      </el-submenu>

      <el-submenu v-if="!noToken && isTeacher" index="teacher">
        <template #title>
          <i class="el-icon-location" />
          <span>教师相关</span>
        </template>
        <el-submenu index="1">
          <template #title>
            <span>出题</span>
          </template>
          <el-menu-item index="/teacher/newProblem">编程题</el-menu-item>
          <el-menu-item index="/teacher/newCompletion">填空题</el-menu-item>
          <el-menu-item index="/teacher/newShortAnswerQuestion"
            >简答题</el-menu-item
          >
          <el-menu-item index="/teacher/newJudgementQuestion"
            >判断题</el-menu-item
          >
          <el-menu-item index="/teacher/newSingleChioceQuestion"
            >单选题</el-menu-item
          >
          <el-menu-item index="/teacher/newMuitiChoiceQuestion"
            >多选题</el-menu-item
          >
        </el-submenu>

        <el-menu-item index="/teacher/myProblems">查找我的题目</el-menu-item>
        <el-menu-item index="/teacher/myProblemSets">查看题目集</el-menu-item>
        <el-menu-item index="/teacher/userList">用户组列表</el-menu-item>
      </el-submenu>

      <el-submenu v-if="!noToken && isAdmin" index="admin">
        <template #title>
          <i class="el-icon-location" />
          <span>管理员相关</span>
        </template>
        <el-menu-item index="/admin/excel">学生信息上传</el-menu-item>
        <el-menu-item index="/admin/bind">学生信息绑定</el-menu-item>
        <el-menu-item index="/admin/role">教师权限管理</el-menu-item>
      </el-submenu>
    </el-menu>
  </div>
</template>

<script>
export default {
  name: "ojaside",
  props: {
    pageIndex: String,
  },
  computed: {
    noToken() {
      return this.$store.state.token === null;
    },
    isTeacher() {
      this.$store.state.myInfo;
      return this.$store.state.myInfo.roles.includes("ROLE_TEACHER");
    },
    isAdmin() {
      return this.$store.state.myInfo.roles.includes("ROLE_ADMIN");
    },
  },
};
</script>

<style scoped>
.el-menu {
  text-align: left;
  height: 100%;
}

.ojaside {
  position: relative;
  display: inline-block;
  width: 13em;
  box-sizing: border-box;
  margin: 20px 0 0 10px;
  overflow: hidden;
  border-radius: 0.5rem;
  height: calc(100vh - 3.5em);
}

.ojaside >>> .el-menu {
  border-right: 0 !important;
}
</style>
