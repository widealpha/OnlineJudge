<template>
	<!--<div class="notFound" :style="{height:divHeight;line}">-->
	<img src="../assets/404.svg" alt="" width="500" class="notFound">
	<!--</div>-->
</template>

<script>
export default {
	name: "notFound",
};
</script>

<style lang="less" scoped>
.notFound {
	position: absolute;
	display: block;
	top: 50%;
	left: 50%;
	transform: translateX(-50%) translateY(-50%);
}
</style>
