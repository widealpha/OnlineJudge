<template>
	<div>
		<div class="head" style="height: 5vh">
			<el-pagination v-if="!forSearch" :total="total" layout="prev, pager, next, jumper, sizes, slot" :current-page.sync="page" :page-size.sync="size" @size-change="changePage" @current-change="changePage">
				<el-button type="text" @click="showSearch=true">查询绑定信息<i class="el-icon-search"></i></el-button>
				<el-button type="text" @click="output">导出所有绑定信息<i class="el-icon-copy-document"></i></el-button>
			</el-pagination>
			<div v-if="forSearch">
				<el-button type="text" @click="forSearch=false; changePage()">返回分页<i class="el-icon-copy-document"></i></el-button>
				<el-button type="text" @click="showSearch=true">查询绑定信息<i class="el-icon-search"></i></el-button>
			</div>
		</div>
		<el-table v-loading="loading" :data="students" style="width: 100%" border stripe>
			<el-table-column prop="id" label="ID">
			</el-table-column>
			<el-table-column prop="userId" label="账号">
			</el-table-column>
			<el-table-column prop="stuNum" label="学号">
			</el-table-column>
			<el-table-column prop="name" label="姓名">
			</el-table-column>
			<el-table-column prop="code" label="绑定码">
			</el-table-column>
			<el-table-column label="操作">
				<template slot-scope="scope">
					<el-button @click.native.prevent="remove(scope.$index)" type="text" size="small">
						移除
					</el-button>
					<el-button @click.native.prevent="isUpdating = true; updateIndex = scope.$index" type="text" size="small">
						更新
					</el-button>
					<el-button v-if="!scope.row.userId" @click.native.prevent="bind(scope.$index)" type="text" size="small">
						绑定
					</el-button>
					<el-button v-if="scope.row.userId" style="color: red;" @click.native.prevent="deleteBind(scope.row)" type="text" size="small">
						解绑
					</el-button>
				</template>
			</el-table-column>
		</el-table>

		<el-dialog title="更新绑定信息" :visible.sync="isUpdating" @closed="$refs.updateForm.resetFields()" @open="initForm">
			<el-form :model="updateForm" ref="updateForm" :rules="updateRules">
				<el-form-item prop="stuNum" label="学号">
					<el-input v-model="updateForm.stuNum"></el-input>
				</el-form-item>
				<el-form-item prop="name" label="姓名">
					<el-input v-model="updateForm.name"></el-input>
				</el-form-item>
				<el-form-item prop="code" label="绑定码">
					<el-input v-model="updateForm.code"></el-input>
				</el-form-item>
			</el-form>
			<div slot="footer" class="dialog-footer">
				<el-button type="primary" @click="update">确 定</el-button>
			</div>
		</el-dialog>

		<el-dialog title="查询绑定信息" :visible.sync="showSearch" @closed="$refs.searchForm.resetFields()">
			<el-form :model="searchForm" ref="searchForm" :rules="searchRules">
				<el-form-item prop="stuNum" label="学号">
					<el-input v-model="searchForm.stuNum"></el-input>
				</el-form-item>
				<el-form-item prop="name" label="姓名">
					<el-input v-model="searchForm.name"></el-input>
				</el-form-item>
			</el-form>
			<div slot="footer" class="dialog-footer">
				<el-button type="primary" @click="search">确 定</el-button>
			</div>
		</el-dialog>
		<el-backtop></el-backtop>
	</div>
</template>

