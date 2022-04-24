import Vue from 'vue';
import Vuex from 'vuex';

Vue.use(Vuex)

export default new Vuex.Store({
    state: {
        token: null,
        noToken: true,
        isTeacher: false,
        isAdmin: false,
        myInfo: {
            headImage: null,
            nickname: null,
            realName: null,
            stuNum: null,
            userId: undefined,
            username: null,
        },
        problemSetInfo: {
            problems: [],
            title: "",
            announcement: "",
            author: "",
            open: undefined,
            beginTime: "1970-01-01",
            endTime: "1970-01-01",
            status: 0,
            canUseOnlineJudge: true,
            canViewTestPoint: true,
            isMyProblemSet: false,
            problemIds: [],
            problemSetId: 0,
        }
    },
    mutations: {
        setToken(state, token) {
            state.token = token
        },
        setNoToken(state, noToken) {
            state.noToken = noToken;
        },
        setIsTeacher(state, isTeacher) {
            state.isTeacher = isTeacher;
        },
        setIsAdmin(state, isAdmin) {
            state.isAdmin = isAdmin;
            state.isTeacher = isAdmin;
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
    },
    actions: {
        setToken(context, token) {
            context.commit('setToken', token);
        },
        setNoToken(context, noToken) {
            context.commit('setNoToken', noToken);
        },
        setIsTeacher(context, isTeacher) {
            context.commit('setIsTeacher', isTeacher);
        },
        setIsAdmin(context, isAdmin) {
            context.commit('setIsAdmin', isAdmin);
        },
        setMyInfo(context, myInfo) {
            context.commit('setMyInfo', myInfo);
        },
        setProblemSetInfo(context, newInfo) {
            context.commit('setProblemSetInfo', newInfo);
        },
    },
    getters: {
        usernameToShow: (state) => {
            return state.myInfo.nickname ? state.myInfo.nickname : (state.myInfo.realName ? state.myInfo.realName : state.myInfo.username)
        }
    },
})