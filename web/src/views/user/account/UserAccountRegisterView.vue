<template>
    <ContentField>
        <div class="row justify-content-md-center">
            <div class="col-3">
                <form @submit.prevent="register">
                    <!-- 在表单提交时，阻止默认的提交行为，并调用组件中的 register 方法来处理提交逻辑。在表单提交时，阻止默认的提交行为，并调用组件中的 login 方法来处理提交逻辑。 -->
                    <div class="mb-3">
                        <label for="username" class="form-label">用户名</label>
                        <input v-model="username" type="text" class="form-control" id="username" placeholder="请输入用户名">
                        <!-- v-model 表示将 <input> 元素的值与组件中的 username 数据进行双向绑定，即当用户在输入框中输入内容时，username 数据会自动更新；同时，当 username 数据的值发生变化时，输入框中的值也会更新为新的值。这样可以方便地在组件中获取和处理用户输入的数据。 -->
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">密码</label>
                        <input v-model="password" type="password" class="form-control" id="password" placeholder="请输入密码">
                    </div>
                    <div class="mb-3">
                        <label for="confirmedPassword" class="form-label">确认密码</label>
                        <input v-model="confirmedPassword" type="password" class="form-control" id="confirmedPassword" placeholder="请再次输入密码">
                    </div>
                    <div class="error-message">{{ error_message }}</div>
                    <button type="submit" class="btn btn-primary" >提交</button>
                </form>
            </div>
        </div>
    </ContentField>
</template>

<script>
import ContentField from '../../../components/ContentField.vue'
import { ref } from 'vue'
import router from '../../../router/index'
import $ from 'jquery'

export default {
    components: {
        ContentField
    },
    setup() {
        let username = ref('');
        let password = ref('');
        let confirmedPassword = ref('');
        let error_message = ref('');

        const register = () => {
            $.ajax({
                url: "http://127.0.0.1:3000/user/account/register/",
                type: "post",
                data: {
                    username: username.value,
                    password: password.value,
                    confirmedPassword: confirmedPassword.value,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        router.push({name: "user_account_login"});
                    } else {
                        error_message.value = resp.error_message;
                    }
                },
            });
        }

        return {
            username,
            password,
            confirmedPassword,
            error_message,
            register,
        }
    }
}

</script>

<style scoped>
button {
    widows: 100%;
}

div.error-message {
    color: red;
}
</style>