<template>
	<div>
		<el-card>
			<div slot="header" style="text-align: left; min-height: 20px">
				<el-button style="margin-left: 10px;" @click="$emit('update:step', 2);" plain>上一步</el-button>
				<el-button :disabled="!allPassed" style="float: right;" type="success">提交限制</el-button>
			</div>
			<el-table :data="limits">
				<el-table-column type="expand" width="50">
					<template slot-scope="props">
						<el-card>
							<div slot="header">测试代码</div>
							<codemirror v-model="props.row.testCode" :options="Object.assign({},cmOption,{readOnly:true, mode: getCmMode(props.row.compiler[1])})" class="code-mirror" />
						</el-card>
					</template>
				</el-table-column>
				<el-table-column label="所选编译器">
					<template slot-scope="props">
						<span>{{props.row.compiler[1]}}</span>
					</template>
				</el-table-column>
				<el-table-column label="操作">
					<template slot-scope="props">
						<el-button type="primary" @click="updateIndex=props.$index; isUpdating=true;">更新</el-button>
						<el-button type="danger" @click="deleteMe(props.$index)">删除</el-button>
						<el-button type="success" @click="testMe(props.$index)" v-if="props.row.result===undefined">测试</el-button>
					</template>
				</el-table-column>
				<el-table-column label="测试结果">
					<template slot-scope="props">
						<el-tag v-if="props.row.result===undefined" effect="dark" type="info">待测试</el-tag>
						<el-tag v-else-if="props.row.result" effect="dark" type="success">已通过</el-tag>
						<el-tag v-else effect="dark" type="danger">未通过</el-tag>
					</template>
				</el-table-column>
				<div slot="append">
					<el-button plain style="width: 100%; margin-bottom: 1px" @click="isAdding=true;"><i class="el-icon-plus"></i></el-button>
				</div>
			</el-table>
		</el-card>

		<el-dialog title="增加限制" :visible.sync="isAdding" @open="$refs.addForm?$refs.addForm.resetFields():''" :append-to-body="true">
			<el-form label-position="top" :model="addForm" ref="addForm" :rules="addRules">
				<el-form-item prop="compiler" label="使用语言">
					<el-cascader v-model="addForm.compiler" :options="languages" placeholder="请选择语言" />
				</el-form-item>
				<el-form-item prop="codeMax" label="最大代码长度">
					<el-input v-model="addForm.codeMax">
						<template slot="append">kb</template>
					</el-input>
				</el-form-item>
				<el-form-item prop="timeMax" label="最大运行时间">
					<el-input v-model="addForm.timeMax">
						<template slot="append">ms</template>
					</el-input>
				</el-form-item>
				<el-form-item prop="memoryMax" label="最大占用内存">
					<el-input v-model="addForm.memoryMax">
						<template slot="append">MB</template>
					</el-input>
				</el-form-item>
				<el-form-item prop="testCode" label="测试代码">
					<div class="code">
						<codemirror v-model="addForm.testCode" :options="cmOption" class="code-mirror" />
					</div>
				</el-form-item>
			</el-form>
			<div slot="footer" class="dialog-footer">
				<el-button type="primary" @click="addLimit">确 定</el-button>
			</div>
		</el-dialog>

		<el-dialog title="更新限制" :visible.sync="isUpdating" @open="copyLimits" :append-to-body="true">
			<el-form label-position="top" :model="updateForm" ref="updateForm" :rules="updateRules">
				<el-form-item prop="compiler" label="使用语言">
					<el-cascader v-model="updateForm.compiler" :options="languages" placeholder="请选择语言" />
				</el-form-item>
				<el-form-item prop="codeMax" label="最大代码长度">
					<el-input v-model="updateForm.codeMax">
						<template slot="append">kb</template>
					</el-input>
				</el-form-item>
				<el-form-item prop="timeMax" label="最大运行时间">
					<el-input v-model="updateForm.timeMax">
						<template slot="append">ms</template>
					</el-input>
				</el-form-item>
				<el-form-item prop="memoryMax" label="最大占用内存">
					<el-input v-model="updateForm.memoryMax">
						<template slot="append">MB</template>
					</el-input>
				</el-form-item>
				<el-form-item prop="testCode" label="测试代码">
					<div class="code">
						<codemirror v-model="updateForm.testCode" :options="cmOption" class="code-mirror" />
					</div>
				</el-form-item>
			</el-form>
			<div slot="footer" class="dialog-footer">
				<el-button type="primary" @click="updateMe">确 定</el-button>
			</div>
		</el-dialog>
	</div>
</template>

<script>
import preCode from "@/assets/json/preCode.json";

