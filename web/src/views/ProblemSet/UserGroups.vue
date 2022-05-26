<template>
	<div>
		<el-card>
			<div>
				<ul style="list-style: none;" v-loading="loading">
					<li v-for="(item, index) in myUserGroups" :key="index" style="margin-bottom: 10px;">
						<el-card shadow="hover" style="text-align: left;">
							<div slot="header" style="height: 1rem; line-height: 1rem;">
								<b>{{item.name}}</b>
								<div style="float: right; transform: translateY(-25%)">
									<el-button v-if="openedUserGroups.indexOf(item.id) != -1" type="danger" size="mini" icon="el-icon-minus" @click="cancelOpen(item)">取消开放</el-button>
									<el-button v-else type="primary" size="mini" icon="el-icon-plus" @click="openToThis(item)">开放</el-button>
								</div>
							</div>
							<div>
								<span>用户组信息：<span :style="item.introduce?{}:{'color': '#888'}">{{item.introduce?item.introduce:'无'}}</span></span>
								<span style="float: right"><i class="el-icon-user-solid"></i>{{item.num}}</span>
								<span style="float: right; margin-right: 1rem;"><b>创建时间：</b>{{item.create_time}}</span>
							</div>
						</el-card>
					</li>
				</ul>
			</div>
		</el-card>
	</div>
</template>

<script>
export default {
	data() {
		return {
			myUserGroups: [],
			openedUserGroups: [],
			loading: false,
		};
	},
	methods: {
		async getOpenedUsergroupInfo() {
			let arr = [];
			let res = await this.$ajax.post(
				"/problemset/getUsergroupInfo",
				{
					id: this.$route.params.problemSetId,
				},
				{
					headers: {
						Authorization: `Bearer ${this.$store.state.token}`,
					},
				}
			);
			if (res.data.code == 0) {
				for (let i = 0; i < res.data.data.length; i++) {
					arr.push(res.data.data[i].id);
				}
			}
			this.openedUserGroups = arr;
		},
		async getMyUsergroupInfo() {
			let res = await this.$ajax.post(
				"/usergroup/getMyUsergroup",
				{
					id: this.$route.params.problemSetId,
				},
				{
					headers: {
						Authorization: `Bearer ${this.$store.state.token}`,
					},
				}
			);
			if (res.data.code == 0) {
				this.myUserGroups = res.data.data;
			}
		},
		async openToThis(item) {
			let res = await this.$ajax.post(
				"/problemset/authorizeToUsergroup",
				{
					id: this.$route.params.problemSetId,
					usergroupId: item.id,
				},
				{
					headers: {
						Authorization: `Bearer ${this.$store.state.token}`,
					},
				}
			);
			if (res.data.code) {
				this.$notify({
					title: "失败",
					message: res.data.message,
					type: "error",
				});
			} else {
				this.openedUserGroups.push(item.id);
				this.$notify({
					title: "成功",
					message: res.data.message,
					type: "success",
				});
			}
		},
		async cancelOpen(item) {
			let res = await this.$ajax.post(
				"/problemset/revokeToUsergroup",
				{
					id: this.$route.params.problemSetId,
					usergroupId: item.id,
				},
				{
					headers: {
						Authorization: `Bearer ${this.$store.state.token}`,
					},
				}
			);
			if (res.data.code) {
				this.$notify({
					title: "失败",
					message: res.data.message,
					type: "error",
				});
			} else {
				let index = this.openedUserGroups.indexOf(item.id);
				this.openedUserGroups.splice(index, 1);
				this.$notify({
					title: "成功",
					message: res.data.message,
					type: "success",
				});
			}
		},
	},
	created() {
		if (!this.$store.state.myInfo.roles.includes("ROLE_TEACHER")) {
			this.$router.replace(
				`/problem-set/${this.$store.state.problemSetInfo.problemSetId}/`
			);
		} else {
			this.getOpenedUsergroupInfo();
			this.getMyUsergroupInfo();
		}
	},
};
</script>