<template>
	<el-card>
		<div slot="header">
			<span style="float: left">学生信息上传</span>
			<el-upload ref="upload" :auto-upload="false" :headers="headers" :multiple="false" :on-change="onChange" :show-file-list="false" action=''>
				<el-button slot="trigger" size="small" type="primary">
					选择文件<i :class="'el-icon-files'"></i>
				</el-button>
				<el-button :disabled="loading || students.length==0" size="small" style="margin-left: 10px;" type="success" @click="submitUpload">
					点击上传
					<i :class="loading?'el-icon-loading':'el-icon-upload'"></i>
				</el-button>
				<el-button :disabled="fileRes==''" size="small" style="float: right" type="text" @click="outExe">
					导出excel文件
					<i class="el-icon-download"></i>
				</el-button>
			</el-upload>
		</div>
		<el-table :data="students" border stripe style="width: 100%">
			<el-table-column label="学号" prop="id">
			</el-table-column>
			<el-table-column label="姓名" prop="name">
			</el-table-column>
		</el-table>
	</el-card>
</template>

<script>
const XLSX = require("xlsx");

export default {
	name: "excelImport",

	data() {
		return {
			loading: false,
			formdata: {},
			students: [],
			headers: {
				Authorization: `Bearer ${localStorage.getItem("token")}`,
			},
			fileRes: "",
		};
	},
	props: {
		upload: {
			type: Function,
			default: () => {
				return Function;
			},
		},
	},
	methods: {
		onChange(file) {
			this.formdata = new FormData();
			this.formdata.append("file", file.raw);
			this.processFile(file);
		},
		getHeaderRow(sheet) {
			const headers = [];
			/* sheet['!ref']表示所有单元格的范围，例如从A1到F8则记录为 A1:F8*/
			const range = XLSX.utils.decode_range(sheet["!ref"]);
			let C,
				R = range.s.r; /* 从第一行开始 */
			/* 按列进行数据遍历 */
			for (C = range.s.c; C <= range.e.c; ++C) {
				/* 查找第一行中的单元格 */
				const cell = sheet[XLSX.utils.encode_cell({ c: C, r: R })];
				let hdr = "UNKNOWN " + C; // <-- 进行默认值设置
				if (cell && cell.t) {
					hdr = XLSX.utils.format_cell(cell);
				}
				headers.push(hdr);
			}
			return headers;
		},

		processFile(file) {
			this.students = [];
			const reader = new FileReader();
			reader.onload = (e) => {
				/* 解析数据 */
				const bstr = e.target.result;
				const wb = XLSX.read(bstr, { type: "binary" });
				/* 获取文件的第一个工作表（WorkSheet） */
				const wsname = wb.SheetNames[0];
				const ws = wb.Sheets[wsname];
				/* 数组转换 */
				const data = XLSX.utils.sheet_to_json(ws, { header: 1 });
				/* 进行表格数据更新 */
				for (let i = 1; i < data.length; i++) {
					let item = {
						id: String(data[i][0]),
						name: data[i][1],
					};
					this.students.push(item);
				}
				/* 进行表格表头数据更新 */
				this.cols = this.getHeaderRow(ws);
			};
			reader.readAsBinaryString(file.raw);
		},
		async submitUpload() {
			this.loading = true;
			this.fileRes = await this.upload(this.formdata);

			this.loading = false;
			this.$message({
				message: "上传成功！请点击最下方按钮导出xlsx文件",
				type: "success",
			});
		},
		outExe() {
			this.$prompt("此操作将导出excel文件, 是否继续?", "请输入文件名", {
				confirmButtonText: "确定",
				cancelButtonText: "取消",
				type: "warning",
			})
				.then(({ value }) => {
					// this.excelData = this.dataList; //你要导出的数据list。
					this.saveExcel(value);
				})
				.catch(() => {});
		},
		saveExcel(name) {
			const content = this.fileRes.data;
			 (content);
			const blob = new Blob([content]); //构造一个blob对象来处理数据
			let fileName = "绑定码.xlsx";
			if (name) {
				fileName = name + ".xlsx";
			}
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
	},
};
</script>

<style scoped>
.head div {
	display: inline-block;
}
</style>
