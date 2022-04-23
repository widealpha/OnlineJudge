<template>
	<div>
		<el-header style="text-align: right; background: #fff; height: 3.5em; line-height: 3.5em; padding-left: 0px">
			<div v-if="noToken">
				<el-button plain @click="Register">注册</el-button>
				<el-button @click="Login" type="primary">登录</el-button>
			</div>
			<div v-else>
				<el-dropdown class="drop" :show-timeout="0">
					<div style="margin-right: 30px" @mouseenter="nameColor='dodgerblue';" @mouseleave="nameColor='#000';">
						<el-avatar style="vertical-align: middle" :src="avatar"><i style="font-size: 2em; vertical-align: middle;" class="el-icon-loading"></i></el-avatar>
						<el-link :underline="false" :style="{'font-size': '2em', 'font-family': 'KaiTi', 'margin-left': '0.25em', 'color': nameColor}">{{ username }}</el-link>
					</div>
					<el-dropdown-menu>
						<el-dropdown-item @click.native="myInfo"><i class="el-icon-user"></i>个人信息</el-dropdown-item>
						<el-dropdown-item @click.native="Logout"><i class="el-icon-switch-button"></i>退出登录</el-dropdown-item>
					</el-dropdown-menu>
				</el-dropdown>
			</div>
		</el-header>
	</div>
</template>

<script>
export default {
	name: "ojheader",
	data() {
		return {
			avatar: require("@/assets/icon/student.svg"),
			nameColor: "#000",
		};
	},
	methods: {
		Logout() {
			Promise.all([
				this.$store.dispatch("setToken", null),
				this.$store.dispatch("setIsAdmin", false),
				this.$store.dispatch("setMyInfo", {
					headImage: null,
					nickname: null,
					realName: null,
					stuNum: null,
					userId: undefined,
					username: null,
				}),
			]).then(() => {
				localStorage.removeItem("token");
				this.$router.replace({ path: "/login" });
			});
		},
		myInfo() {
			this.$router.push({ path: "/info" });
		},
		Login() {
			this.$router.push({ path: "/login" });
		},
		Register() {
			this.$router.push({ path: "/register" });
		},
	},
	computed: {
		noToken() {
			return this.$store.state.token === null;
		},
		username() {
			return this.$store.getters.usernameToShow;
		},
		isTeacher() {
			return this.$store.state.isTeacher;
		},
		isAdmin() {
			return this.$store.state.isAdmin;
		},
	},
};
</script>

<style>
.drop:hover {
	cursor: pointer;
}
</style>