export default {
	props: {
		step: Number,
	},
	components: {
		codemirror,
	},
	data() {
		return {
			limits: [],
			isAdding: false,
			addForm: {
				compiler: "",
				testCode: "",
				codeMax: "16",
				timeMax: "400",
				memoryMax: "64",
			},
			addRules: {
				compiler: [
					{
						required: true,
						message: "请选择编译器",
						trigger: "blur",
					},
				],
				testCode: [
					{
						required: true,
						message: "请输入测试代码",
						trigger: "blur",
					},
				],
				codeMax: [
					{
						required: true,
						message: "该项不能为空",
						trigger: "blur",
					},
					{
						pattern: /^[0-9]+$/,
						message: "仅允许输入数字",
						trigger: ["blur", "change"],
					},
				],
				memoryMax: [
					{
						required: true,
						message: "该项不能为空",
						trigger: "blur",
					},
					{
						pattern: /^[0-9]+$/,
						message: "仅允许输入数字",
						trigger: ["blur", "change"],
					},
				],
				timeMax: [
					{
						required: true,
						message: "该项不能为空",
						trigger: "blur",
					},
					{
						pattern: /^[0-9]+$/,
						message: "仅允许输入数字",
						trigger: ["blur", "change"],
					},
				],
			},
			isUpdating: false,
			updateIndex: -1,
			updateForm: {
				compiler: "",
				testCode: "",
				codeMax: "16",
				timeMax: "400",
				memoryMax: "64",
			},
			updateRules: {
				compiler: [
					{
						required: true,
						message: "请选择编译器",
						trigger: "blur",
					},
				],
				testCode: [
					{
						required: true,
						message: "请输入测试代码",
						trigger: "blur",
					},
				],
				codeMax: [
					{
						required: true,
						message: "该项不能为空",
						trigger: "blur",
					},
					{
						pattern: /^[0-9]+$/,
						message: "仅允许输入数字",
						trigger: ["blur", "change"],
					},
				],
				memoryMax: [
					{
						required: true,
						message: "该项不能为空",
						trigger: "blur",
					},
					{
						pattern: /^[0-9]+$/,
						message: "仅允许输入数字",
						trigger: ["blur", "change"],
					},
				],
				timeMax: [
					{
						required: true,
						message: "该项不能为空",
						trigger: "blur",
					},
					{
						pattern: /^[0-9]+$/,
						message: "仅允许输入数字",
						trigger: ["blur", "change"],
					},
				],
			},
			languages: [],
			cmOption: {
				tabSize: 4,
				styleActiveLine: true,
				lineNumbers: true,
				styleSelectedText: true,
				line: true,
				foldGutter: true,
				indentUnit: 4, // 缩进单位，值为空格数，默认为2
				indentWithTabs: true, // 在缩进时，是否需要把 n*tab宽度个空格替换成n个tab字符
				gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter"],
				highlightSelectionMatches: {
					showToken: /\w/,
					annotateScrollbar: true,
				},
				mode: undefined,
				// hint.js options
				hintOptions: {
					// 当匹配只有一项的时候是否自动补全
					completeSingle: false,
				},
				//快捷键 可提供三种模式 sublime、emacs、vim
				keyMap: "sublime",
				matchBrackets: true,
				showCursorWhenSelecting: true,
				theme: "idea",
				extraKeys: { Ctrl: "autocomplete" },
			},
		};
	},
	methods: {
		// 获取可用语言
		async getSupportLanguage() {
			let res = await this.$ajax.get(
				"/solve/supportLanguages",
				{},
				{
					headers: {
						Authorization: `Bearer ${this.$store.state.token}`,
					},
				}
			);
			if (res.data.code === 0) {
				// for (const key in res.data.data) {
				// 	let children = [];
				// 	res.data.data[key].forEach((element) => {
				// 		children.push({
				// 			value: element,
				// 			label: element,
				// 		});
				// 	});
				// 	this.languages.push({
				// 		value: key,
				// 		label: key,
				// 		children: children,
				// 	});
				// }
			}
		},
		async getMyLimits() {
			let res = await this.$ajax.post(
				"/problemLimit/getProblemLimit",
				{
					id: this.problemId,
				},
				{
					headers: {
						Authorization: `Bearer ${this.$store.state.token}`,
					},
				}
			);
			/**
			 * TODO: 这个地方等后端限制接口正常后再修复
			 */
			if (res.data.code == 0) {
				// this.limits = res.data.data;
				 (res);
			} else {
				this.$message({
					message: res.data.message,
					type: "error",
					showClose: false,
					duration: 1000,
				});
			}
		},
		addLimit() {
			this.$refs.addForm.validate(async (valid) => {
				if (valid) {
					 (this.addForm);
					this.limits.push(JSON.parse(JSON.stringify(this.addForm)));
					this.$message({
						message: "添加成功",
						type: "success",
						showClose: false,
						duration: 1000,
					});
					this.isAdding = false;
				}
			});
		},
		copyLimits() {
			this.updateForm = JSON.parse(JSON.stringify(this.limits[0]));
		},
		updateMe() {
			this.$refs.updateForm.validate(async (valid) => {
				if (valid) {
					let updateForm = JSON.parse(
						JSON.stringify(this.updateForm)
					);
					updateForm.result = undefined;
					this.$set(this.limits, this.updateIndex, updateForm);
					this.$message({
						message: "修改成功",
						type: "success",
						showClose: false,
						duration: 1000,
					});
					this.isUpdating = false;
				}
			});
		},
		async deleteMe(index) {
			this.limits.splice(index, 1);
			this.$message({
				message: "删除成功",
				type: "success",
				showClose: false,
				duration: 1000,
			});
		},
		async testMe(index) {
			let limit = JSON.parse(JSON.stringify(this.limits[index]));
			limit.result = false;
			this.$set(this.limits, index, limit);
		},
		getCode(compiler, code) {
			let language;
			if (compiler.indexOf("C")) {
				if (compiler.indexOf("CPP") === 0) {
					language = "c++";
					// this.cmOption.mode = "text/x-c++src";
				} else {
					language = "c";
					// this.cmOption.mode = "text/x-c";
				}
			} else {
				if (compiler.indexOf("JAVA") === 0) {
					language = "java";
					// this.cmOption.mode = "text/x-java";
				} else {
					language = "python";
					// this.cmOption.mode = "text/x-python";
				}
			}
			return `\`\`\`${language}\n${code}\n\`\`\``;
		},
		getCmMode(language) {
			language = language.toUpperCase();
			if (language.indexOf("C") === 0) {
				if (language.indexOf("CPP") === 0) {
					return "text/x-c++src";
				} else {
					return "text/x-c";
				}
			} else {
				if (language.indexOf("JAVA") === 0) {
					return "text/x-java";
				} else if (language.indexOf("PYTHON") === 0) {
					return "text/x-python";
				}
			}
		},
	},
	created() {
		this.getSupportLanguage();
		let problemId = this.$route.query.problemId;
		if (problemId) {
			this.problemId = Number(problemId);
			this.getMyLimits();
		}
	},
	watch: {
		step(newVal) {
			if (newVal === 3) {
				for (const index in this.limits) {
					let element = this.limits[index];
					element.result = undefined;
				}
			}
		},
		$route(newVal) {
			let problemId = newVal.query.problemId;
			if (problemId) {
				this.problemId = Number(problemId);
				this.getMyLimits();
			} else {
				this.$emit("update:step", 1);
			}
		},
		"addForm.compiler"(newVal) {
			let language = newVal[0];
			switch (language) {
				case "C":
					this.cmOption.mode = "text/x-c";
					this.addForm.testCode = preCode.cCode;
					break;
				case "CPP":
					this.cmOption.mode = "text/x-c++src";
					this.addForm.testCode = preCode.cppCode;
					break;
				case "PYTHON":
					this.cmOption.mode = "text/x-python";
					this.addForm.testCode = preCode.python3Code;
					break;
				case "JAVA":
					this.cmOption.mode = "text/x-java";
					this.addForm.testCode = preCode.javaCode;
			}
		},
	},
	computed: {
		allPassed() {
			let result = this.limits.length !== 0;
			for (const index in this.limits) {
				let element = this.limits[index];
				result = result && element.result;
			}
			return result;
		},
	},
};

