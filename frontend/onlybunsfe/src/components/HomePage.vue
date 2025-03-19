<template>
    <div>
        <button @click="logout">Logout</button>
    </div>
</template>

<script>
import axios from 'axios';
export default {
    name: 'HomePage',
    methods: {
        async logout() {
            // Handle logout logic here
            try {
                await axios.post('http://localhost:8081/api/auth/logout', {}, {
                    withCredentials: true
                });
                alert('Logged out successfully!');
                this.$router.push('/login');
            } catch (error) {
                console.error('Logout failed:', error);
            }
                }
            }
}
</script>

<style scoped>
/* Add your styles here */
</style>