<template>
  <div>
    <el-card>
      <div>
        <ul style="list-style: none">
          <li
            v-for="(item, index) in $store.state.problemSetInfo.problems"
            :key="index"
            style="margin-bottom: 10px"
          >
            <el-card shadow="hover" style="text-align: left">
              <!-- <el-card v-if="item.tagList.indexOf(query.tag)" shadow="hover" style="text-align: left;"> -->
              <div slot="header">
                <el-row style="height: 2em">
                  <el-col :span="12" style="text-align: left; line-height: 2em">
                    <span style="color: #4179b1; font-size: 18px">
                      <i
                        class="el-icon-s-management"
                        style="color: #4179b1; margin-right: 1px"
                      />{{ item.id }}
                    </span>
                    &nbsp;
                    <el-link
                      type="primary"
                      @click="
                        $router.push({
                          path: `/problem-set/${$store.state.problemSetInfo.problemSetId}/problems/${item.id}`,
                        })
                      "
                      >{{ item.title }}</el-link
                    >
                  </el-col>
                </el-row>
              </div>
              <div>
                <el-tag
                  @click="searchProblemsByTag(tag)"
                  effect="plain"
                  size="mini"
                  style="margin-left: 10px; cursor: pointer"
                  v-for="tag in item.tagList"
                  :key="tag"
                >
                  {{ tag }}
                </el-tag>
                <span
                  ><i
                    class="el-icon-date"
                    style="color: #4179b1; margin-right: 2px; font-size: 18px"
                  />最后修改时间: {{ item.lastModifiedDate }}</span
                >
                <span style="float: right"
                  ><i
                    class="el-icon-s-opportunity"
                    style="color: #4179b1; margin-right: 1px; font-size: 18px"
                  />难度:{{ item.difficulty }}</span
                >
                <span style="float: right; margin-right: 10px"
                  ><i
                    class="el-icon-user-solid"
                    style="color: #4179b1; margin-right: 2px; font-size: 18px"
                  />题目作者:{{ item.creatorId }}</span
                >
              </div>
            </el-card>
          </li>
        </ul>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  methods: {
    searchProblemsByTag(tag) {
      this.$router.push({
        path: `/problem-set/${this.$store.state.problemSetInfo.problemSetId}/allProblems`,
        query: { tag: tag },
      });
    },
  },
};
</script>