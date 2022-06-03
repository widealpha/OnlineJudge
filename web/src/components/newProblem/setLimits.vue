<template>
  <div>
    <el-card>
      <div slot="header" style="text-align: left; min-height: 20px">
        <el-button
          style="margin-left: 10px;"
          @click="$emit('update:step', 2)"
          plain
          >上一步</el-button
        >
      </div>
      <el-form
        style="padding:20px"
        label-position="left"
        :model="addForm"
        ref="addForm"
        :rules="addRules"
		
      >
        <el-form-item prop="codeLengthLimit" label="最大代码长度:">
          <el-input v-model="addForm.codeLengthLimit">
            <template slot="append">kb</template>
          </el-input>
        </el-form-item>
        <el-form-item prop="timeLimit" label="最大运行时间:">
          <el-input v-model="addForm.timeLimit">
            <template slot="append">ms</template>
          </el-input>
        </el-form-item>
        <el-form-item prop="memoryLimit" label="最大占用内存:">
          <el-input v-model="addForm.memoryLimit">
            <template slot="append">byte</template>
          </el-input>
        </el-form-item>
      </el-form>
      <el-button style="margin: 20px auto 40px auto;" type="primary" @click="submitLimit"
        >提交限制</el-button
      >
    </el-card>
  </div>
</template>

<script>
export default {
  props: {
    step: Number,
  },

  data() {
    return {
      addForm: {
        codeLengthLimit: null,
        timeLimit: null,
        memoryLimit: null,
      },
      addRules: {
        codeLengthLimit: [
          {
            required: true,
            message: "该项不能为空",
            trigger: "blur",
          },
          {
            pattern: /^[0-9]+$/,
            message: "仅允许输入数字",
            trigger: ["blur", "change"],
          },
        ],
        memoryLimit: [
          {
            required: true,
            message: "该项不能为空",
            trigger: "blur",
          },
          {
            pattern: /^[0-9]+$/,
            message: "仅允许输入数字",
            trigger: ["blur", "change"],
          },
        ],
        timeLimit: [
          {
            required: true,
            message: "该项不能为空",
            trigger: "blur",
          },
          {
            pattern: /^[0-9]+$/,
            message: "仅允许输入数字",
            trigger: ["blur", "change"],
          },
        ],
      },
    };
  },
  methods: {
    async submitLimit() {
      this.$refs.addForm.validate(async (valid) => {
        if (valid) {
          let res = await this.$ajax.post(
            "/problem/updateLimit",
            {
              problemId: this.problemId,
              timeLimit: this.addForm.timeLimit,
              memoryLimit: this.addForm.memoryLimit,
              codeLengthLimit: this.addForm.codeLengthLimit,
            },
            {
              headers: {
                Authorization: `Bearer ${this.$store.state.token}`,
              },
            }
          );

          if (res.data.code == 0) {
            this.$message.success("添加成功");
          } else {
            this.$message.error(res.data.message);
          }
        }
      });
    },
    async getMyLimits() {
      this.addForm.codeLengthLimit = this.$store.state.problemInfo.codeLengthLimit;
      this.addForm.timeLimit = this.$store.state.problemInfo.timeLimit;
      this.addForm.memoryLimit = this.$store.state.problemInfo.memoryLimit;
    },
  },
  async created() {
    await this.getMyLimits();
    let problemId = this.$route.query.problemId;
    if (problemId) {
      this.problemId = Number(problemId);
    }
  },
  watch: {
    $route(newVal) {
      let problemId = newVal.query.problemId;
      if (problemId) {
        this.problemId = Number(problemId);
        this.getMyLimits();
      } else {
        this.$emit("update:step", 1);
      }
    },
  },
};
</script>

<style scoped></style>
