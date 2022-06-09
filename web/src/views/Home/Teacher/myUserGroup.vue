<template>
      <userList :isAddToProblemSet='false'/>
</template>

<script>
import userList from "@/components/userList/userList";
export default {
name:"myUserGroup",
  components: {
    userList,
  },
}
</script>

<style>

</style>