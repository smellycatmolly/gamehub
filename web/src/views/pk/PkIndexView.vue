<template>
    <PlayGround v-if="$store.state.pk.status === 'playing'" />
    <MatchGround v-if="$store.state.pk.status === 'matching'" />
</template>

<script>
import PlayGround from '../../components/PlayGound.vue'
import MatchGround from '../../components/MatchGround.vue'
import { onMounted, onUnmounted } from 'vue';  // 当组件被挂载/卸载之后执行的函数
import { useStore } from 'vuex'

export default {
    components: {
        PlayGround,
        MatchGround,
    },
    setup() {
        const store = useStore();
        const socketUrl = `ws://127.0.0.1:3000/websocket/${store.state.user.token}/`;  // 用于构建每个用户独立的 WebSocket 连接。

        let socket = null;
        onMounted(() => {  // onMounted 钩子，在组件playground挂载到 DOM 后执行一些操作。
            store.commit("updateOpponent", {
                username: "我的对手",
                photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png"
            })
            
            socket = new WebSocket(socketUrl);  // 创建 WebSocket 实例，通过传入连接 URL socketUrl 来建立与 WebSocket 服务器的连接。

            socket.onopen = () => {  // 监听 WebSocket 连接的打开事件
                console.log("connected!");
                store.commit("updateSocket", socket);
            }

            socket.onmessage = msg => {  // 监听 WebSocket 接收到消息的事件，当收到消息时触发。msg 是这个函数的参数
                const data = JSON.parse(msg.data);  // 将消息数据解析为 JSON 对象。
                if (data.event === "start-matching") {  // 匹配成功
                    store.commit("updateOpponent", {
                        username: data.opponent_username,
                        photo: data.opponent_photo,
                    });
                    setTimeout(() => {
                        store.commit("updateStatus", "playing");
                    }, 2000);  // 2s后再展示地图画面，保证能看到对手的头像
                    store.commit("updateGamemap", data.gamemap);
                }
            }

            socket.onclose = () => {
                console.log("disconnected!");
            }
        });

        onUnmounted(() => {  // 在组件component(playground)销毁（从 DOM 中移除）之前执行一些清理操作
            socket.close();
            store.commit("updateStatus", "matching");
        })
    }
}
</script>

<style scoped>
</style>