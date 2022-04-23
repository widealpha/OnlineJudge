<template>
	<div>
		<ojheader></ojheader>
		<el-container>
			<ojaside :pageIndex="'2-1'"></ojaside>
			<el-main>
				<div class="head" style="height: 10vh">
					<span>难度：</span>
					<el-rate v-model="difficulty" @change="forSearch = false; changePage()" :inline="true"></el-rate>
					<el-input placeholder="请输入内容" prefix-icon="el-icon-search" v-model="key" style="width: 15vw">
					</el-input>
					<el-button style="margin-left: 10px" type="primary" :disabled="loading" @click="search">查找</el-button>
					<el-pagination :total="total" layout="prev, pager, next, jumper, sizes" :current-page.sync="page" :page-size.sync="size" @size-change="changePage" @current-change="changePage">
					</el-pagination>
				</div>
				<div>
					<el-table v-loading="loading" :data="problems" style="width: 100%" border stripe>
						<el-table-column type="expand" width="50">
							<template slot-scope="props">
								<el-card>
									<div slot="header">题目描述</div>
									<mavon-editor :value="props.row.description" :toolbarsFlag="false" :editable="false" :subfield="false" :defaultOpen="'preview'" fontSize="16px" />
								</el-card>
							</template>
						</el-table-column>
						<el-table-column prop="id" label="ID" width="50">
						</el-table-column>
						<el-table-column prop="title" label="标题" width="100" show-overflow-tooltip>
						</el-table-column>
						<el-table-column prop="author" label="作者ID" width="80">
						</el-table-column>
						<el-table-column prop="difficulty" label="难度" width="50">
						</el-table-column>
						<el-table-column prop="createDate" label="出题时间">
						</el-table-column>
						<el-table-column prop="lastModifiedDate" label="最后修改时间">
						</el-table-column>
						<el-table-column prop="passRadio" label="通过率" width="100">
							<template slot-scope="props">
								{{Number(props.row.passRadio*100).toFixed(1) + "%"}}
							</template>
						</el-table-column>
						<el-table-column label="操作" width="75">
							<template slot-scope="scope">
								<el-button type="text" @click="solve(scope.$index)">做题</el-button>
							</template>
						</el-table-column>
					</el-table>
				</div>
			</el-main>
		</el-container>
	</div>
</template>

<script>
import Vue from "vue";
import mavonEditor from "mavon-editor";
import "mavon-editor/dist/css/index.css";
Vue.use(mavonEditor);
import Ojaside from "../../components/ojaside.vue";
import Ojheader from "../../components/ojheader.vue";
export default {
	name: "problems",
	components: {
		Ojheader,
		Ojaside,
	},
	data() {
		return {
			size: 10,
			page: 1,
			total: 0,
			difficulty: 1,
			problems: [],
			loading: true,
			key: "",
			forSearch: false,
		};
	},
	methods: {
		async changePage() {
			this.loading = true;
			let res = await this.$ajax.post(
				this.forSearch
					? "/problem/getProblemByKey"
					: "/problem/getProblemByDifficulty",
				this.forSearch
					? {
							key: this.key,
							page: this.page,
							size: this.size,
					  }
					: {
							difficulty: this.difficulty,
							page: this.page,
							size: this.size,
					  },
				{
					headers: {
						Authorization: `Bearer ${this.$store.state.token}`,
					},
				}
			);
			this.problems = res.data.data.rows;
			this.total = res.data.data.total;
			this.loading = false;
		},
		async search() {
			this.forSearch = true;
			this.loading = true;
			this.difficulty = 0;
			let res = await this.$ajax.post(
				"/problem/getProblemByKey",
				{
					key: this.key,
					page: this.page,
					size: this.size,
				},
				{
					headers: {
						Authorization: `Bearer ${this.$store.state.token}`,
					},
				}
			);
			this.problems = res.data.data.rows;
			this.total = res.data.data.total;
			this.loading = false;
		},
		solve(index) {
			this.$router.push(`/problems/${this.problems[index].id}`);
		},
	},
	beforeCreate() {
		if (localStorage.getItem("token") == null) {
			this.$router.replace("/");
		}
	},
	async created() {
		let res = await this.$ajax.post(
			"/problem/getProblemByDifficulty",
			{
				difficulty: this.difficulty,
				page: this.page,
				size: this.size,
			},
			{
				headers: {
					Authorization: `Bearer ${this.$store.state.token}`,
				},
			}
		);
		this.problems = res.data.data.rows;
		this.total = res.data.data.total;
		this.loading = false;
	},
};
</script>

<style>
.el-header {
	background-color: #b3c0d1;
	color: #333;
	line-height: 60px;
}
.head div {
	display: inline-block;
}
.el-aside {
	color: #333;
}
</style>
