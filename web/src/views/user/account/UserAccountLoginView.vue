<template>
    <ContentField v-if="!$store.state.user.pulling_info">
         <!--加入pulling_info的判断：登录之前展示，登录成功后不展示。因为是state中的全局变量，NavBar中也能用  -->
        <div class="row justify-content-md-center">
            <div class="col-3">
                <form @submit.prevent="login">
                    <!-- 在表单提交时，阻止默认的提交行为，并调用组件中的 login 方法来处理提交逻辑。在表单提交时，阻止默认的提交行为，并调用组件中的 login 方法来处理提交逻辑。 -->
                    <div class="mb-3">
                        <label for="username" class="form-label">用户名</label>
                        <input v-model="username" type="text" class="form-control" id="username" placeholder="请输入用户名">
                        <!-- v-model 表示将 <input> 元素的值与组件中的 username 数据进行双向绑定，即当用户在输入框中输入内容时，username 数据会自动更新；同时，当 username 数据的值发生变化时，输入框中的值也会更新为新的值。这样可以方便地在组件中获取和处理用户输入的数据。 -->
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">密码</label>
                        <input v-model="password" type="password" class="form-control" id="password" placeholder="请输入密码">
                    </div>
                    <div class="error_message">{{ error_message }}</div>
                    <button type="submit" class="btn btn-primary" >提交</button>
                </form>
            </div>
        </div>
    </ContentField>
</template>

<script>
import ContentField from '../../../components/ContentField.vue'
import { useStore } from 'vuex';  // useStore 是从 vuex 库中导入的一个函数，用于在 Vue 组件中获取 Vuex 的 store 实例。它可以在组件中使用，通过调用 useStore() 函数来获取全局的 Vuex store 对象，然后可以使用该对象进行状态管理和触发相应的 actions。
import { ref } from 'vue';  // Ref 类型用于包装普通数据，使其成为响应式的数据。通过使用 Ref 类型，可以在组件中创建响应式的数据，并对其进行监听和更新。
import router from '../../../router/index'


export default {
    components: {
        ContentField
    },
    setup() {
        const store = useStore();  // 它会获取到 index.js 中导出的 Vuex store 实例，使你可以在组件中访问和修改其中定义的状态、调用 actions、提交 mutations 等。
        let username = ref('');  // 使用 ref 函数创建一个响应式数据 username，并将初始值设为空字符串。
        let password = ref('');
        let error_message = ref('');

        const jwt_token = localStorage.getItem("jwt_token");  // 如果不加这个localStorage每次刷新的时候state里的token就会消失，就会跳转到登录页面
        if (jwt_token) {  // 判断如果localStorage的token有的话，就请求getinfo补充state，然后回到home页面
            store.commit("updateToken", jwt_token);
            store.dispatch("getinfo", {
                success() {
                    router.push({ name: "home" });
                    store.commit("updatePullingInfo", false);
                },
                error() {
                    store.commit("updatePullingInfo", false);
                }
            })
        } else {
            store.commit("updatePullingInfo", false);  // 拉取结束
        }

        const login = () => {  // const login = () => { } 定义了一个名为 login 的函数，用于处理登录逻辑。
            error_message.value = "";  // 每次清空
            store.dispatch("login", {  // 要调用actions里的函数必须要用store.dispatch
                username: username.value, // ref取值要用.value
                password: password.value,
                success() {  // success()和error()函数是传递给user.js里的login()函数的，因为从43到57行都是data
                    store.dispatch("getinfo", {
                        success() {
                            router.push({ name: 'home' });
                          // store.state.user 将返回 user 模块的状态对象，该对象包含在 index.js 中 ModuleUser 模块中定义的 state 属性。
                        }
                    })
                },
                error() {
                    error_message.value = "用户名或密码错误";
                }
            })
        }

        return {
            username,
            password,
            error_message,
            login,
        }
    }
}

</script>

<style scoped>
button {
    width: 100%;
}
div.error_message {
    color: red;
}
</style>