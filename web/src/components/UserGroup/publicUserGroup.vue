<template>
  <div style="height:500px">
    选择用户组
    <div class="block" style="margin:20px 0">
      <el-cascader
        v-model="value"
        :options="options"
        @change="handleChange"
      ></el-cascader>
    </div>
  </div>
</template>

<script>
export default {
  name: "PublicUserGroup",
  data() {
    return {
      value: [],
      options: [
        {
          //值
          value: null,
          // 名
          label: "",
          disabled: false,
          children: [],
        },
      ],
      firstName: "",
      firstlabel: null,
    };
  },
  methods: {
    async handleChange(value) {
      const userGroupId = value[1];
      const problemSetId = this.$store.state.problemSetInfo.id;
      let res = await this.$ajax.post(
        "/userGroup/linkUserGroupProblemSet",
        {
          userGroupId: userGroupId,
          problemSetId: problemSetId,
        },
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );
      if (res.data.code === 0) {
        this.$message.success("添加用户组成功");
      } else {
        this.$message.error("添加用户组失败");
      }
      console.log(res);
    },
    async getChildUserGroup() {
      let res = await this.$ajax.post(
        "/userGroup/allSubGroupInfo",
        {
          userGroupId: this.options[0].value,
        },
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );

      if (res.data.code === 0) {
        this.options[0].children = res.data.data.children;
      }
    },
    async getPublicUserGroup() {
      let res = await this.$ajax.post(
        "/userGroup/getPublicUserGroupInfo",
        {},
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );

      if (res.data.code == 0) {
        this.options[0].label = res.data.data.name;
        this.options[0].value = res.data.data.id;
      }
    },
  },
  async created() {
    await this.getPublicUserGroup();
    await this.getChildUserGroup();
  },
};
</script>

<style></style>
