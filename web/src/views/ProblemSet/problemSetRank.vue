<template>
	<div class="rank">
		<div class="basicInfo">
			<span class="totalNum">
				题目总数 : {{ this.total_num }}
			</span>
			<el-button :disabled="tableData.length===0" class="export" size="small" type="text" @click="exportExcel">
				导出excel文件
				<i class="el-icon-download"></i>
			</el-button>
			<el-button class="refresh" type="primary" @click="getRank">刷新</el-button>
		</div>
		<div class="rankTable">
			<el-table id="out-table" :data="tableData" border style="width:100%">
				<el-table-column label="排名" prop="rank" width="100">
				</el-table-column>
				<el-table-column label="ID" prop="userId" width="200">
				</el-table-column>
				<el-table-column label="通过数" prop="acNum">
				</el-table-column>
				<el-table-column label="分数" prop="score">
				</el-table-column>
			</el-table>
		</div>
	</div>
</template>

<script>
import FileSaver from "file-saver";
import XLSX from "xlsx";

export default {
	name: "problemSetRank",
	data() {
		return {
			total_num: 0,
			tableData: [],
		};
	},
	mounted() {
		 (this.$store.state.problemSetInfo.problemSetId);
		this.getRank();
	},
	methods: {
		exportExcel() {
			this.$message({
				message: "正在导出中,请稍后...",
				type: "success",
			});
			/* out-table关联导出的dom节点  */
			let wb = XLSX.utils.table_to_book(
				document.querySelector("#out-table")
			);
			/* get binary string as output */
			let wbout = XLSX.write(wb, {
				bookType: "xlsx",
				bookSST: true,
				type: "array",
			});
			try {
				FileSaver.saveAs(
					new Blob([wbout], { type: "application/octet-stream" }),
					"成绩排行榜.xlsx"
				);
			} catch (e) {
				if (typeof console !== "undefined")  (e, wbout);
			}

			return wbout;
		},

		async getRank() {
			let res = await this.$ajax.post(
				"/problemset/getProblemsetRank",
				{
					id: this.$store.state.problemSetInfo.problemSetId,
				},
				{
					headers: {
						Authorization: `Bearer ${this.$store.state.token}`,
					},
				}
			);
			 (res);
			this.tableData = res.data.data.rankList;
			this.total_num = this.tableData.length;
		},
	},
};
</script>

<style lang="less" scoped>
.rank {
	position: relative;
	//background: yellow;
	width: 100%;
	left: 0;
	right: 0;
	//height: 100px;

	.basicInfo {
		position: relative;
		width: 100%;
		left: 0;
		right: 0;
		//background: red;
		height: 80px;
		line-height: 80px;

		.totalNum {
			position: absolute;
			left: 20px;
		}

		.export {
			float: right;
			color: #888888;
			position: absolute;
			right: 120px;
			bottom: 25px;
		}

		.refresh {
			position: absolute;
			right: 20px;
			bottom: 20px;
		}
	}

	.rankTable {
		box-sizing: border-box;
		padding: 0 20px;
	}
}
</style>
