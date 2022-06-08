<template>
  <div>
    <!-- 首页，我的题目集 -->
    <ul style="list-style: none" class="ul">
      <li v-for="(item, index) in problemSets" :key="index">
        <problem-set
          :name="item.name"
          :problemSetId="item.id"
          :type="item.type"
          :open="item.open"
          :creatorId="item.creatorId"
          :beginTime="item.beginTime"
          :endTime="item.endTime"
        ></problem-set>
      </li>
    </ul>
  </div>
</template>

<script>
import problemSet from "./problemSet.vue";
export default {
  components: {
    problemSet,
  },
  data() {
    return {
      loading: false,
      problemSets: [],
      userGroupId: 0,
    };
  },
  methods: {
    async getMyAddedUserGroup() {
      let res = await this.$ajax.post(
        "/userGroup/getSelfJoinedUserGroup",
        {},
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );
      this.userGroupId = res.data.data[0].id;
    },
    async getUserGroupProblemSet() {
      let res = await this.$ajax.post(
        "/userGroup/getUserGroupProblemSet",
        {
          id: this.userGroupId,
        },
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );
     
      if (res.data.code == 0 && res.data.data.length !== 0) {
        this.problemSets = res.data.data;
      }
    },
    async getProblemSetInfo() {
      let res = await this.$ajax.post(
        "/problemSet/getProblemSetInfo",
        {
          problemSetId: this.problemSets[0],
        },
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );
      console.log(res.data.data);
      this.problemSets = [res.data.data];
    },
  },
  async mounted() {
    await this.getMyAddedUserGroup();
    await this.getUserGroupProblemSet();
    this.getProblemSetInfo();
  },
};
</script>

<style scoped>
.ul li {
  margin-block: 20px;
}
</style>
