<template>
  <div>
    <div class="judgeinput">
      <div>
        <el-row>
          <el-col :span="6">
            <el-select v-model="lang" placeholder="请选择语言" @change="chooseLanguage">
              <el-option v-for="language in languages" :label="language" :value="language" :key="language"></el-option>
            </el-select>
          </el-col>
          <el-col :span="6">
            <el-select v-model="cmOption.theme" placeholder="选择皮肤" class="chooseTheme">
              <el-option v-for="item in skins" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-col>
          <div>
            <el-button type="warning" @click="getRecords" style="float: right">提交记录</el-button>
          </div>
        </el-row>
      </div>
      <el-row style="margin-top: 20px" :gutter="20">
        <el-col :span="16">
          <div class="text">
            <codemirror style="border: 0.5px solid black" v-model="code" :options="cmOption" class="code-mirror" />
          </div>
          <div class="submit">
            <el-button type="primary" @click="tryToSubmit" :loading="isSubmitting" :disabled="$store.state.token===null">提交代码</el-button>
            <el-button :disabled="taskId==0" @click="showRes">查看结果</el-button>
          </div>
        </el-col>
        <el-col :span="8" >
          <el-card v-loading="testing" element-loading-text="拼命计算中..." element-loading-spinner="el-icon-loading" element-loading-background="rgba(256, 256, 256, 0.5)">
            <div class="testArea" style="display: flex; flex-direction: column;">
              <el-input id="testInput" :readonly="testing" type="textarea" resize="none" v-model="testInput" placeholder="请输入测试用例" />
              <el-input id="testOutput" style="margin-top: 20px;" readonly type="textarea" resize="none" v-model="testOutput" placeholder="这里将给出代码执行结果" />
            </div>
          </el-card>
          <div class="submit">
            <el-button plain @click="exeTest" :loading="testing">执行用例</el-button>
          </div>
        </el-col>
      </el-row>
    </div>

    <div class="others">
      <el-dialog title="当前结果" :visible.sync="showResult" width="80%" top="10vh" :append-to-body="true">
        <el-descriptions border>
          <el-descriptions-item label="使用语言">{{result.language}}</el-descriptions-item>
          <el-descriptions-item label="测试结果">{{result.message}}</el-descriptions-item>
          <el-descriptions-item label="通过率">{{result.totalCorrect}}/{{result.checkpointSize}}</el-descriptions-item>
          <el-descriptions-item label="内存消耗">{{(result.memory / 1024).toFixed(1)}}MB</el-descriptions-item>
          <el-descriptions-item label="时间占用">{{result.cpuTime}}ms</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{result.date}}</el-descriptions-item>
          <el-descriptions-item label="详细代码">
            <el-input type="textarea" v-model="result.code" readonly :autosize="true"></el-input>
          </el-descriptions-item>
        </el-descriptions>
      </el-dialog>

      <el-dialog title="提交结果" :visible.sync="showDetails" width="60%" top="10vh" :append-to-body="true">
        <el-table :data="resultDetail.details">
          <el-table-column width="120" label="测试点序号" align="center">
            <template slot-scope="scope">
              <span>{{ scope.$index + 1 }}</span>
            </template>
          </el-table-column>
          <el-table-column label="测试点结果">
            <template slot-scope="scope">
              <el-tag :type="getTagType(scope.row.result)" effect="dark">{{ scope.row.result }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="消耗内存">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.memory==-1?-1:(Number(scope.row.memory)/(1024**2)).toFixed(2) }}MB</span>
            </template>
          </el-table-column>
          <el-table-column label="消耗时间">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.time }}ms</span>
            </template>
          </el-table-column>
          <el-table-column label="测试点提示" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.tip }}</span>
            </template>
          </el-table-column>
        </el-table>
        <el-card style="margin-top: 10px;">
          <div slot="header">
            <span>详细代码</span>
          </div>
          <codemirror :value="resultDetail.code" :options="Object.assign({},cmOption,{readOnly:true, mode: resultDetail.cmMode})" />
        </el-card>
      </el-dialog>

      <el-dialog title="提交记录" :visible.sync="showRecords" width="70%" top="10vh" :append-to-body="true">
        <el-table :data="filterRecords" stripe border>
          <el-table-column prop="id" label="提交ID" width="90">
          </el-table-column>
          <el-table-column prop="pass" label="是否通过">
            <template slot-scope="scope">
              <el-tag :type="scope.row.pass?'success':'danger'" effect="dark">{{ scope.row.pass?'通过':'未通过' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="date" label="提交时间" width="230">
          </el-table-column>
          <el-table-column prop="language" label="使用语言" width="120">
          </el-table-column>
          <el-table-column prop="result" label="结果/错误" width="225">
          </el-table-column>
          <el-table-column label="操作" width="125">
            <template slot-scope="scope">
              <el-button @click="showDet(scope.$index)" plain circle><i class="el-icon-info"></i></el-button>
              <el-button @click="delRecord(scope.$index)" type="danger" circle><i class="el-icon-delete-solid"></i></el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import preCode from "@/assets/json/preCode.json";

export default {
  name: "judgeinput",
  components: {
    codemirror,
  },
  props: {
    problemId: Number,
  },
  data() {
    return {
      isSubmitting: false,
      taskId: 0,
      languages: [],
      skins: [
        {
          value: "panda-syntax",
          label: "panda-syntax",
        },
        {
          value: "idea",
          label: "idea",
        },
        {
          value: "eclipse",
          label: "eclipse",
        },
        {
          value: "darcula",
          label: "darcula",
        },
      ],
      code: "",
      lang: "C99",
      keepCode: false,
      cmOption: {
        tabSize: 4,
        styleActiveLine: true,
        lineNumbers: true,
        styleSelectedText: true,
        line: true,
        foldGutter: true,
        indentUnit: 4, // 缩进单位，值为空格数，默认为2
        indentWithTabs: true, // 在缩进时，是否需要把 n*tab宽度个空格替换成n个tab字符
        gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter"],
        highlightSelectionMatches: {
          showToken: /\w/,
          annotateScrollbar: true,
        },
        mode: undefined, // codemirror 对应的语言模式
        // hint.js options
        hintOptions: {
          // 当匹配只有一项的时候是否自动补全
          completeSingle: false,
        },
        //快捷键 可提供三种模式 sublime、emacs、vim
        keyMap: "sublime",
        matchBrackets: true,
        showCursorWhenSelecting: true,
        theme: "panda-syntax",
        extraKeys: { Alt: "autocomplete" },
      },
      showResult: false,
      result: {},
      showDetails: false,
      details: [],
      showRecords: false,
      records: [],
      getResInterval: undefined,
      resultDetail: {
        cmMode: undefined,
        code: "",
        details: [],
      },
      showTestInput: false,
      testInput: "",
      testOutput: "",
      testing: false,
    };
  },
  methods: {
    getTagType(result) {
      switch (result) {
        case "ACCEPT":
          return "success";
        case "COMPILE_ERROR":
          return "";
        default:
          return "danger";
      }
    },
    // 尝试提交
    async tryToSubmit() {
      if (this.code) {
        this.isSubmitting = true;
        await this.submit();
        this.isSubmitting = false;
      } else {
        this.$alert("请选择编译器并输入代码", "错误", {
          type: "error",
        });
      }
    },
    // 提交
    async submit() {
      let res = await this.$ajax.post(
          "/solve/trySolveProblem",
          {
            problemId: this.problemId,
            language: this.lang,
            problemSetId: this.$store.state.problemSetInfo.id,
            code: this.code,
          },
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          }
      );
      if (res.data.code == 0) {
        if (res.data.data) {
          this.taskId = res.data.data;
          this.getRes();
          await this.showRes();
        } else {
          this.$notify({
            title: "警告",
            message:
                "该题有超过5个人正在排队请求判断的题解，或者当前题目已经请求题解还未完成，请稍后再次尝试",
            type: "warning",
          });
        }
      }
    },
    getRes() {
      let res;
      this.getResInterval = setInterval(async () => {
        res = await this.$ajax.post(
            "/solve/solveTaskResult",
            {
              taskId: this.taskId,
            },
            {
              headers: {
                Authorization: `Bearer ${localStorage.getItem("token")}`,
              },
              timeout: 2000,
            }
        );
        if (res.status === 200 && res.data.code !== 0) {
          clearInterval(this.getResInterval);
          this.$emit("updatePassRadio");
          this.showResult = true;
        }

      }, 2000);
    },
    // 查看当前提交结果
    async showRes() {
      let res = await this.$ajax.post(
          "/solve/solveTaskResult",
          {
            taskId: this.taskId,
          },
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          }
      );
      if (res.data.data != null){
        this.result = res.data.data;
        this.result.message = res.data.message;
        this.showResult = true;
      }
    },
    async showDet(index) {
      let res = await this.$ajax.post(
          "/solve/solveTaskResult",
          {
            taskId: this.filterRecords[index].id,
          },
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          }
      );
      if (res.data.code == 0) {
        this.resultDetail.code = this.filterRecords[index].code;
        this.resultDetail.cmMode = this.getCmMode(
            this.filterRecords[index].language
        );
        this.resultDetail.details = res.data.data;
        this.showDetails = true;
      }
    },
    // 查看提交记录
    async getRecords() {
      let res = await this.$ajax.post(
          "/solve/mySolveRecords",
          {},
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          }
      );
      this.records = res.data.data;
      this.showRecords = true;
    },
    // 删除提交记录
    async delRecord(index) {
      let res = await this.$ajax.post(
          "/solve/deleteMySolveRecord",
          {
            taskId: this.filterRecords[index].id,
          },
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          }
      );
      if (res.data.code == 0) {
        this.$message({
          message: "成功移除提交记录",
          type: "success",
          showClose: false,
          duration: 1000,
        });
      }
      this.filterRecords.splice(index, 1);
    },
    // 用户这一题目最后提交的代码
    async latestProblemCommitCode() {
      let res = await this.$ajax.post(
          "/solve/latestProblemCommitCode",
          {
            problemId: this.problemId,
            problemSetId: this.$store.state.problemSetInfo.id
          },
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          }
      );
      if (res.data.code === 0) {
        this.lang = res.data.data.language;
        this.code = res.data.data.code;
      }
    },
    // 获取可用语言
    async getSupportLanguage() {
      let res = await this.$ajax.get(
          "/solve/supportLanguages",
          {},
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          }
      );
      if (res.data.code === 0) {
        this.languages = res.data.data
      }
    },
    // 选择语言
    chooseLanguage(newVal) {
      this.lang = newVal;
    },
    // 设置初始代码
    setPreCode(language) {
      switch (language) {
        case "C99":
          this.code = preCode.cCode;
          break;
        case "CPP17":
          this.code = preCode.cppCode;
          break;
        case "PYTHON3":
          this.code = preCode.python3Code;
          break;
        case "JAVA8":
          this.code = preCode.javaCode;
      }
    },
    // 执行测试用例
    async exeTest() {
      if (this.lang && this.code) {
        this.testing = true;
        this.testOutput = "";
        let res = await this.$ajax.post(
            "/solve/runCodeTest",
            {
              problemId: this.problemId,
              problemSetId: this.$store.state.problemSetInfo.id,
              code: this.code,
              language: this.lang,
              customInput: this.testInput,
            },
            {
              headers: {
                Authorization: `Bearer ${localStorage.getItem("token")}`,
              },
            }
        );
        if (res.data.code !== 0) {
          this.testOutput = res.data.message;
          this.testing = false;
        }
        console.log(res)
        this.getResInterval = setInterval(async () => {
          let res1 = await this.$ajax.post(
              "/solve/solveTaskResult",
              {
                taskId: res.data.data,
              },
              {
                headers: {
                  Authorization: `Bearer ${localStorage.getItem("token")}`,
                },
              }
          );
          if (res1.status === 200 && res1.data.code !== 0) {
            clearInterval(this.getResInterval);
            this.testOutput = res1.data.data.output;
            this.testing = false;
          }

        }, 2000);
      } else {
        this.$alert("请选择编译器并输入代码", "错误", {
          type: "error",
        });
      }
    },
    // 根据语言返回正确的 cmMode
    getCmMode(language) {
      language = language.toUpperCase();
      if (language.indexOf("C") === 0) {
        if (language.indexOf("CPP") === 0) {
          return "text/x-c++src";
        } else {
          return "text/x-c";
        }
      } else {
        if (language.indexOf("JAVA") === 0) {
          return "text/x-java";
        } else if (language.indexOf("PYTHON") === 0) {
          return "text/x-python";
        }
      }
    },
  },
  async created() {
    this.getSupportLanguage();
    this.latestProblemCommitCode();
  },
  watch: {
    $route() {
      this.isSubmitting = false;
      this.taskId = 0;
      this.skin = "darcula";
      this.code = "";
      this.lang = "";
      this.langVersion = [];
    },
    problemId() {
      this.isSubmitting = false;
      this.taskId = 0;
      this.skin = "darcula";
      this.code = "";
      this.latestProblemCommitCode();
    },
    lang(newVal) {
      this.cmOption.mode = this.getCmMode(newVal);
      this.setPreCode(newVal);
    },
  },
  computed: {
    // 根据题号过滤解题记录
    filterRecords() {
      return this.records
          .filter((record) => {
            if (record.memory != -1) {
              record.memory = record.memory / 2 ** 20;
            }
            record.memory += " MB";
            record.time += " ms";
            return record.problemId == this.problemId;
          })
          .reverse();
    },
  },
  beforeDestroy() {
    clearInterval(this.getResInterval);
  },
};

