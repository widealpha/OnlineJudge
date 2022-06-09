<template>
  <div class="input">
    <el-card style="border: 0; box-sizing: border-box; padding: 20px">
      <div slot="header" class="text">
        <span>编辑题目</span>
      </div>
      <el-form :rules="problemRules" :model="problemInfo">
        <div class="text">
          <h3>基本信息</h3>
        </div>
        <el-form-item label="标题：" prop="name" class="text">
          <el-input
            v-model="problemInfo.name"
            placeholder="请输入1-80个字符作为标题"
          ></el-input>
        </el-form-item>
        <el-form-item label="难度：" prop="difficulty">
          <el-rate
            v-model="problemInfo.difficulty"
            style="text-align: left; margin-top: 0.75em"
          />
        </el-form-item>

        <el-form-item prop="myTags" style="margin-top: -1em">
          <div style="text-align: left">
            <ul
              style="
                padding: 0;
                margin-top: 0;
                margin-bottom: 0.2em;
                list-style-type: none;
              "
            >
              <li
                v-for="item in problemInfo.myTags"
                :key="item.index"
                style="height: 2em"
              >
                <el-tag
                  :disable-transitions="false"
                  closable
                  effect="plain"
                  size="small"
                  @close="deleteTag(item)"
                >
                  {{ item.value }}
                </el-tag>
              </li>
            </ul>
            <el-dropdown
              ref="tagDrop"
              placement="bottom-start"
              size="mini"
              trigger="click"
            >
              <el-button
                icon="el-icon-circle-plus-outline"
                plain
                round
                size="mini"
                @click="tempTag = []"
                >添加知识点
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-cascader-panel
                  ref="tagCascader"
                  v-model="tempTag"
                  :props="tagCascaderProps"
                  @change="selectTag"
                />
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </el-form-item>
        <el-form-item label="题面：" prop="description">
          <mavon-editor
            v-model="problemInfo.description"
            :toolbars="markdownOption"
            class="markdown"
            fontSize="16px"
        /></el-form-item>
        <el-form-item>
          
        </el-form-item>
      </el-form>
    <el-button @click="commit" type="primary">提交题目</el-button>
    </el-card>
  </div>
</template>

<script>
import Vue from "vue";
import mavonEditor from "mavon-editor";
import "mavon-editor/dist/css/index.css";

Vue.use(mavonEditor);
export default {
  name: "newSingleChioceQuestion",
  data() {
    return {
      problemInfo: {
        description: "",
        name: "",
        difficulty: 0,
        myTags: [],
      },
      myTags: [],
      tempTag: [],
      tagStr: "",
      tagCascaderProps: {
        getTags: this.getTags,
        lazy: true,
        async lazyLoad(node, resolve) {
          resolve(await this.getTags(node));
        },
      },
      problemRules: {
        name: [
          {
            required: true,
            message: "标题不能为空哦",
            trigger: "blur",
          },
          {
            min: 1,
            max: 80,
            message: "长度在1-80个字符",
            trigger: "blur",
          },
        ],
        difficulty: [
          {
            required: true,
            message: "难度不能为空哦",
            trigger: "blur",
          },
        ],
        myTags: [
          {
            required: true,
            message: "请选择至少一个标签",
            trigger: "none",
          },
        ],
        description: [
          {
            required: true,
            message: "题面描述不能为空",
            trigger: "blur",
          },
        ],
      },
      markdownOption: {
        bold: true, // 粗体
        italic: true, // 斜体
        header: true, // 标题
        underline: true, // 下划线
        strikethrough: true, // 中划线
        mark: true, // 标记
        superscript: true, // 上角标
        subscript: true, // 下角标
        quote: true, // 引用
        ol: true, // 有序列表
        ul: true, // 无序列表
        link: true, // 链接
        imagelink: false, // 图片链接
        code: true, // code
        table: true, // 表格
        fullscreen: true, // 全屏编辑
        readmodel: true, // 沉浸式阅读
        htmlcode: true, // 展示html源码
        help: true, // 帮助
        /* 1.3.5 */
        undo: true, // 上一步
        redo: true, // 下一步
        trash: true, // 清空
        save: true, // 保存（触发events中的save事件）
        /* 1.4.2 */
        navigation: true, // 导航目录
        /* 2.1.8 */
        alignleft: true, // 左对齐
        aligncenter: true, // 居中
        alignright: true, // 右对齐
        /* 2.2.1 */
        subfield: true, // 单双栏模式
        preview: true, // 预览
      },
    };
  },
  methods: {
    commit(){
      this.$message.success("出题成功")
    },
    // 获取可选标签
    async getTags(node) {
      let res;
      if (node.root) {
        res = await this.$ajax.post(
          "/problem/getTopLevelTag",
          {},
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          }
        );
      } else {
        res = await this.$ajax.post(
          "/problem/getChildrenTagByParentTagId",
          {
            parentTagId: node.data.id,
          },
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          }
        );
      }

      const nodes = res.data.data.map((item) => ({
        value: item.name,
        label: item.name,
        id: item.id,
        leaf: item.level >= 3,
        disabled: this.problemInfo.myTags.some((tag) => tag.id === item.id),
      }));

      return nodes;
    },
    // 选中标签
    selectTag(value) {
      const node = this.$refs.tagCascader.getCheckedNodes()[0];
      node.data.disabled = true;
      this.problemInfo.myTags.push({
        value: value.join("/"),
        id: node.data.id,
      });

      this.$refs.tagDrop.hide(); // el-dropdown 源码中的方法，手动关闭下拉列表
    },
    // 删除标签
    deleteTag(item) {
      const node = this.$refs.tagCascader.getNodeByValue(item.value.split("/"));
      if (node) {
        node.data.disabled = false;
      }
      this.problemInfo.myTags.splice(this.problemInfo.myTags.indexOf(item), 1);
    },
  },
  async created() {},
};
</script>

<style lang="less" scoped>
.input {
  border-radius: 4px;
}
.text {
  text-align: left;
}
.markdown {
  height: 800px;
  min-width: 0%;
  max-width: calc(100vw - 440px);
  margin: 0 auto;
}
.form-control {
  display: block;
  width: 100%;
  padding: 0.375rem 0.75rem;
  font-size: 0.875rem;
  line-height: 1.5;
  color: #495057;
  background-color: #fff;
  background-clip: padding-box;
  border: 1px solid #ced4da;
  border-radius: 0.125rem;
  transition: border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
}
</style>
