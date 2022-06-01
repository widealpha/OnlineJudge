<template>
  <div>
    <el-card>
      <div slot="header" class="text">
        <div style="text-align: left">
          <el-button @click="$emit('update:step', 1)" plain>上一步</el-button>
          <el-button
            :disabled="!existCheckpoints"
            style="float: right; margin-right: 20px"
            @click="$emit('update:step', 3)"
            plain
            >下一步</el-button
          >
        </div>
      </div>
      <div>
        <div style="margin-bottom: 20px">
          <el-upload
            action=""
            ref="upload"
            :multiple="false"
            :on-change="analyzeZip"
            :auto-upload="false"
          >
            <el-button
              slot="trigger"
              style="margin-left: 10px"
              size="mini"
              type="success"
              >上传测试数据<i class="el-icon-upload"></i
            ></el-button>
            <el-button
              style="margin-left: 10px"
              size="mini"
              type="primary"
              @click="downloadTestPoints"
              >下载测试数据<i class="el-icon-download"></i
            ></el-button>
          </el-upload>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import Hex from "crypto-js/enc-hex";
import WordArray from "crypto-js/lib-typedarrays";
import sha256 from "crypto-js/sha256";
export default {
  data() {
    return {
      problemId: 0,

      sha256: "",
      loading: false,
    };
  },
  methods: {
    // 上传并加密文件
    async analyzeZip(file) {
      const reader = new FileReader();
      reader.onloadend = async (evt) => {
        if (evt.target.readyState === FileReader.DONE) {
          // DONE == 2
          const hash = sha256(WordArray.create(evt.target.result)).toString(
            Hex
          );
          let formData = new FormData();
          formData.append("problemId", this.problemId);
          formData.append("sha256", hash);
          formData.append("file", file.raw, "file");
          let res = await this.$ajax.post(
            "/problem/uploadCheckpoints",
            formData,
            {
              headers: {
                Authorization: `Bearer ${this.$store.state.token}`,
                contentType: "multipart/form-data",
              },
            }
          );
          res;
          if (res.data.code === 0) {
            this.$message.success("上传成功！");
            await this.getProblemInfoById();
          } else {
            this.$message.error(res.data.message);
          }
        }
      };

      reader.readAsArrayBuffer(file.raw);
    },
    // 通过id获取更新题目信息（是否存在测试点）
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
        this.$store.commit("setProblemInfo", data);
      }
    },
    async downloadTestPoints() {
      let res = await this.$ajax.get(
        "/problem/downloadCheckpoints",
        {
          problemId: this.problemId,
        },
        {
          headers: {
            Authorization: `Bearer ${this.$store.state.token}`,
          },
          responseType: "blob", // 以 blob 类型接收后端发回的响应数据
        }
      );

      const content = res.data; // 接收响应内容
      const blob = new Blob([content]); // 构造一个blob对象来处理数据
      let fileName = `测试点${this.problemId}.zip`;
      // 对于<a>标签，只有 Firefox 和 Chrome（内核） 支持 download 属性
      // IE10以上支持blob但是依然不支持download
      if ("download" in document.createElement("a")) {
        //支持a标签download的浏览器
        const link = document.createElement("a"); // 创建a标签
        link.download = fileName; // a标签添加属性
        link.style.display = "none";
        link.href = URL.createObjectURL(blob);
        document.body.appendChild(link);
        link.click(); // 执行下载
        URL.revokeObjectURL(link.href); // 释放url
        document.body.removeChild(link); // 释放标签
      } else {
        // 其他浏览器
        navigator.msSaveBlob(blob, fileName);
      }
    },
  },
  async created() {
    const { problemId } = this.$route.query;
    if (problemId) {
      this.problemId = Number(problemId);
      this.getProblemInfoById();
    }
  },
  computed: {
    existCheckpoints() {
      return this.$store.state.problemInfo.existCheckpoints;
    },
  },
  watch: {
    $route(newVal) {
      let problemId = newVal.query.problemId;
      problemId
        ? (this.problemId = Number(problemId))
        : this.$emit("update:step", 1);
    },
  },
};
</script>