import { codemirror } from "vue-codemirror";
import "codemirror/lib/codemirror.css";
// language
import "codemirror/mode/clike/clike.js";
import "codemirror/mode/python/python.js";
// theme css
import "codemirror/theme/panda-syntax.css";
import "codemirror/theme/idea.css";
import "codemirror/theme/eclipse.css";
import "codemirror/theme/darcula.css";
// require active-line.js
import "codemirror/addon/selection/active-line.js";
// styleSelectedText
import "codemirror/addon/selection/mark-selection.js";
import "codemirror/addon/search/searchcursor.js";
// hint
import "codemirror/addon/hint/show-hint.js";
import "codemirror/addon/hint/show-hint.css";
import "codemirror/addon/hint/javascript-hint.js";
import "codemirror/addon/selection/active-line.js";
// highlightSelectionMatches
import "codemirror/addon/scroll/annotatescrollbar.js";
import "codemirror/addon/search/matchesonscrollbar.js";
import "codemirror/addon/search/searchcursor.js";
import "codemirror/addon/search/match-highlighter.js";
// keyMap
import "codemirror/addon/edit/matchbrackets.js";
import "codemirror/addon/comment/comment.js";
import "codemirror/addon/dialog/dialog.js";
import "codemirror/addon/dialog/dialog.css";
import "codemirror/addon/search/searchcursor.js";
import "codemirror/addon/search/search.js";
import "codemirror/keymap/sublime.js";
// foldGutter
import "codemirror/addon/fold/foldgutter.css";
import "codemirror/addon/fold/brace-fold.js";
import "codemirror/addon/fold/comment-fold.js";
import "codemirror/addon/fold/foldcode.js";
import "codemirror/addon/fold/foldgutter.js";
import "codemirror/addon/fold/indent-fold.js";
import "codemirror/addon/fold/markdown-fold.js";
import "codemirror/addon/fold/xml-fold.js";
</script>

<style scoped>
.text >>> .code-mirror {
  font-size: 14px;
  line-height: 2em;
  text-align: left !important;
}

/* .text {
  font-size: 13px;
  line-height: 150%;
  text-align: left !important;
  height:100%;
} */

.box-card {
  height: 1000px;
}

.testArea >>> .el-textarea__inner {
  height: 120px;
}

.submit {
  display: flex;
  flex-direction: row-reverse;
}

.submit >>> button.el-button.el-button {
  margin-block: 10px;
  margin-inline: 5px;
}

.chooseTheme {
  margin-left: 10px;
}
</style>
