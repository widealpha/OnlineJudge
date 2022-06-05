<template>
	<div>
		<excel-import :upload="submitUpload" ref="excelImport" />
	</div>
</template>

<script>
import ExcelImport from "@/components/excelImport.vue";
export default {
	name: "Excel",
	components: {
		ExcelImport,
	},
	data() {
		return {
			loading: false,
			students: [],
			fileRes: "",
		};
	},
	methods: {
		async submitUpload() {
			this.loading = true;
			let res = await this.$ajax.post(
				"/bind/uploadStudentExcel",
				this.$refs.excelImport.formdata,
				{
					headers: {
						Authorization: `Bearer ${localStorage.getItem("token")}`,
						"Content-Type": "multipart/form-data",
					},
					responseType: "blob", //服务器返回的数据类型
				}
			);
			return res;
		},
	},
	beforeCreate() {
		if (!this.$store.state.myInfo.roles.includes("ROLE_ADMIN")) {
			this.$router.replace("/");
		}
	},
};
</script>

<style scoped>
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