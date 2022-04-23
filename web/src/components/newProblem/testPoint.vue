<template>
	<div>
		<el-card>
			<div slot="header" class="text">
				<div style="text-align: left;">
					<el-button @click="$emit('update:step', 1);" plain>上一步</el-button>
					<el-button :disabled="points.length===0" style="float: right; margin-right: 20px" @click="$emit('update:step', 3)" plain>下一步</el-button>
				</div>
			</div>
			<div>
				<div style="margin-bottom: 20px;">
					<el-upload action='' ref="upload" :show-file-list="false" :on-change="analyzeZip" :auto-upload="false">
						<el-button slot="trigger" style="margin-left: 10px;" size="mini" type="success">上传测试数据<i class="el-icon-upload"></i></el-button>
						<el-button style="margin-left: 10px;" size="mini" type="primary" @click="downloadTestPoints">下载测试数据<i class="el-icon-download"></i></el-button>
					</el-upload>
				</div>
				<el-table v-loading="loading" :data="points" style="width: 100%" border stripe>
					<el-table-column prop="testName" label="名称" width="180">
					</el-table-column>
					<el-table-column prop="score" label="分数" width="50">
					</el-table-column>
					<el-table-column prop="tip" label="提示">
					</el-table-column>
					<el-table-column label="操作" width="305">
						<template slot-scope="scope">
							<el-button type="danger" @click="remove(scope.$index)" size="small">
								<i class="el-icon-delete"></i>
								移除
							</el-button>
							<el-button type="primary" @click="isUpdating = true; updateIndex = scope.$index" size="small">
								<i class="el-icon-edit"></i>
								更新
							</el-button>
						</template>
					</el-table-column>
					<template slot="append">
						<el-button plain style="width: calc(100% - 1px); margin-bottom: 1px;" @click="isAdding = true;">
							<i class="el-icon-circle-plus-outline"></i>增加测试点
						</el-button>
					</template>
				</el-table>
			</div>
		</el-card>

		<el-dialog title="更新测试点" :visible.sync="isUpdating" @open="initForm" :append-to-body="true">
			<el-form :model="updateForm" ref="updateForm" :rules="updateRules">
				<el-form-item prop="testName" label="名称">
					<el-input v-model="updateForm.testName"></el-input>
				</el-form-item>
				<el-form-item prop="score" label="分数">
					<el-input v-model="updateForm.score"></el-input>
				</el-form-item>
				<el-form-item prop="tip" label="提示">
					<el-input v-model="updateForm.tip"></el-input>
				</el-form-item>
			</el-form>
			<div slot="footer" class="dialog-footer">
				<el-button type="primary" @click="update">确 定</el-button>
			</div>
		</el-dialog>

		<el-dialog title="增加测试点" :visible.sync="isAdding" @open="$refs.addForm?$refs.addForm.resetFields():''" :append-to-body="true">
			<el-form :model="addForm" ref="addForm" :rules="addRules">
				<el-form-item prop="testName" label="名称">
					<el-input v-model="addForm.testName"></el-input>
				</el-form-item>
				<el-form-item prop="score" label="分数">
					<el-input v-model="addForm.score"></el-input>
				</el-form-item>
				<el-form-item prop="in" label="测试点输入">
					<el-input v-model="addForm.in" type="textarea"></el-input>
				</el-form-item>
				<el-form-item prop="out" label="测试点输出">
					<el-input v-model="addForm.out" type="textarea"></el-input>
				</el-form-item>
				<el-form-item prop="tip" label="提示">
					<el-input v-model="addForm.tip"></el-input>
				</el-form-item>
			</el-form>
			<div slot="footer" class="dialog-footer">
				<el-button type="primary" @click="addPoint">确 定</el-button>
			</div>
		</el-dialog>
	</div>
</template>

