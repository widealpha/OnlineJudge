<template>
	<div class="home">
		<ojheader />
		<el-container>
			<ojaside :pageIndex="$route.path" />
			<el-main>
				<layout />
			</el-main>
		</el-container>
	</div>
</template>

<script>
import Layout from "@/components/layout.vue";
import Ojaside from "@/components/ojaside.vue";
import Ojheader from "@/components/ojheader.vue";
export default {
	components: {
		Ojheader,
		Ojaside,
		Layout,
	},
};
</script>

<style scoped>
</style>