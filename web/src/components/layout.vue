<template>
  <div class="layout">
    <router-view/>
  </div>
</template>

<script>
export default {
  name: "layout",
};
</script>

<style scoped>
.layout {
  left: 0;
  right: 0;
  height: calc(100vh - 3.5em - 40px);

}
</style>
