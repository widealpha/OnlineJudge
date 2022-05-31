import Vue from 'vue';
import Vuex from 'vuex';

Vue.use(Vuex)

export default new Vuex.Store({
    state: {
        token: null,
        noToken: true,

        myInfo: {
            avatar: null,
            nickname: null,
            name: null,
            email: null,
            userId: undefined,
            studentId: null,
        },
        problemSetInfo: {
            problems: [],
            // 是否公开 0:私有 1:公开
            open: 0,
            // 题目
            title: "",
            // 介绍
            introduction: "",
            creatorId: 0,
            beginTime: "2000-01-01 00:00:00",
            endTime: "2000-01-01 00:00:00",
            // 1 练习，2 作业，3 竞赛
            type: 0,
            isMyProblemSet: false,
            typeName: "",
            problemSetId: 0,
        }
    },
    //同步操作
    mutations: {
        setToken(state, token) {
            state.token = token
        },
        setNoToken(state, noToken) {
            state.noToken = noToken;
        },

        setMyInfo(state, myInfo) {
            for (const key in myInfo) {
                Vue.set(state.myInfo, key, myInfo[key])
            }
        },
        setProblemSetInfo(state, newInfo) {
            for (const key in newInfo) {
                Vue.set(state.problemSetInfo, key, newInfo[key])
            }
        },
        getType(state, type) {
            switch (type) {
                case 1:
                    state.problemSetInfo.typeName = "练习";
                    return "练习";
                case 2:
                    state.problemSetInfo.typeName = "测验";
                    return "测验";
                case 3:
                    state.problemSetInfo.typeName = "竞赛";
                    return "竞赛";
            }
        },
    },

    getters: {
        usernameToShow: (state) => {
            return state.myInfo.nickname ? state.myInfo.nickname : (state.myInfo.realName ? state.myInfo.realName : state.myInfo.username)
        }
    },
})