<template>
	<div>
		<el-card>
			<div slot="header">
				<el-row style="height: 5vh;">
					<el-col :span="12" style="text-align: left; line-height: 5vh">
						<span>我的题目集</span>
					</el-col>
					<el-col :span="12" style="text-align: right; line-height: 5vh">
						<el-button @click="adding=true;" size="mini">
							创建题目集
						</el-button>
					</el-col>
				</el-row>
			</div>
			<div>
				<ul style="list-style: none;" class="ul" v-loading="loading">
					<li v-for="(item, index) in problemSets" :key="index">
						<problem-set :title="item.name" :problemSetId="item.id" :status="item.status" :open="item.open" :author="item.author.toString()" :beginTime="item.beginTime" :endTime="item.endTime"></problem-set>
					</li>
				</ul>
			</div>
		</el-card>

		<div class="others" style="text-align: left;">
			<el-dialog title="创建题目集" :visible.sync="adding" width="30%">
				<el-form label-position="top" :model="infoForm" :rules="infoRules" ref="infoForm">
					<el-form-item label="名称" prop="name">
						<el-input v-model="infoForm.name" class="input" placeholder="请输入题目集名称"></el-input>
					</el-form-item>
					<el-form-item label=" 时间范围" prop="timeRange">
						<el-date-picker v-model="infoForm.timeRange" type="datetimerange" range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间" class="input">
						</el-date-picker>
					</el-form-item>
					<el-form-item label="公告" prop="announcement">
						<el-input v-model="infoForm.announcement" type="textarea" class="input" placeholder="请输入题目集公告"></el-input>
					</el-form-item>
				</el-form>
				<el-button type="primary" @click="createProblemSet">提交</el-button>
			</el-dialog>
		</div>
	</div>
</template>

<script>
import problemSet from "../../../components/ProblemSets/problemSet.vue";
export default {
	components: {
		problemSet,
	},
	data() {
		return {
			problemSets: [],
			adding: false,
			loading: false,
			infoForm: {
				timeRange: "",
				name: "",
				announcement: "",
			},
			infoRules: {
				timeRange: [
					{
						required: true,
						message: "请选择题目集的开始时间与结束时间",
						trigger: ["blur", "change"],
					},
				],
				name: [
					{
						required: true,
						message: "请输入题目集的名称",
						trigger: ["blur", "change"],
					},
				],
				announcement: [
					{
						required: true,
						message: "请输入题目集的公告",
						trigger: ["blur", "change"],
					},
				],
			},
		};
	},
	methods: {
		async getProblemsetList() {
			this.loading = true;
			let res = await this.$ajax.post(
				"/problemset/getProblemsetList",
				{},
				{
					headers: {
						Authorization: `Bearer ${this.$store.state.token}`,
					},
				}
			);
			if (res.data.code == 0) {
				this.problemSets = res.data.data;
			}
			this.loading = false;
		},
		createProblemSet() {
			this.$refs.infoForm.validate(async (valid) => {
				if (valid) {
					let res = await this.$ajax.post(
						"/problemset/createProblemset",
						{
							name: this.infoForm.name,
							announcement: this.infoForm.announcement,
							beginTime: this.formatDate(
								this.infoForm.timeRange[0]
							),
							endTime: this.formatDate(
								this.infoForm.timeRange[1]
							),
						},
						{
							headers: {
								Authorization: `Bearer ${this.$store.state.token}`,
							},
						}
					);
					if (res.data.code == 0) {
						this.$notify({
							title: "成功",
							message: "成功创建题目集",
							type: "success",
						});
						this.getProblemsetList();
						this.adding = false;
					} else {
						this.$notify({
							title: "失败",
							message: `${res.data.message}`,
							type: "success",
						});
					}
				}
			});
		},
		formatDate(date) {
			let year = date.getFullYear();
			let month = date.getMonth() + 1;
			let day = date.getDate();
			let hour = date.getHours();
			let min = date.getMinutes();
			let sec = date.getSeconds();
			return (
				year +
				"-" +
				(month < 10 ? "0" + month : month) +
				"-" +
				(day < 10 ? "0" + day : day) +
				" " +
				(hour < 10 ? "0" + hour : hour) +
				":" +
				(min < 10 ? "0" + min : min) +
				":" +
				(sec < 10 ? "0" + sec : sec)
			);
		},
	},
	created() {
		if (!this.$store.state.myInfo.roles.includes("ROLE_TEACHER")) {
			this.$router.replace("/");
		} else {
			this.getProblemsetList();
		}
	},
};
</script>

<style scoped>
.ul li {
	margin-block: 20px;
}
.input {
	width: 100%;
}
</style>