<template>
	<el-card style="text-align: left" shadow="hover">
		<div slot="header">
			<span style="height: 100%">
				<i class="el-icon-s-management" style="color: dodgerblue; font-size: 150%"></i>
			</span>
			<el-link type="primary" @click="toCertainProblemSet">{{title}}</el-link>
			<el-tag style="float: right" effect="dark" :type="getStatus() == '进行中' ? '' : 'info'">
				{{getStatus()}}
			</el-tag>
			<!-- <el-tag style="float: right; margin-inline: 10px" effect="dark">
				{{open?'开启':'关闭'}}
			</el-tag> -->
			<el-tag v-if="status!=null" style="float: right; margin-inline: 10px" effect="dark" :type="status==1?'success':status==2?'':'warning'">
				{{getType()}}
			</el-tag>
		</div>
		<div>
			<span>结束时间:{{endTime}}</span>
			<span style="float: right"><i class="el-icon-user-solid"></i>题目集作者:{{author}}</span>
		</div>
	</el-card>
</template>

<script>
export default {
	name: "problemSet",
	props: {
		author: {
			type: String,
			default: "题目集作者",
		},
		title: {
			type: String,
			default: "题目集标题",
		},
		beginTime: {
			type: String,
			default: "1970-01-01 00:00",
		},
		endTime: {
			type: String,
			default: "1970-01-01 00:00",
		},
		problemSetId: {
			type: Number,
			default: 0,
		},
		status: Number,
		open: Number,
	},
	methods: {
		getStatus() {
			let beginTime = new Date(this.beginTime);
			let endTime = new Date(this.endTime);
			let nowTime = new Date();
			if (nowTime >= endTime) {
				return "已关闭";
			} else if (nowTime <= beginTime) {
				return "未开始";
			} else {
				return "进行中";
			}
		},
		getType() {
			switch (this.status) {
				case 1:
					return "完全公开";
				case 2:
					return "教师间公开";
				case 3:
					return "私有";
			}
		},
		toCertainProblemSet() {
			this.$router.push({
				path: `/problem-set/${this.problemSetId}`,
			});
		},
	},
};
</script>