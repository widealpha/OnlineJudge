<template>
  <div>
    <el-button @click="addDialog = true">添加用户组</el-button>
    <!-- <el-table></el-table> -->
    <userList />
    <el-dialog
      :visible="addDialog"
      title="请输入要添加的用户组id"
      @close="addDialog = false"
    >
      <el-input v-model="userGroupId" @input="checkIsNull"></el-input>
      <span slot="footer" class="dialog-footer">
      
        <el-button @click="addUserGroup" :disabled="disableSubmit" type="primary"
          >提交</el-button
        >
        </span
      >
    </el-dialog>
  </div>
</template>

<script>
import userList from "@/components/userList/userList";
export default {
  name: "MyUserGroup",
  components: {
    userList,
  },
  data() {
    return {
      disableSubmit: true,
      userGroupId: "",
      addDialog: false,
    };
  },
  methods: {
    checkIsNull() {
      if (this.userGroupId=="") {
       this.disableSubmit=true
       
      } else {
        this.disableSubmit = false;
      }
    },
    async addUserGroup() {
      this.addDialog = false;
      let res = await this.$ajax.post(
        "/userGroup/linkUserGroupProblemSet",
        {
          userGroupId: this.userGroupId,
          problemSetId: this.$store.state.problemSetInfo.id,
        },
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );
      console.log(res);
    },
    async getAllUserGroup() {
      let res = await this.$ajax.post(
        "/problemSet/problemSetUserGroups",
        {
          problemSetId: this.$store.state.problemSetInfo.id,
        },
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );
    },
  },
  created() {
    this.getAllUserGroup();
  },
};
</script>

<style></style>
