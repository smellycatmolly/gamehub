import $ from 'jquery'  // 导入了jQuery库，并将其赋值给变量$，以便在代码中使用jQuery的功能:ajax

export default {
    state: {
        id: "",
        username: "",
        photo: "",
        token: "",
        is_login: false,
        pulling_info: true,  // 是否正在拉取信息
    },
    getters: {
    },
    mutations: {  // Mutations 用于修改 Vuex 的状态（state）。它们是同步函数，负责处理简单的状态变更。通常在 mutations 中执行的操作是直接修改 state 的值，以确保状态的变更是同步的。
        updateUser(state, user) {
            state.id = user.id;
            state.username = user.username;
            state.photo = user.photo;
            state.is_login = user.is_login;
        },
        updateToken(state, token) {
            state.token = token;
        },
        logout(state) {  // 退出登录不需要向后端发送请求，自己把token删除了就可以了
            state.id = "";
            state.username = "";
            state.photo = "";
            state.token = "";
            state.is_login = false;
        },
        updatePullingInfo(state, pulling_info) {
            state.pulling_info = pulling_info;
        },
        
    },
    actions: {  // Actions 用于处理异步操作、封装业务逻辑，并提交 mutations 来修改状态。它们可以包含任意异步操作，如发起网络请求、定时器、以及其他需要异步处理的任务。在 Actions 中可以执行一系列操作，包括异步请求数据、调用 API、提交多个 mutations 等。当异步操作完成后，可以通过提交 mutations 来更新 state 的值。通过将异步操作放在 Actions 中，可以更好地管理复杂的异步流程，并使代码结构更清晰。
        // 把login的ajax放在.js里没放在.vue里，但register的ajax放在了.vue里：因为login会修改state的值，要放user.js里。register不修改state值，放vue里就行，不用套娃
        login(context, data) {  // 在 login action 中，context 对象包含了以下属性和方法：context.state：访问当前模块的状态（state）对象。context.getters：访问当前模块的 getters 对象，用于获取派生状态。context.commit：提交（commit）一个 mutation，用于修改状态。context.dispatch：触发（dispatch）一个 action，用于触发其他的 action。
            // 发送一个POST请求到指定的URL，并提供用户名和密码作为请求参数
            $.ajax({ // 使用jQuery的ajax方法发送异步请求的代码。在这里，它发送了一个POST请求到指定的URL，并提供了请求的参数。
                url: "https://app5678.acapp.acwing.com.cn/api/user/account/token/",
                type: "post",
                data: {  // 请求的参数，其中包括username和password。
                username: data.username,
                password: data.password,
                },
                success(resp) {  // 请求成功时的回调函数，当服务器成功返回响应时，该函数将被调用，并将响应作为参数传递给函数
                    if (resp.error_message === "success") {
                        localStorage.setItem("jwt_token", resp.token);  // localStorage也是字典。浏览器提供的一种本地存储机制，可以在客户端持久地存储数据。使用键值对的方式来存储数据。每个键和对应的值都是字符串类型。
                        context.commit("updateToken", resp.token);  // 格式：调用mutations里的函数时要加commit
                        data.success(resp);  // 这里的data.success()和data.error()是UserAccountLoginView.vue中在store.dispatch("login", {})中定义的
                    } else {
                        data.error(resp);  // 异步操作是指在程序中执行的操作不按照线性顺序进行，而是通过回调函数或其他方式来处理操作的完成或结果，从而提高程序的并发性和响应性。
                    }
                },
                error(resp) {  // 使用 Vuex 进行状态管理时，可以通过 store.dispatch 方法来触发一个 action。login 方法是一个 action，通过 store.dispatch 来调用。当调用 store.dispatch('login', data) 时，data.success()和data.error()被定义，其中 data 是传递给 login action 的参数。在 login action 内部，通过 data.success(resp) 和 data.error(resp) 来执行不同的回调函数。
                    data.error(resp);
                }
            });
        },
        getinfo(context, data) {
            $.ajax({ // 使用jQuery的ajax方法发送异步请求的代码。在这里，它发送了一个GET请求到指定的URL，并提供了请求的参数。
                url: "https://app5678.acapp.acwing.com.cn/api/user/account/info/",
                type: "get",
                headers: {
                  Authorization: "Bearer " + context.state.token,
                },
                success(resp) {  // 请求成功时的回调函数，当服务器成功返回响应时，该函数将被调用，并将响应作为参数传递给函数
                    if (resp.error_message === "success"){
                        context.commit("updateUser", {
                            ...resp,  // 表示将 resp 对象展开，将对象的所有属性和值作为参数传递给 context.commit 方法的第二个参数。
                            is_login: true,
                        });
                        data.success(resp);
                    } else {
                        data.error(resp);
                    }
                },
                error(resp) {
                    data.error(resp);
                }
            })
        },
        logout(context) {
            localStorage.removeItem("jwt_token");
            context.commit("logout");
        }
    },
    modules: {
    }
}
