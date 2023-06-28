<template>
  <NavBar />
  <router-view></router-view>
</template>

<script>
import NavBar from './components/NavBar.vue'
import "bootstrap/dist/css/bootstrap.min.css"
import "bootstrap/dist/js/bootstrap"
// import $ from 'jquery'  // 导入了jQuery库，并将其赋值给变量$，以便在代码中使用jQuery的功能。

export default {  // 将当前的对象作为默认导出，使其他文件可以导入并使用该对象。
  components: {  // 在 Vue 组件中声明其他组件的选项。在这个例子中，它将一个名为 NavBar 的组件添加到当前组件的依赖组件列表中。这样做的目的是告诉 Vue，当前组件将使用到 NavBar 组件，并使得 NavBar 组件在当前组件中可用。
    NavBar
  },
  setup() {
    // $.ajax({ // 使用jQuery的ajax方法发送异步请求的代码。在这里，它发送了一个POST请求到指定的URL，并提供了请求的参数。
    //   url: "http://127.0.0.1:3000/user/account/register/",
    //   type: "post",  // 这个请求包含密码，所以用post不用get
    //   data: {  // 请求的参数，其中包括username和password。
    //     username: 'mzy2',
    //     password: "123",
    //     confirmedPassword: "123",
    //   },
    //   success(resp) {  // 请求成功时的回调函数，当服务器成功返回响应时，该函数将被调用，并将响应作为参数传递给函数
    //     console.log(resp);
    //   },
    //   error(resp) {
    //     console.log(resp);
    //   }
    // })
  }
}
</script>

<style>
body {
  background-image: url("@/assets/images/background.png");
  background-size: cover;
}
</style>