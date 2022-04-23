<template>
	<div>
		<el-card v-if="shouldShow">
			<div slot="header" style="text-align: center;">
				<span style="font-weight: bolder; color: gray">{{problemDetail.title}}</span>
			</div>
			<div>
				<el-col :span="24">
					<div class="questiondetails">
						<question-details :problemId="problemDetail.problemId" :title.sync="problemDetail.title" ref="questionDetails" />
					</div>
					<el-divider>
						<i class="el-icon-monitor" />
					</el-divider>
					<div class="input" style="margin-top: 20px">
						<judge-input :problemId="problemDetail.problemId" @updatePassRadio="updatePassRadio" />
					</div>
				</el-col>
			</div>
		</el-card>
		<el-card v-else-if="$store.state.problemSetInfo.open" v-show="noThisProblem">
			<el-result icon="error" title="错误" subTitle="该题目不存在">
				<template slot="extra">
					<el-button type="primary" size="medium" @click="backToList">返回题目列表</el-button>
				</template>
			</el-result>
		</el-card>
	</div>
</template>

<script>
import JudgeInput from "../../components/solveProblems/judgeInput.vue";
import QuestionDetails from "../../components/solveProblems/questionDetails.vue";
export default {
	components: {
		JudgeInput,
		QuestionDetails,
	},
	data() {
		return {
			problemDetail: {},
			shouldShow: false,
			noThisProblem: false,
		};
	},
	methods: {
		backToList() {
			this.$router.push({
				path: `/problem-set/${this.$route.params.problemSetId}/problems`,
			});
		},
		updatePassRadio() {
			this.$refs.questionDetails.updatePassRadio();
		},
		judgeProblemExists() {
			if (this.problemIds.indexOf(this.problemDetail.problemId) !== -1) {
				this.shouldShow = true;
				this.$nextTick(async () => {
					this.$refs.questionDetails.getDetails(
						this.problemDetail.problemId
					);
				});
			} else {
				this.shouldShow = false;
				this.noThisProblem = true;
			}
		},
	},
	created() {
		this.problemDetail.problemId = Number(this.$route.params.problemId);
		this.judgeProblemExists();
	},
	watch: {
		async $route(newVal) {
			this.problemDetail.problemId = Number(newVal.params.problemId);
			this.judgeProblemExists();
		},
		problemIds() {
			this.judgeProblemExists();
		},
	},
	computed: {
		problemIds() {
			return this.$store.state.problemSetInfo.problemIds;
		},
	},
};
</script>