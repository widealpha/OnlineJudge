<template>
	<div>
		<ul style="list-style: none;" class="ul">
			<li v-for="(item, index) in problemSets" :key="index">
				<problem-set :title="item.name" :problemSetId="item.id" :status="item.status" :open="item.open" :author="item.author.toString()" :beginTime="item.beginTime" :endTime="item.endTime"></problem-set>
			</li>
		</ul>
	</div>
</template>

<script>
import problemSet from "./problemSet.vue";
export default {
	components: {
		problemSet,
	},
	data() {
		return {
			loading: false,
			problemSets: [],
		};
	},
	methods: {
		async getMyProblemSets() {
			let res = await this.$ajax.post(
				"/problemSet/getSelfDoneProblemSet",
				{},
				{
					headers: {
						Authorization: `Bearer ${this.$store.state.token}`,
					},
				}
			);
			if (res.data.code == 0) {
				this.problemSets = res.data.data;
			}
		},
	},
};
</script>

<style scoped>
.ul li {
	margin-block: 20px;
}
</style>