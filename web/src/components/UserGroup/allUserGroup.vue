<template>
  <div>
    <template>
      <el-table :data="allUserGroup" stripe style="width: 100%">
        <el-table-column prop="id" label="id"> </el-table-column>
        <el-table-column prop="name" label="名称"> </el-table-column>
        <el-table-column prop="type" label="类型"> </el-table-column>
        <el-table-column prop="intruduction" label="简介"> </el-table-column>
        <el-table-column prop="creatorId" label="创建者id"> </el-table-column>
        <el-table-column
          prop=isPublic
          label="是否公开"
        >
        </el-table-column>
      </el-table>
    </template>
  </div>
</template>

<script>
export default {
  name: "AllUserGroup",
  data() {
    return {
      allUserGroup: [],
    };
  },
  methods: {
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
      if ((res.status = 200)) {
        if (res.data.code == 0) {
          this.allUserGroup = res.data.data;
        }
      }
    },
  },
  created() {
    this.getAllUserGroup();
  },
};
</script>

<style></style>
