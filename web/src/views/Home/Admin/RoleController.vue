<template>
	<div>
		<el-table v-loading="loading" :data="teachers" style="width: 100%" border stripe>
			<el-table-column prop="id" label="USER_ID">
			</el-table-column>
			<el-table-column label="操作">
				<template slot-scope="scope">
					<el-button @click.native.prevent="removeTeacherRole(scope.$index)" type="text" size="small">
						移除
					</el-button>
				</template>
			</el-table-column>
		</el-table>
		<div>
			<el-input-number v-model="userId" @change="validNum()" :precision="0" :min="2" label="描述文字"></el-input-number>
			<el-button @click="addTeacherRole">给予教师权限</el-button>
		</div>
	</div>
</template>

<script>
export default {
	name: "RoleController",
	data() {
		return {
			teachers: [],
			userId: 2,
			loading: true,
		};
	},
	methods: {
		validNum() {
			if (isNaN(this.userId)) {
				this.userId = 2;
			}
		},
		async addTeacherRole() {
			let id = parseInt(this.userId);
			let res = await this.$ajax.post(
				"/role/addTeacherRole",
				{
					userId: id,
				},
				{
					headers: {
						Authorization: `Bearer ${localStorage.getItem("token")}`,
					},
				}
			);
			switch (res.data.code) {
				case 2007:
					this.$alert(res.data.message);
					break;
				case 0:
					this.$alert("添加成功");
					this.teachers.push({ id: id });
					break;
			}
			this.userId = "";
		},
		async removeTeacherRole(index) {
			let id = this.teachers[index].id;
			let res = await this.$ajax.post(
				"/role/removeTeacherRole",
				{
					userId: id,
				},
				{
					headers: {
						Authorization: `Bearer ${localStorage.getItem("token")}`,
					},
				}
			);
			switch (res.data.code) {
				case 2007:
					this.$alert(res.data.message);
					break;
				case 0:
					this.$alert("成功移除");
					this.teachers.splice(index, 1);
					break;
			}
		},
		async updateTeacherList() {
			this.teachers = [];
			let res = await this.$ajax.get(
				"/role/teacherList",
				{},
				{
					headers: {
						Authorization: `Bearer ${localStorage.getItem("token")}`,
					},
				}
			);
			for (let i = 0; i < res.data.data.length; i++) {
				this.teachers.push({ id: res.data.data[i] });
			}
			this.loading = false;
		},
	},
	created() {
		if (!this.$store.state.myInfo.roles.includes("ROLE_ADMIN")) {
			this.$router.replace("/");
		} else {
			this.updateTeacherList();
		}
	},
};
</script>

<style scoped>
</style>