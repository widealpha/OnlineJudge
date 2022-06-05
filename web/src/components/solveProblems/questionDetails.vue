<template>
	<div>
		<div class="details" style="width: 100%; position: relative" v-loading="loading">
			<el-row>
				<el-col :span="18">
					<mavon-editor style="height: 570px;" class="md" :value="description" :subfield="false" :defaultOpen="'preview'" :toolbarsFlag="false" :editable="false" :scrollStyle="true" :ishljs="true" />
				</el-col>
				<el-col :span="5" :offset="1">
					<el-card style="text-align: left; height: 15vh" shadow="hover">
						<div>
							<span><i class="el-icon-medal" />作者</span>
							<span style="float: right; font-weight: bold">{{author===""?"佚名":author}}</span>
						</div>
						<div>
							<span>代码长度限制</span>
							<span style="float: right">{{codeMax===undefined?"--":codeMax}} KB</span>
						</div>
						<div>
							<span>时间限制</span>
							<span style="float: right">{{timeMax===undefined?"--":timeMax}} ms</span>
						</div>
						<div>
							<span>内存限制</span>
							<span style="float: right">{{memoryMax===undefined?"--":memoryMax}} MB</span>
						</div>
					</el-card>

					<el-card style="margin-top: 15px; overflow: visible" shadow="hover">
						<div slot="header" style="text-align: left; height: 15px; line-height: 15px;">
							<span>通过率</span>
						</div>
						<div id="passRadio" style="height: 166px;" />
					</el-card>

					<el-card style="margin-top: 15px; height: 160px; overflow-y: scroll;" shadow="hover">
						<div slot="header" style="text-align: left;">
							<span>题目标签</span>
						</div>
						<div style="display: flex; flex-flow: wrap;">
							<el-tag @click="searchProblemsByTag(tag.name)" effect="dark" style="margin-left: 10px; margin-bottom: 1em; cursor: pointer;" v-for="tag in myTags" :key="tag.id">
								{{tag.name}}
							</el-tag>
						</div>
					</el-card>
				</el-col>
			</el-row>
		</div>
	</div>
</template>


<script>
import Vue from "vue";
import mavonEditor from "mavon-editor";
import "mavon-editor/dist/css/index.css";
Vue.use(mavonEditor);
import $echarts from "../../echarts";
export default {
	name: "questiondetails",
	props: {
		problemId: Number,
		title: String,
	},
	data() {
		return {
			description: `# 描述\n* 要点`,
			author: "",
			codeMax: undefined,
			timeMax: undefined,
			memoryMax: undefined,
			passRadio: 0,
			loading: false,
			myTags: [],
		};
	},
	methods: {
		myEcharts() {
			let myChart = $echarts.getInstanceByDom(
				document.getElementById("passRadio")
			);
			// 基于准备好的dom，初始化echarts实例
			if (myChart === undefined) {
				myChart = $echarts.init(document.getElementById("passRadio"));
			}
			// 使用指定的配置项和数据显示图表。
			myChart.setOption({
				legend: {
					top: "top",
				},
				tooltip: {
					trigger: "item",
					formatter: "{b}: {c}<br/>({d}%)",
				},
				series: [
					{
						type: "pie",
						stillShowZeroSum: false,
						radius: "80%",
						top: "15%",
						data: [
							{
								value: this.passRadio,
								name: "AC",
							},
							{
								value: Number(100 - this.passRadio).toFixed(1),
								name: "WA",
							},
						],
						label: {
							show: true,
							position: "inner",
							color: "#ffffff",
							formatter: (params) => {
								if (params.value == 0) {
									return "";
								} else {
									return `${params.name}: ${params.value}`;
								}
							},
						},
						emphasis: {
							itemStyle: {
								shadowBlur: 10,
								shadowOffsetX: 0,
								shadowColor: "rgba(0, 0, 0, 0.5)",
							},
						},
					},
				],
				color: ["#32CD32", "#FF1111"],
			});
		},
		async updatePassRadio() {
			let res = await this.$ajax.post(
				"/problem/getProblemById",
				{
					id: this.problemId,
				},
				{
					headers: {
						Authorization: `Bearer ${localStorage.getItem("token")}`,
					},
				}
			);
			if (res.data.code == 0) {
				this.passRadio = Number(res.data.data.passRadio * 100).toFixed(
					1
				);
				this.myEcharts();
			}
		},
		async getDetails(problemId) {
			this.loading = true;
			let res = await this.$ajax.post(
				"/problem/info",
				{
					id: problemId,
				},
				{
					headers: {
						Authorization: `Bearer ${localStorage.getItem("token")}`,
					},
				}
			);
			if (res.data.code == 0) {
				if (res.data.data != null) {
					this.author = res.data.data.author;
					this.description = res.data.data.description;
					this.myTags = res.data.data.tagList;
					this.passRadio = Number(
						res.data.data.passRadio * 100
					).toFixed(1);
					this.myEcharts();
					this.$emit("update:title", res.data.data.title);
					this.getLimits(problemId);
				} else {
					this.$message({
						message: "查无此题",
						type: "error",
						duration: 1000,
					});
					this.$emit("update:title", "查无此题");
					this.author = "";
					this.description =
						"```\n╔═╗╦═╗╦═╗╔═╗╦═╗\n║╣ ╠╦╝╠╦╝║ ║╠╦╝\n╚═╝╩╚═╩╚═╚═╝╩╚═\n```";
				}
			}
			this.loading = false;
			return res.data.data;
		},
		async getLimits(problemId) {
			let res = await this.$ajax.post(
				"/problemLimit/getProblemLimit",
				{
					id: problemId,
				},
				{
					headers: {
						Authorization: `Bearer ${localStorage.getItem("token")}`,
					},
				}
			);
			if ((res.data.code = 0 && res.data.data != null)) {
				this.codeMax = res.data.data.codeMax;
				this.timeMax = res.data.data.timeMax;
				this.memoryMax = res.data.data.memoryMax;
			}
		},
		searchProblemsByTag(tag) {
			this.$router.push({
				path: `/problem-set/${this.$store.state.problemSetInfo.problemSetId}/problems`,
				query: { tag: tag },
			});
		},
	},
};
</script>

<style scoped>
</style>