<script>
export default {
	data() {
		return {
			size: 10,
			page: 1,
			total: 0,
			students: [],
			loading: true,
			isUpdating: false,
			updateIndex: 0,
			updateForm: {
				name: "",
				code: "",
				stuNum: "",
			},
			updateRules: {
				name: [
					{
						required: true,
						message: "请输入学生姓名",
						trigger: ["change", "blur"],
					},
				],
				code: [
					{
						required: true,
						pattern: /^[A-Z]+$/,
						message: "绑定码必须全为大写字母",
						trigger: ["change", "blur"],
					},
				],
				stuNum: [
					{
						required: true,
						pattern: /^[0-9]+$/,
						message: "学号必须为纯数字",
						trigger: ["change", "blur"],
					},
				],
			},
			showSearch: false,
			forSearch: false,
			searchForm: {
				name: "",
				stuNum: "",
			},
			searchRules: {
				name: [
					{
						message: "请输入学生姓名",
						trigger: ["change", "blur"],
					},
				],
				stuNum: [
					{
						pattern: /^[0-9]+$/,
						message: "学号必须为纯数字",
						trigger: ["change", "blur"],
					},
				],
			},
		};
	},
	methods: {
		async remove(index) {
			this.loading = true;
			const student = this.students[index];
			let res = await this.$ajax.post(
				"/bind/deleteStudentBind",
				{
					name: student.name,
					code: student.code,
					stuNum: student.stuNum,
				},
				{
					headers: {
						Authorization: `Bearer ${this.$store.state.token}`,
					},
				}
			);
			if (res.data.code == 0) {
				if (this.forSearch == false) {
					res = await this.$ajax.get(
						"/bind/allStudentsBind",
						{
							size: 1,
							page: this.page * this.size,
						},
						{
							headers: {
								Authorization: `Bearer ${this.$store.state.token}`,
							},
						}
					);
					if (res.data.data.rows[0]) {
						this.students.push(res.data.data.rows[0]);
					}
				}
				this.$message({
					message: "成功解除绑定",
					type: "success",
					duration: 1000,
				});
			} else {
				this.$alert(res.data.message);
			}
			this.students.splice(index, 1);
			this.loading = false;
		},
		bind(index) {
			const student = this.students[index];
			this.$prompt("绑定账户", "请输入需要绑定的学生用户id", {
				confirmButtonText: "确定",
				cancelButtonText: "取消",
				inputPattern: /^[0-9]+$/,
				inputErrorMessage: "仅允许输入数字",
			})
				.then(async ({ value }) => {
					let res = await this.$ajax.post(
						"/bind/adminBindStuNum",
						{
							userId: value,
							name: student.name,
							stuNum: student.stuNum,
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
							message: "用户绑定成功",
							type: "success",
						});
						this.changePage();
					} else {
						this.$notify({
							title: "失败",
							message: "用户绑定失败",
							type: "error",
						});
					}
				})
				.catch(() => {});
		},
		initForm() {
			const index = this.updateIndex;
			let student = this.students[index];
			this.updateForm.name = student.name;
			this.updateForm.code = student.code;
			this.updateForm.stuNum = student.stuNum;
		},
		update() {
			this.$refs.updateForm.validate(async (valid) => {
				if (valid) {
					const index = this.updateIndex;
					let student = this.students[index];
					let res = await this.$ajax.post(
						"/bind/updateStudentInfoById",
						{
							id: student.id,
							name: this.updateForm.name,
							code: this.updateForm.code,
							stuNum: this.updateForm.stuNum,
						},
						{
							headers: {
								Authorization: `Bearer ${this.$store.state.token}`,
							},
						}
					);
					switch (res.data.code) {
						case 0:
							this.$message({
								message: "更新成功",
								type: "success",
								duration: 1000,
							});
							student.name = this.updateForm.name;
							student.stuNum = this.updateForm.stuNum;
							student.code = this.updateForm.code;
							break;
						default:
							this.$alert(res.data.message);
							break;
					}
					this.isUpdating = false;
				}
			});
		},
		async changePage() {
			this.loading = true;
			let res = await this.$ajax.get(
				"/bind/allStudentsBind",
				{
					size: this.size,
					page: this.page,
				},
				{
					headers: {
						Authorization: `Bearer ${this.$store.state.token}`,
					},
				}
			);
			this.students = res.data.data.rows;
			this.total = res.data.data.total;
			this.loading = false;
		},
		search() {
			this.$refs.searchForm.validate(async (valid) => {
				if (valid) {
					if (!this.searchForm.name && !this.searchForm.stuNum) {
						this.$message({
							message: "至少填写一项查询项！",
							type: "error",
							duration: 1000,
						});
					} else {
						this.loading = true;
						let res = await this.$ajax.post(
							"/bind/searchStudentInfo",
							{
								name: this.searchForm.name,
								stuNum: this.searchForm.stuNum,
							},
							{
								headers: {
									Authorization: `Bearer ${this.$store.state.token}`,
								},
							}
						);
						this.showSearch = false;
						this.students = res.data.data;
						this.forSearch = true;
						this.loading = false;
					}
				}
			});
		},
		async output() {
			let res = await this.$ajax.get(
				"/bind/allStudentsBindExcel",
				{},
				{
					headers: {
						Authorization: `Bearer ${this.$store.state.token}`,
					},
					responseType: "blob",
				}
			);
			const content = res.data;
			const blob = new Blob([content]); //构造一个blob对象来处理数据
			let fileName = "全部绑定信息.xlsx";
			//对于<a>标签，只有 Firefox 和 Chrome（内核） 支持 download 属性
			//IE10以上支持blob但是依然不支持download
			if ("download" in document.createElement("a")) {
				//支持a标签download的浏览器
				const link = document.createElement("a"); //创建a标签
				link.download = fileName; //a标签添加属性
				link.style.display = "none";
				link.href = URL.createObjectURL(blob);
				document.body.appendChild(link);
				link.click(); //执行下载
				URL.revokeObjectURL(link.href); //释放url
				document.body.removeChild(link); //释放标签
			} else {
				//其他浏览器
				navigator.msSaveBlob(blob, fileName);
			}
		},
		async deleteBind(item) {
			item;
			// let res = await this.$ajax.post(
			// 	"/bind/deleteStudentBind",
			// 	{
			// 		name: item.name,
			// 		code: item.code,
			// 		stuNum: item.stuNum,
			// 	},
			// 	{
			// 		headers: {
			// 			Authorization: `Bearer ${localStorage.getItem(
			// 				"token"
			// 			)}`,
			// 		},
			// 	}
			// );
			// if (res.data.code == 0 && res.data.data) {
			// 	this.$notify({
			// 		title: "成功",
			// 		message: "解绑成功！",
			// 		type: "success",
			// 	});
			// } else {
			// 	this.$notify({
			// 		title: "失败",
			// 		message: "解绑失败！",
			// 		type: "error",
			// 	});
			// }
		},
	},
	async created() {
		if (!this.$store.state.isAdmin) {
			this.$router.replace("/");
		} else {
			this.loading = true;
			let res = await this.$ajax.get(
				"/bind/allStudentsBind",
				{
					size: this.size,
					page: 1,
				},
				{
					headers: {
						Authorization: `Bearer ${this.$store.state.token}`,
					},
				}
			);
			this.students = res.data.data.rows;
			this.total = res.data.data.total;
			this.loading = false;
		}
	},
};
</script>