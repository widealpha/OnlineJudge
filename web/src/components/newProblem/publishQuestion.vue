<template>
  <div class="input">
    <el-card style="border: 0">
      <div slot="header" class="text">
        <span>编辑题目</span>
      </div>
      <!--题目信息编辑区,包括基本信息,题面描述,标准答案,题目判定,点击提交按钮后进行表单格式化校验,校验标准全部是不为空-->
      <div class="questionInfo">
        <!--基本信息表单(标题,难度,作者,单位)-->
        <el-form
          ref="BasicInfo"
          :model="BasicInfo"
          :rules="basicRules"
          class="basicInfo"
        >
          <div class="text">
            <h3>基本信息</h3>
          </div>
          <el-form-item label="标题" prop="title">
            <el-input
              v-model="BasicInfo.title"
              placeholder="请输入1-80个字符作为标题"
            ></el-input>
          </el-form-item>
          <el-form-item label="难度" prop="difficulty">
            <el-rate
              v-model="BasicInfo.difficulty"
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
                  v-for="item in BasicInfo.myTags"
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
          <el-divider />
          <el-form-item label="题面" prop="description">
            <mavon-editor
              v-model="BasicInfo.description"
              :toolbars="markdownOption"
              class="markdown"
              fontSize="16px"
            />
          </el-form-item>
        </el-form>
      </div>
      <el-divider />
      <el-button style="width: 10em" type="primary" @click="onSubmit">
        {{ problemId === 0 ? "题目上传" : "提交修改" }}
      </el-button>
    </el-card>
  </div>
</template>

<script>
import Vue from "vue";
import mavonEditor from "mavon-editor";
import "mavon-editor/dist/css/index.css";

Vue.use(mavonEditor);