<script>
import JSZip from "jszip";
export default {
	data() {
		return {
			problemId: 0,
			points: [],
			isUpdating: false,
			isAdding: false,
			updateIndex: 0,
			updateForm: {
				testName: "",
				score: "",
				tip: "",
				time: "",
			},
			updateRules: {
				testName: [
					{
						required: true,
						message: "请输入测试点名称",
						trigger: ["change", "blur"],
					},
				],
				score: [
					{
						required: true,
						pattern: /^[0-9]+$/,
						message: "仅允许输入数字",
						trigger: ["change", "blur"],
					},
				],
			},
			addForm: {
				testName: "",
				score: "",
				in: "",
				out: "",
				tip: "",
				time: "",
			},
			addRules: {
				testName: [
					{
						required: true,
						message: "请输入测试点名称",
						trigger: ["change", "blur"],
					},
				],
				score: [
					{
						required: true,
						pattern: /^[0-9]+$/,
						message: "仅允许输入数字",
						trigger: ["change", "blur"],
					},
				],
			},
			loading: false,
		};
	},
	methods: {
		async getMyTestPoints() {
			this.loading = true;
			let res = await this.$ajax.post(
				"/testPoint/getTestPointInfByProblemId",
				{
					problemId: this.problemId,
				},
				{
					headers: {
						Authorization: `Bearer ${this.$store.state.token}`,
					},
				}
			);
			if (res.data.code == 0) {
				this.points = res.data.data;
			} else {
				this.$message({
					message: res.data.message,
					type: "error",
					showClose: false,
					duration: 1000,
				});
			}
			this.loading = false;
		},
		analyzeZip(file) {
			let jszip = new JSZip();
			jszip
				.loadAsync(file.raw)
				.then(async (zip) => {
					let files = Object.keys(zip.files);
					let allIn = true;
					let points = [];
					zip.file("scores.txt")
						.async("string")
						.then((text) => {
							let arr = text.split("\n");
							for (let i = 0; i < arr.length; i++) {
								if (arr[i] === "") {
									continue;
								}
								let lnSplit = arr[i].split(" ");
								let name = lnSplit[0];
								let score = lnSplit[1];
								let tip = lnSplit[2].substring(
									1,
									lnSplit[2].length - 1
								);
								points.push({
									testName: name,
									score: score,
									tip: tip,
									time: new Date().toLocaleString(),
								});
								let inName = name + ".in";
								let outName = name + ".out";
								if (
									files.indexOf(inName) === -1 ||
									files.indexOf(outName) === -1
								) {
									allIn = false;
									break;
								}
							}
							if (allIn) {
								new Promise((resolve) => {
									let index = 0,
										length = points.length;
									let uploadRequests = [];
									points.forEach((point) => {
										let name = point.testName;
										let inFile, outFile;
										Promise.all([
											zip
												.file(name + ".in")
												.async("blob"),
											zip
												.file(name + ".out")
												.async("blob"),
										]).then(([inContent, outContent]) => {
											inFile = new window.File(
												[inContent],
												name + ".in"
											);
											outFile = new window.File(
												[outContent],
												name + ".out"
											);
											let formData = new FormData();
											formData.append(
												"problemId",
												this.problemId
											);
											formData.append("testName", name);
											formData.append("inFile", inFile);
											formData.append("outFile", outFile);
											formData.append(
												"score",
												point.score
											);
											formData.append("tip", point.tip);
											uploadRequests.push(
												this.$ajax.post(
													"/testPoint/addTestPoint",
													formData,
													{
														headers: {
															Authorization: `Bearer ${this.$store.state.token}`,
															"Content-Type":
																"multipart/form-data",
														},
													}
												)
											);
											if (++index === length) {
												resolve(uploadRequests);
											}
										});
									});
								}).then((uploadRequests) => {
									Promise.all(uploadRequests).then((ress) => {
										for (let i = 0; i < ress.length; i++) {
											let res = ress[i];
											if (res.data.code === 0) {
												this.points.push(
													JSON.parse(
														JSON.stringify(
															points[i]
														)
													)
												);
											}
										}
									});
								});
							} else {
								this.$notify({
									message: "上传的文件缺少部分测试点",
									type: "error",
								});
							}
						});
				})
				.catch(() => {
					this.$notify({
						message: "上传的文件有问题",
						type: "error",
					});
				});
		},
		async remove(index) {
			let point = this.points[index];
			let res = await this.$ajax.post(
				"/testPoint/deleteTestPoint",
				{
					problemId: this.problemId,
					testName: point.testName,
				},
				{
					headers: {
						Authorization: `Bearer ${this.$store.state.token}`,
					},
				}
			);
			if (res.data.code == 0) {
				this.$message({
					message: "成功删除测试点",
					type: "success",
					showClose: false,
					duration: 1000,
				});
				this.points.splice(index, 1);
			}
		},
		update() {
			this.$refs.updateForm.validate((valid) => {
				if (valid) {
					let point = this.points[this.updateIndex];
					point.testName = this.updateForm.testName;
					point.score = this.updateForm.score;
					point.tip = this.updateForm.tip;
					point.time = new Date().toLocaleString();
					this.isUpdating = false;
				}
			});
		},
		initForm() {
			let point = this.points[this.updateIndex];
			this.updateForm.testName = point.testName;
			this.updateForm.score = point.score;
			this.updateForm.tip = point.tip;
		},
		addPoint() {
			this.$refs.addForm.validate(async (valid) => {
				if (valid) {
					this.addForm.time = new Date().toLocaleString();
					this.isAdding = false;
					let inFile = new window.File(
						[this.addForm.in],
						this.addForm.testName + ".in"
					);
					let outFile = new window.File(
						[this.addForm.out],
						this.addForm.testName + ".out"
					);
					let formData = new FormData();
					formData.append("problemId", this.problemId);
					formData.append("testName", this.addForm.testName);
					formData.append("inFile", inFile);
					formData.append("outFile", outFile);
					formData.append("score", this.addForm.score);
					formData.append("tip", this.addForm.tip);
					let res = await this.$ajax.post(
						"/testPoint/addTestPoint",
						formData,
						{
							headers: {
								Authorization: `Bearer ${this.$store.state.token}`,
								"Content-Type": "multipart/form-data",
							},
						}
					);
					if (res.data.code == 0) {
						this.$message({
							message: "添加成功",
							type: "success",
							showClose: false,
							duration: 1000,
						});
						this.points.push(
							JSON.parse(JSON.stringify(this.addForm))
						);
					} else {
						this.$message({
							message: "添加失败",
							type: "error",
							showClose: false,
							duration: 1000,
						});
					}
				}
			});
		},
		async downloadTestPoints() {
			let res = await this.$ajax.post(
				"/testPoint/downLoadTestFile",
				{
					problemId: this.problemId,
				},
				{
					headers: {
						Authorization: `Bearer ${this.$store.state.token}`,
					},
					responseType: "blob", // 以 blob 类型接收后端发回的响应数据
				}
			);
			const content = res.data; // 接收响应内容
			const blob = new Blob([content]); // 构造一个blob对象来处理数据
			let fileName = `测试点${this.problemId}.zip`;
			// 对于<a>标签，只有 Firefox 和 Chrome（内核） 支持 download 属性
			// IE10以上支持blob但是依然不支持download
			if ("download" in document.createElement("a")) {
				//支持a标签download的浏览器
				const link = document.createElement("a"); // 创建a标签
				link.download = fileName; // a标签添加属性
				link.style.display = "none";
				link.href = URL.createObjectURL(blob);
				document.body.appendChild(link);
				link.click(); // 执行下载
				URL.revokeObjectURL(link.href); // 释放url
				document.body.removeChild(link); // 释放标签
			} else {
				// 其他浏览器
				navigator.msSaveBlob(blob, fileName);
			}
		},
	},
	created() {
		let problemId = this.$route.query.problemId;
		if (problemId) {
			this.problemId = Number(problemId);
			this.getMyTestPoints();
		}
	},
	watch: {
		$route(newVal) {
			let problemId = newVal.query.problemId;
			if (problemId) {
				this.problemId = Number(problemId);
				this.getMyTestPoints();
			} else {
				this.$emit("update:step", 1);
			}
		},
	},
};
</script>