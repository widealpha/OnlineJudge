<template>
  <div>
    <userList :isAddToProblemSet="false" />
  </div>
</template>

<script>
import userList from "@/components/userList/userList";
export default {
  name: "UserList",
  components: {
    userList,
  },
};
</script>

<style></style>
