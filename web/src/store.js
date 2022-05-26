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
    },
    // //异步操作
    // actions: {

    //     setToken(context, token) {
    //         context.commit('setToken', token);
    //     },
    //     setNoToken(context, noToken) {
    //         context.commit('setNoToken', noToken);
    //     },
    //     setIsTeacher(context, isTeacher) {
    //         context.commit('setIsTeacher', isTeacher);
    //     },
    //     setIsAdmin(context, isAdmin) {
    //         context.commit('setIsAdmin', isAdmin);
    //     },
    //     setIsStudent(context, isStudent) {
    //         context.commit('setIsStudent', isStudent);
    //     },
    //     setIsCommon(context, isCommon) {
    //         context.commit('setIsCommon', isCommon);
    //     },
    //     setMyInfo(context, myInfo) {
    //         context.commit('setMyInfo', myInfo);
    //     },
    //     setProblemSetInfo(context, newInfo) {
    //         context.commit('setProblemSetInfo', newInfo);
    //     },
    // },
    getters: {
        usernameToShow: (state) => {
            return state.myInfo.nickname ? state.myInfo.nickname : (state.myInfo.realName ? state.myInfo.realName : state.myInfo.username)
        }
    },
})