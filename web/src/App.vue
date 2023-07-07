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
  }
}
</script>

<style>
body {
  background-image: url("@/assets/images/background.png");
  background-size: cover;
}
</style>