import { codemirror } from "vue-codemirror";
import "codemirror/lib/codemirror.css";
// language
import "codemirror/mode/clike/clike.js";
import "codemirror/mode/python/python.js";
// theme css
import "codemirror/theme/idea.css";
// require active-line.js
import "codemirror/addon/selection/active-line.js";
// styleSelectedText
import "codemirror/addon/selection/mark-selection.js";
import "codemirror/addon/search/searchcursor.js";
// hint
import "codemirror/addon/hint/show-hint.js";
import "codemirror/addon/hint/show-hint.css";
import "codemirror/addon/hint/javascript-hint.js";
import "codemirror/addon/selection/active-line.js";
// highlightSelectionMatches
import "codemirror/addon/scroll/annotatescrollbar.js";
import "codemirror/addon/search/matchesonscrollbar.js";
import "codemirror/addon/search/searchcursor.js";
import "codemirror/addon/search/match-highlighter.js";
// keyMap
import "codemirror/mode/clike/clike.js";
import "codemirror/addon/edit/matchbrackets.js";
import "codemirror/addon/comment/comment.js";
import "codemirror/addon/dialog/dialog.js";
import "codemirror/addon/dialog/dialog.css";
import "codemirror/addon/search/searchcursor.js";
import "codemirror/addon/search/search.js";
import "codemirror/keymap/sublime.js";
// foldGutter
import "codemirror/addon/fold/foldgutter.css";
import "codemirror/addon/fold/brace-fold.js";
import "codemirror/addon/fold/comment-fold.js";
import "codemirror/addon/fold/foldcode.js";
import "codemirror/addon/fold/foldgutter.js";
import "codemirror/addon/fold/indent-fold.js";
import "codemirror/addon/fold/markdown-fold.js";
import "codemirror/addon/fold/xml-fold.js";
</script>

<style scoped>
.code >>> .code-mirror {
	line-height: 2em;
}
</style>