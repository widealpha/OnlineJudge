<template>
	<div>
		<el-card>
			<div slot="header">
				<el-row style="height: 5vh;">
					<el-col :span="12" style="text-align: left; line-height: 5vh;">
						<span style="font-weight: bolder; color: gray;">{{problemSetInfo.name}}</span>
					</el-col>
					<el-col :span="12" style="text-align: right; line-height: 5vh;">
						<el-button v-if="problemSetInfo.isMyProblemSet" icon="el-icon-edit" style="float: right;" @click="alteringInfo=true;">更新</el-button>
					</el-col>
				</el-row>
			</div>
			<div>
				<el-row>
					<el-col :span="12">
						<el-descriptions title="基本信息" :column="1">
							<el-descriptions-item label="作者">
								<el-tag size="small">{{problemSetInfo.author}}</el-tag>
							</el-descriptions-item>
							<el-descriptions-item label="开始时间">
								{{problemSetInfo.beginTime}}
							</el-descriptions-item>
							<el-descriptions-item label="结束时间">
								{{problemSetInfo.endTime}}
							</el-descriptions-item>
							<el-descriptions-item label="题目集状态">
								<el-tag size="small" :type="problemSetStatus==='进行中'?'':'info'">{{problemSetStatus}}</el-tag>
							</el-descriptions-item>
						</el-descriptions>
					</el-col>
					<el-col :span="12">
						<el-card style=" text-align: left">
							<div slot="header">
								<span style="font-weight: bolder; color: gray">题目集公告</span>
							</div>
							{{problemSetInfo.announcement}}
						</el-card>
					</el-col>
				</el-row>
			</div>
		</el-card>
		<el-card style="margin-top: 10px;">
			<div slot="header">
				<el-row style="height: 5vh;">
					<el-col :span="12" style="text-align: left;">
						<span style="font-weight: bolder; color: gray; line-height: 5vh;">题目列表</span>
					</el-col>
					<el-col :span="12" style="text-align: right;">
						<el-button v-if="problemSetInfo.isMyProblemSet" style="float: right;" @click="$router.push({path:`/problem-set/${$route.params.problemSetId}/allProblems`})">题目管理</el-button>
					</el-col>
				</el-row>
			</div>
			<div>
				<el-table :data="problemSetInfo.problems">
					<el-table-column type="index" label="标号" width="75"></el-table-column>
					<el-table-column prop="id" label="题号" width="75"></el-table-column>
					<el-table-column prop="title" label="标题">
						<template slot-scope="props">
							<el-link type="primary" @click="getToProblem(props.$index + 1, props.row)">{{props.row.title}}</el-link>
						</template>
					</el-table-column>
					<el-table-column prop="passRadio" label="通过率">
						<template slot-scope="props">
							{{Number(props.row.passRadio*100).toFixed(1) + "%"}}
						</template>
					</el-table-column>
				</el-table>
			</div>
		</el-card>

		<div class="others">
			<el-dialog title="题目集概况" :append-to-body="true" :visible.sync="alteringInfo" width="35%" @open="initInfoForm">
				<el-form label-position="top" :model="infoForm" :rules="infoRules" ref="infoForm">
					<el-form-item label=" 新时间范围" prop="timeRange">
						<el-date-picker style="width: 100%" v-model="infoForm.timeRange" type="datetimerange" range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间">
						</el-date-picker>
					</el-form-item>
					<el-form-item label="新名称" prop="name">
						<el-input v-model="infoForm.name" class="input"></el-input>
					</el-form-item>
					<el-form-item label="新公告" prop="announcement">
						<el-input v-model="infoForm.announcement" type="textarea" class="input"></el-input>
					</el-form-item>
					<el-form-item label="题目集状态">
						<el-select v-model="infoForm.status">
							<el-option v-for="item in statuses" :key="item.value" :label="item.label" :value="item.value">
							</el-option>
						</el-select>
					</el-form-item>
					<el-form-item label="是否允许使用在线判题">
						<el-switch v-model="infoForm.canUseOnlineJudge" active-color="#13ce66" inactive-color="#ff4949" />
					</el-form-item>
					<el-form-item label="是否显示测试点提示">
						<el-switch v-model="infoForm.canViewTestPoint" active-color="#13ce66" inactive-color="#ff4949" />
					</el-form-item>
				</el-form>
				<el-button type="primary" @click="alterInfo">提交</el-button>
			</el-dialog>
		</div>
	</div>
