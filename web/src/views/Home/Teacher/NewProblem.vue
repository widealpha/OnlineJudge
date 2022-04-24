<template>
	<div class="newProblem">
		<el-steps :active="step" simple>
			<el-step title="编辑题目" icon="el-icon-edit" />
			<el-step title="测试数据" icon="el-icon-upload" />
			<el-step title="设置限制" icon="el-icon-cpu" />
		</el-steps>
		<div style="margin-top: 10px;border-radius: 0.5rem;overflow: hidden">
			<publish-question :step.sync="step" v-show="step===1" />
			<div v-if="$route.query.problemId">
				<test-point v-show="step===2" :step.sync="step" />
				<set-limits v-show="step===3" :step.sync="step" />
			</div>
		</div>
	</div>
</template>

<script>
import publishQuestion from "../../../components/newProblem/publishQuestion.vue";
import setLimits from "../../../components/newProblem/setLimits.vue";
import testPoint from "../../../components/newProblem/testPoint.vue";
export default {
	name: "newProblem",
	components: {
		publishQuestion,
		setLimits,
		testPoint,
	},
	data() {
		return {
			step: 1,
		};
	},
	created() {
		if (!this.$store.state.isTeacher) {
			this.$router.replace("/");
		} else {
			let step = this.$route.params.step;
			this.step = step ? step : 1;
		}
	},
	watch: {
		"$route.params.step"(newVal) {
			this.step = newVal ? parseInt(newVal) : 1;
		},
	},
};
</script>
<style scoped>
.newProblem>>>.el-steps--simple{
  background: #fff;
  border-radius: 0.5rem;
}
</style>
