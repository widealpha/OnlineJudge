<template>
  <div>
  
  
    <userList :isAddToProblemSet='true'/>
 
  </div>
</template>

<script>
import userList from "@/components/userList/userList";
export default {
  name: "MyUserGroup",
  components: {
    userList,
  },
  data() {
    return {
  
    };
  },
  methods: {
   
   
  },
 
};
</script>

<style></style>
