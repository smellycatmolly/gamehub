import { createStore } from 'vuex'
import ModuleUser from './user'
import ModulePk from './pk'

export default createStore({   // 通过 export default createStore({ ... })，你将这个 Vuex store 实例导出，以便在其他文件中使用。
  state: {
  },
  getters: {
  },
  mutations: {
  },
  actions: {
  },
  modules: {
    user: ModuleUser,
    pk: ModulePk,
  }
})

// 上述代码创建了一个基本的 Vuex store，其中包含了状态对象、getters 对象、mutations 对象、actions 对象和一个子模块。