<template>
	<div>
		<el-card>
			<div>
				<el-pagination background layout="prev, pager, next" :total="total" :current-page="problemListPage" @current-change="changePage">
				</el-pagination>
				<ul style="list-style: none;" v-loading="loading">
					<li v-for="(item, index) in allProblemList" :key="index" style="margin-bottom: 10px;">
						<el-card shadow="hover" style="text-align: left;">
							<div slot="header">
								<el-row style="height: 2em">
									<el-col :span="12" style="text-align: left; line-height: 2em;">
										<span>
											<i class="el-icon-s-management" style="color: #888;">{{item.id}}</i>
										</span>
										&nbsp;
										<el-link type="primary" @click="showProblemDetails(item)">{{item.title}}</el-link>
									</el-col>
									<el-col :span="12" style="text-align: right; line-height: 2em;">
										<el-button v-if="$store.state.problemSetInfo.problemIds.indexOf(item.id) != -1" type="danger" size="mini" icon="el-icon-minus" @click="delProblem(item)">移除</el-button>
										<el-button v-else type="primary" size="mini" icon="el-icon-plus" @click="addProblem(item)">添加</el-button>
									</el-col>
								</el-row>
							</div>
							<div>
								<el-tag @click="searchProblemsByTag(tag.id)" effect="plain" size="mini" style="margin-left: 10px; cursor: pointer;" v-for="tag in item.tagList" :key="tag.id">
									{{tag.name}}
								</el-tag>
								<span style="float: right"><i class="el-icon-s-opportunity"></i>难度:{{item.difficulty}}</span>
								<span style="float: right; margin-right: 10px;"><i class="el-icon-user-solid"></i>题目作者:{{item.author}}</span>
							</div>
						</el-card>
					</li>
				</ul>
			</div>
		</el-card>

		<div class="others">
			<el-dialog :visible.sync="showDetails" :append-to-body="true" top="50px">
				<el-descriptions :title="problemDetails.title" direction="vertical" :column="4" border>
					<el-descriptions-item label="作者">{{problemDetails.author}}</el-descriptions-item>
					<el-descriptions-item label="难度">{{problemDetails.difficulty}}</el-descriptions-item>
					<el-descriptions-item label="通过率">{{Number(problemDetails.passRadio * 100).toFixed(1)}}%</el-descriptions-item>
					<el-descriptions-item label="更新时间">{{problemDetails.lastModifiedDate}}</el-descriptions-item>
				</el-descriptions>
				<mavon-editor class="md" :value="problemDetails.description" :subfield="false" :defaultOpen="'preview'" :toolbarsFlag="false" :editable="false" :scrollStyle="true" :ishljs="true" />
			</el-dialog>
		</div>
	</div>
</template>

<script>
import Vue from "vue";
import mavonEditor from "mavon-editor";
import "mavon-editor/dist/css/index.css";
Vue.use(mavonEditor);
export default {
	data() {
		return {
			problemListPage: 1,
			problemListSize: 10,
			total: 0,
			allProblemList: [],
			loading: false,
			showDetails: false,
			problemDetails: {},
		};
	},
	methods: {
		async getAllProblems() {
			this.loading = true;
			let res = await this.$ajax.post(
				"/problem/getAllProblem",
				{
					page: this.problemListPage,
					size: this.problemListSize,
				},
				{
					headers: {
						Authorization: `Bearer ${this.$store.state.token}`,
					},
				}
			);
			if (res.data.code === 0) {
				this.allProblemList = res.data.data.rows;
				this.total = res.data.data.total;
			}
			this.loading = false;
		},
		async getProblemsByTag() {
			this.loading = true;
			let res = await this.$ajax.post(
				"/problem/getProblemByTag",
				{
					tagId: parseInt(this.$route.query.tag),
					page: this.problemListPage,
					size: this.problemListSize,
				},
				{
					headers: {
						Authorization: `Bearer ${this.$store.state.token}`,
					},
				}
			);
			if (res.data.code == 0) {
				this.allProblemList = res.data.data.rows;
				this.total = res.data.data.total;
			}
			this.loading = false;
		},
		changePage(current) {
			this.problemListPage = current;
			if (this.$route.query.tag) {
				this.getProblemsByTag();
			} else {
				this.getAllProblems();
			}
		},
		async addProblem(problem) {
			this.loading = true;
			let res = await this.$ajax.post(
				"/problemset/addProblem",
				{
					id: this.$route.params.problemSetId,
					problemId: problem.id,
				},
				{
					headers: {
						Authorization: `Bearer ${this.$store.state.token}`,
					},
				}
			);
			if (res.data.code == 0) {
				res = await this.$ajax.post(
					"/problemset/getProblemset",
					{
						id: this.$route.params.problemSetId,
					},
					{
						headers: {
							Authorization: `Bearer ${this.$store.state.token}`,
						},
					}
				);
				if (res.data.code === 0) {
					let problems = res.data.data.problems;
					let problemIds = [];
					problems.forEach((element) => {
						problemIds.push(element.id);
					});
					this.$store.dispatch("setProblemSetInfo", {
						problems,
						problemIds,
					});
				}
				this.$message({
					message: "添加成功！",
					type: "success",
				});
			} else {
				this.$message({
					message: "添加失败！",
					type: "error",
				});
			}
			this.loading = false;
		},
		async delProblem(problem) {
			this.loading = true;
			let res = await this.$ajax.post(
				"/problemset/deleteProblem",
				{
					id: this.$route.params.problemSetId,
					problemId: problem.id,
				},
				{
					headers: {
						Authorization: `Bearer ${this.$store.state.token}`,
					},
				}
			);
			if (res.data.code == 0) {
				res = await this.$ajax.post(
					"/problemset/getProblemset",
					{
						id: this.$route.params.problemSetId,
					},
					{
						headers: {
							Authorization: `Bearer ${this.$store.state.token}`,
						},
					}
				);
				if (res.data.code === 0) {
					let problems = res.data.data.problems;
					let problemIds = [];
					problems.forEach((element) => {
						problemIds.push(element.id);
					});
					this.$store.dispatch("setProblemSetInfo", {
						problems,
						problemIds,
					});
				}
				this.$message({
					message: "移除成功！",
					type: "success",
				});
			} else {
				this.$message({
					message: "移除失败！",
					type: "error",
				});
			}
			this.loading = false;
		},
		showProblemDetails(details) {
			this.problemDetails = JSON.parse(JSON.stringify(details));
			this.showDetails = true;
		},
		searchProblemsByTag(tag) {
			this.$router.push({
				path: `/problem-set/${this.$store.state.problemSetInfo.problemSetId}/allProblems`,
				query: { tag: tag },
			});
		},
	},
	created() {
		if (!this.$store.state.isTeacher) {
			this.$router.replace(
				`/problem-set/${this.$store.state.problemSetInfo.problemSetId}/`
			);
		} else {
			if (this.$route.query.tag) {
				this.getProblemsByTag();
			} else {
				this.getAllProblems();
			}
		}
	},
	watch: {
		$route(newVal) {
			if (newVal.query.tag) {
				this.getProblemsByTag();
			} else {
				this.getAllProblems();
			}
		},
	},
};
</script>