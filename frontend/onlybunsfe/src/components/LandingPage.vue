<template>
    <div class="landing-container">
        <h1>Unlogged User</h1>
        <div class="button-group">
            <button @click="navigateTo('/login')">Login</button>
            <button @click="navigateTo('/register')">Register</button>
        </div>
    </div>
</template>

<script>
export default{
    methods: {
        navigateTo(route){
            this.$router.push(route);
        }
    }
}
</script>
<style scoped>
.landing-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  text-align: center;
}

.button-group {
  margin-top: 20px;
}

button {
  margin: 10px;
  padding: 10px 20px;
  font-size: 18px;
  cursor: pointer;
}
</style>