</template>

<script>
export default {
	data() {
		return {
			alteringInfo: false,
			infoForm: {
				timeRange: "",
				name: "",
				announcement: "",
				status: 0,
				canUseOnlineJudge: true,
				canViewTestPoint: true,
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
				status: [
					{
						required: true,
						message: "请选择题目集状态",
						trigger: ["blur", "change"],
					},
				],
			},
			statuses: [
				{ label: "完全公开", value: 1 },
				{ label: "教师间公开", value: 2 },
				{ label: "私有", value: 3 },
			],
		};
	},
	methods: {
		getToProblem(index, problem) {
			this.$router.push({
				path: `/problem-set/${this.$route.params.problemSetId}/problems/${problem.id}`,
			});
		},
		changeIndex(index) {
			this.menuIndex = index;
		},
		initInfoForm() {
			this.infoForm.name = this.problemSetInfo.name;
			this.infoForm.announcement = this.problemSetInfo.announcement;
			this.infoForm.timeRange = [
				new Date(this.problemSetInfo.beginTime),
				new Date(this.problemSetInfo.endTime),
			];
			this.infoForm.status = this.problemSetInfo.status;
			this.infoForm.canUseOnlineJudge =
				this.problemSetInfo.canUseOnlineJudge;
			this.infoForm.canViewTestPoint =
				this.problemSetInfo.canViewTestPoint;
		},
		alterInfo() {
			this.$refs.infoForm.validate(async (valid) => {
				if (valid) {
					let res = await this.$ajax.post(
						"/problemset/alterInfo",
						{
							id: this.$route.params.problemSetId,
							name: this.infoForm.name,
							announcement: this.infoForm.announcement,
							beginTime: this.formatDate(
								this.infoForm.timeRange[0]
							),
							endTime: this.formatDate(
								this.infoForm.timeRange[1]
							),
							status: this.infoForm.status,
							canUseOnlineJudge: this.infoForm.canUseOnlineJudge
								? 1
								: 0,
							canViewTestPoint: this.infoForm.canViewTestPoint
								? 1
								: 0,
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
							message: "题目集信息修改成功",
							type: "success",
						});
						this.$store.commit("setProblemSetInfo", {
							name: this.infoForm.name,
							announcement: this.infoForm.announcement,
							beginTime: this.formatDate(
								this.infoForm.timeRange[0]
							),
							endTime: this.formatDate(
								this.infoForm.timeRange[1]
							),
							status: this.infoForm.status,
							canUseOnlineJudge: this.infoForm.canUseOnlineJudge,
							canViewTestPoint: this.infoForm.canViewTestPoint,
						});
						this.alteringInfo = false;
					} else {
						this.$notify({
							title: "失败",
							message: `${res.data.message}`,
							type: "error",
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
	computed: {
		problemSetInfo() {
			return this.$store.state.problemSetInfo;
		},
		isMyProblemSet() {
			return this.$store.state.problemSetInfo.isMyProblemSet;
		},
		problemSetStatus() {
			let beginTime = new Date(this.problemSetInfo.beginTime);
			let endTime = new Date(this.problemSetInfo.endTime);
			let nowTime = new Date();
			if (nowTime >= endTime) {
				return "已关闭";
			} else if (nowTime <= beginTime) {
				return "未开始";
			} else {
				return "进行中";
			}
		},
	},
};
</script>