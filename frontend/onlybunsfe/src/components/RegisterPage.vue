<template>
    <div class="register-page">
        <h2>Register</h2>
        <input type="text" v-model="username" placeholder="Username">
        <input type="email" v-model="email" placeholder="Email">
        <input type="password" v-model="password" placeholder="Password">
        <input type="password" v-model="confirmPassword" placeholder="Confirm Password">
        <input type="text" v-model="address" placeholder="Address">
        <button @click="register">Register</button>

        <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
    </div>
</template>

<script>
import axios from 'axios';
export default {
    name: 'RegisterPage',
    data() {
        return {
            email: '',
            username: '',
            password: '',
            confirmPassword: '',
            address: '',
            errorMessage: '',
        };
    },
    methods: {
        async register() {
            // ✅ Validation
            if (!this.email || !this.username || !this.password || !this.confirmPassword || !this.address) {
                this.errorMessage = "All fields are required!";
                return;
            }
            if (this.password !== this.confirmPassword) {
                this.errorMessage = "Passwords do not match!";
                return;
            }

            try {
                await axios.post('http://localhost:8081/api/users/register', {
                    email: this.email,
                    username: this.username,
                    password: this.password,
                    address: this.address
                });
                this.$router.push('/login');
            } catch (error) {
                this.errorMessage = error.response?.data?.message || "Registration failed!";
            }
        }
    },
};
</script>

<style scoped>
.register-page {
    .register-page {
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
}

.error {
    color: red;
    margin-top: 10px;
}
}
</style>