<template>
  <div>
    <el-card>
      <div>
        <el-table :data="problems" style="width:100%">
          <el-table-column label="id" prop="id" />
          <el-table-column label="标题" prop="name" />
          <el-table-column label="类型" prop="typeName" />
          <el-table-column label="难度" prop="difficultyName" />
          <el-table-column label="做题" prop="difficultyName">
            <template slot-scope="scope">
              <el-button
                @click="goToProblems(scope.row.id)"
                type="text"
                size="small"
                >查看</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  data() {
    return {
      problems: [],
    };
  },
  methods: {
    goToProblems(id) {

       	this.$router.push({name:"Question",params:{problemId:id}});

    },
  },
  created() {
    this.problems = this.$store.state.problemSetInfo.problemDtos;
    console.log(this.$store.state.problemSetInfo.problemDtos);
  },
};
</script>
