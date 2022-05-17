import Vue from 'vue'
import VueRouter from 'vue-router'

// 捕获VueRouter路由重复触发导致的报错
const originalPush = VueRouter.prototype.push;
VueRouter.prototype.push = function push(location, onResolve, onReject) {
  if (onResolve || onReject) {
    return originalPush.call(this, location, onResolve, onReject);
  }
  return originalPush.call(this, location).catch(err => err);
};

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    component: resolve => require(['./pages/Home'], resolve),
    children: [
      {
        path: '',
        component: resolve => require(['./views/Home/ProblemSets'], resolve),
        meta: {
          title: '首页'
        }
      },
      {
        path: '/student',
        redirect: "/student/problems",
        component: resolve => require(['./components/layout'], resolve),
        children: [
          {
            path: 'problems',
            component: resolve => require(['./views/Home/Student/Problems'], resolve),
            meta: {
              title: '题目列表'
            }
          },
          {
            path: 'userList',
            name: 'userList',
            component: resolve => require(['./components/userList/userList'], resolve),
            meta: {
              title: '用户组列表'
            },
          },
          {
            path: 'userListDetails',
            name: 'userListDetails',
            component: resolve => require(['./components/userList/userListDetails'], resolve),
            meta: {
              title: '用户组详情'
            }
          },
        ]
      },
      {
        path: '/teacher',
        redirect: '/teacher/myProblems',
        component: resolve => require(['./components/layout'], resolve),
        children: [
          {
            path: 'myProblems',
            component: resolve => require(['./views/Home/Teacher/MyProblems'], resolve),
            meta: {
              title: '我的题库'
            }
          },
          {
            name: 'newProblem',
            path: 'newProblem',
            component: resolve => require(['./views/Home/Teacher/NewProblem'], resolve),
            meta: {
              title: '教师出题'
            }
          },
          {
            path: 'myproblemsets',
            component: resolve => require(['./views/Home/Teacher/MyProblemSets'], resolve),
            meta: {
              title: '我的题目集'
            },
          },
          {
            path: 'userListDetails',
            component: resolve => require(['./components/userList/userListDetails'], resolve),
            meta: {
              title: '用户组详情'
            }
          },
          {
            path: 'userList',
            component: resolve => require(['./components/userList/userList'], resolve),
            meta: {
              title: '用户组列表'
            },
          },
        ]
      },
      {
        path: '/admin',
        redirect: '/admin/excel',
        component: resolve => require(['./components/layout'], resolve),
        children: [
          {
            path: 'excel',
            component: resolve => require(['./views/Home/Admin/Excel'], resolve),
            meta: {
              title: '学生信息上传'
            }
          },
          {
            path: 'bind',
            component: resolve => require(['./views/Home/Admin/Bind'], resolve),
            meta: {
              title: '学生信息绑定查询'
            }
          },
          {
            path: 'role',
            component: resolve => require(['./views/Home/Admin/RoleController'], resolve),
            meta: {
              title: '教师权限管理'
            }
          },
        ]
      },
    ],
  },
  {
    path: '/login',
    name: 'Login',
    component: resolve => require(['./pages/Login'], resolve),
    meta: {
      title: '登录'
    }
  },
  {
    path: '/register',
    name: 'Register',
    component: resolve => require(['./pages/Register'], resolve),
    meta: {
      title: '注册'
    }
  },
  {
    path: '/info',
    name: 'Info',
    component: resolve => require(['./pages/Info'], resolve),
    meta: {
      title: '个人信息'
    }
  },
  {
    path: '/problem-set',
    name: 'problemSet',
    component: resolve => require(['./pages/ProblemSet'], resolve),
    children: [
      {
        path: ':problemSetId',
        component: resolve => require(['./views/ProblemSet/Details'], resolve),
        meta: {
          title: '题目集概况'
        }
      },
      {
        path: ':problemSetId/problems',
        component: resolve => require(['./views/ProblemSet/ProblemList'], resolve),
        meta: {
          title: '题目列表'
        },
      },
      {
        path: ':problemSetId/problems/:problemId',
        component: resolve => require(['./views/ProblemSet/Problem'], resolve),
        meta: {
          title: '题目详情'
        },
      },
      {
        path: ':problemSetId/allProblems',
        component: resolve => require(['./views/ProblemSet/AllProblemList'], resolve),
        meta: {
          title: '题目管理'
        },
      },
      {
        path: ':problemSetId/userGroups',
        component: resolve => require(['./views/ProblemSet/UserGroups'], resolve),
        meta: {
          title: '开放信息'
        }
      },
      {
        path: ':problemSetId/problemSetRank',
        component: resolve => require(['./views/ProblemSet/problemSetRank'], resolve),
        meta: {
          title: '成绩排行榜'
        }
      }
    ],
  },
  {
    path: '/problems/:problemId',
    name: 'Question',
    component: resolve => require(['./pages/Student/QuestionPage'], resolve),
    meta: {
      title: '题目详情'
    },
  },
  {
    path: '/slice',
    component: resolve => require(['./pages/SliceUpload'], resolve),
    meta: {
      title: '断点续传测试'
    }
  },
]

const router = new VueRouter({
  routes
})

export default router
