<template>
  <div>
    <!-- 首页，公共题目集 -->
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
    };
  },
  methods: {
    async getPublicProblemSets() {
      let res = await this.$ajax.post(
        "/problemSet/getPublicProblemSet",
        {},
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );
      if (res.data.code == 0) {
   
        this.problemSets = res.data.data;
      }
    },
  },
};
</script>

<style scoped>
.ul li {
  margin-block: 20px;
}
</style>