export default {
  name: "publishQuestion",
  data() {
    return {
      tagStr: "",
      problemId: 0,
      BasicInfo: {
        title: "",
        difficulty: 1,
        myTags: [],
        description:
          "这是一个编程题模板。请在这里写题目描述。例如：本题目要求读入2个整数A和B，然后输出它们的和。\n" +
          "\n" +
          "### 输入格式:\n" +
          "\n" +
          "请在这里写输入格式。例如：输入在一行中给出2个绝对值不超过1000的整数A和B。\n" +
          "\n" +
          "### 输出格式:\n" +
          "\n" +
          "请在这里描述输出格式。例如：对每一组输入，在一行中输出A+B的值。\n" +
          "\n" +
          "### 输入样例:\n" +
          "\n" +
          "在这里给出一组输入。例如：\n" +
          "\n" +
          "```in\n" +
          "18 -299\n" +
          "```\n" +
          "\n" +
          "### 输出样例:\n" +
          "\n" +
          "在这里给出相应的输出。例如：\n" +
          "\n" +
          "```out\n" +
          "-281\n" +
          "```",
      },
      // 基本信息校验标准(title长度在1-80&&不为空,其他不为空)
      basicRules: {
        title: [
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
      tagCascaderProps: {
        getTags: this.getTags,
        lazy: true,
        async lazyLoad(node, resolve) {
          resolve(await this.getTags(node));
        },
      },
      tempTag: [],
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
    //上一步返回后，根据问题id获取问题信息
    async getProblemInfoById() {
      let res = await this.$ajax.post(
        "/problem/info",
        {
          problemId: this.problemId,
        },
        {
          headers: {
            Authorization: `Bearer ${this.$store.state.token}`,
          },
        }
      );

      if (res.data.code === 0) {
        let data = res.data.data;
        
        this.BasicInfo.title = data.name;
        this.BasicInfo.difficulty = data.difficulty;
        this.BasicInfo.description = data.description;
        this.BasicInfo.myTags = [];
        data.tags.forEach((tag) => {
          this.BasicInfo.myTags.push({
            value: tag.name,
            id: tag.id,
          });
          const node = this.$refs["tagCascader"].getNodeByValue(tag.name);
          node;
        });
      }
    },
    // 添加一个问题
    async addAProblem() {
      let res = await this.$ajax.post(
        "/problem/addProgramingProblem",
        {
          name: this.BasicInfo.title,
          difficulty: this.BasicInfo.difficulty,
          tags: this.tagStr,
          description: this.BasicInfo.description,
        },
        {
          headers: {
            Authorization: `Bearer ${this.$store.state.token}`,
          },
        }
      );

      if (res.data.code == 0) {
        this.$message({
          message: "提交成功",
          type: "success",
        });

        this.$router.push({
          name: "newProblem",
          query: { problemId: res.data.data },
          params: { step: 2 },
        });
      } else {
        this.$message({
          message: "提交失败",
          type: "error",
        });
      }
    },
    // 更新一个问题
    async updateAProblem() {
      let res = await this.$ajax.post(
        "/problem/updateProgramingProblem",
        {
          problemId: this.problemId,
          name: this.BasicInfo.title,
          difficulty: this.BasicInfo.difficulty,
          tags: this.tagStr,
          description: this.BasicInfo.description,
        },
        {
          headers: {
            Authorization: `Bearer ${this.$store.state.token}`,
          },
        }
      );
      if (res.data.code == 0) {
        this.$message({
          message: "提交成功",
          type: "success",
        });
      } else {
        this.$message({
          message: "提交失败",
          type: "error",
        });
      }
    },
    // 验证表单并判断是更新还是上传
    async onSubmit() {
      this.$refs.BasicInfo.validate(async (valid) => {
        this.tagStr = JSON.stringify(
          this.BasicInfo.myTags.map((item) => item.id)
        );
        if (valid) {
          if (this.problemId === 0) {
            this.addAProblem();
          } else {
            this.updateAProblem();
          }
        }
      });
    },
    // 获得编译器
    handleChange(value) {
      this.lang = value[1];
      return this.lang;
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
              Authorization: `Bearer ${this.$store.state.token}`,
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
              Authorization: `Bearer ${this.$store.state.token}`,
            },
          }
        );
      }

      const nodes = res.data.data.map((item) => ({
        value: item.name,
        label: item.name,
        id: item.id,
        leaf: item.level >= 3,
        disabled: this.BasicInfo.myTags.some((tag) => tag.id === item.id),
      }));

      return nodes;
    },
    // 选中标签
    selectTag(value) {
      const node = this.$refs.tagCascader.getCheckedNodes()[0];
      node.data.disabled = true;
      this.BasicInfo.myTags.push({
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
      this.BasicInfo.myTags.splice(this.BasicInfo.myTags.indexOf(item), 1);
    },
  },
  async created() {
    let problemId = this.$route.query.problemId;

    if (problemId) {
      this.problemId = Number(problemId);

      await this.getProblemInfoById();
    }
  },
  watch: {
    $route(newVal) {
      let problemId = newVal.query.problemId;
      if (problemId) {
        this.problemId = Number(problemId);

        this.getProblemInfoById();
      } else {
        this.problemId = 0;
        this.BasicInfo = {
          title: "",
          difficulty: 1,
          myTags: [],
          description:
            "这是一个编程题模板。请在这里写题目描述。例如：本题目要求读入2个整数A和B，然后输出它们的和。\n" +
            "\n" +
            "### 输入格式:\n" +
            "\n" +
            "请在这里写输入格式。例如：输入在一行中给出2个绝对值不超过1000的整数A和B。\n" +
            "\n" +
            "### 输出格式:\n" +
            "\n" +
            "请在这里描述输出格式。例如：对每一组输入，在一行中输出A+B的值。\n" +
            "\n" +
            "### 输入样例:\n" +
            "\n" +
            "在这里给出一组输入。例如：\n" +
            "\n" +
            "```in\n" +
            "18 -299\n" +
            "```\n" +
            "\n" +
            "### 输出样例:\n" +
            "\n" +
            "在这里给出相应的输出。例如：\n" +
            "\n" +
            "```out\n" +
            "-281\n" +
            "```",
        };
      }
    },
  },
};
</script>

<style lang="less" scoped>
.input {
  border-radius: 4px;
}

.text {
  text-align: left;
  margin-left: 20px;
}

.el-row {
  margin-bottom: 5px;
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

.markdown {
  height: 800px;
  min-width: 0%;
  max-width: calc(100vw - 440px);
  margin: 0 auto;
}
</style>
