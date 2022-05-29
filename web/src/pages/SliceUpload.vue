<template>
	<div>
		<el-upload action="" :on-change="onChange" :auto-upload="false" :show-file-list="false" :file-list="list">
			<el-button size="small" type="primary">点击上传</el-button>
			<div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过500kb</div>
		</el-upload>
	</div>
</template>

<script>
import SparkMD5 from "spark-md5";
export default {
	data() {
		return {
			list: [],
			sliceFiles: [],
		};
	},
	methods: {
		onChange(file) {
			 (file);
			let spark = new SparkMD5();
			spark;
			let fileReader = new FileReader();
			fileReader.onload = (e) => {
				 (e);
				spark.appendBinary(e.target.result);
				let md5 = spark.end();
				 (md5);
			};
			fileReader.readAsBinaryString(file.raw);
		},
	},
};
</script>