<template>
  <div>
    <el-container>
      <el-header style="background-color: white; height: 50px">
        <el-page-header
          style="line-height: 50px"
          content="回到上一页"
          @back="$router.go(-1)"
        ></el-page-header>
      </el-header>
      <el-main style="padding-top: 0px">
        <el-card class="main">
          <div slot="header" style="text-align: left">
            <span><b>个人信息</b></span>
          </div>
          <div class="container" style="text-align: left">
            <div class="avatar" style="display: flex">
              <el-tooltip class="item" effect="dark" placement="right-end">
                <div slot="content">
                  为了保障您的使用体验，<br />请尽可能上传2M以下的图片
                </div>
                <el-upload
                  class="avatar-uploader"
                  action=""
                  :show-file-list="false"
                  :on-change="imgCropper"
                  :auto-upload="false"
                  ref="upload"
                >
                  <el-image
                    style="width: 10vw; height: 10vw; float: left"
                    :src="myInfo.avatar"
                  ></el-image>
                </el-upload>
              </el-tooltip>
              <div
                style="
                  width: 10vw;
                  height: 10vw;
                  background: rgba(150, 150, 150, 0.7);
                  position: absolute;
                "
                v-if="isUpdatingAvatar"
              >
                <i
                  class="el-icon-loading"
                  style="
                    position: relative;
                    left: 3vw;
                    top: 3vw;
                    font-size: 4vw;
                    color: white;
                  "
                ></i>
              </div>
              <div style="margin-left: 50px">
                <div>
                  <el-button type="info" @click="defaultAvatar"
                    >恢复默认头像</el-button
                  >
                </div>
                <div style="margin-top: 10px">
                  <el-button type="warning" @click="isUpdatingPsw = true"
                    >修改密码</el-button
                  >
                </div>
                <div style="margin-top: 10px" v-if="!needBind">
                  <el-button type="danger" @click="deleteBind"
                    >账号解绑</el-button
                  >
                </div>
              </div>
            </div>
            <el-divider content-position="left"><b>基本信息</b></el-divider>
            <div class="baseInfo">
              <el-form
                label-position="left"
                :model="bindForm"
                ref="bindForm"
                :rules="bindRules"
                :hide-required-asterisk="true"
                :inline-message="true"
              >
                <el-form-item label="昵称：">
                  <span class="infos">{{ myInfo.nickname }}</span>
                </el-form-item>
                <el-form-item label="身份：">
                  <el-tag v-for="role in myInfo.roles" :key="role" style="margin-right:5px">
                    {{ role }}
                  </el-tag>
                </el-form-item>
                <el-form-item prop="name" label="姓名：">
                  <el-input
                    v-if="inputingMyInfo"
                    style="width: 10vw"
                    placeholder="请输入姓名"
                    v-model="bindForm.name"
                  ></el-input>
                  <span class="infos">{{ myInfo.name }}</span>
                </el-form-item>
                <el-form-item prop="stuNum" label="学号：">
                  <el-input
                    v-if="inputingMyInfo"
                    style="width: 10vw"
                    placeholder="请输入学号"
                    v-model="bindForm.stuNum"
                  ></el-input>
                  <span class="infos">{{ myInfo.stuNum }}</span>
                </el-form-item>
                <el-form-item v-if="needBind" prop="code" label="绑定码：">
                  <el-input
                    v-if="inputingMyInfo"
                    style="width: 10vw"
                    placeholder="请输入绑定码"
                    v-model="bindForm.code"
                  ></el-input>
                </el-form-item>
              </el-form>
              <el-button
                v-if="needBind && !inputingMyInfo"
                @click="inputingMyInfo = true"
                >用户绑定</el-button
              >
              <el-button v-if="inputingMyInfo" @click="bind"
                >提交绑定信息</el-button
              >
            </div>
          </div>
        </el-card>
      </el-main>
    </el-container>

    <div class="others">
      <el-dialog
        title="修改密码"
        :visible.sync="isUpdatingPsw"
        @open="$refs.updatePswForm ? $refs.updatePswForm.resetFields() : ''"
      >
        <el-form
          :model="updatePswForm"
          ref="updatePswForm"
          :rules="updatePswRules"
        >
          <el-form-item label="旧密码" prop="oldPsw">
            <el-input
              type="password"
              @keydown.enter.native="updatePsw"
              v-model="updatePswForm.oldPsw"
              placeholder="请输入原来的密码"
              prefix-icon="el-icon-key"
            ></el-input>
          </el-form-item>
          <el-form-item label="新密码" prop="newPsw">
            <el-input
              type="password"
              @keydown.enter.native="updatePsw"
              v-model="updatePswForm.newPsw"
              placeholder="请输入新的密码"
              prefix-icon="el-icon-key"
            ></el-input>
          </el-form-item>
        </el-form>
        <el-button @click="updatePsw" type="primary">更新密码</el-button>
      </el-dialog>

      <div
        class="out"
        v-if="cropping"
        style="
          position: fixed;
          left: 0%;
          top: 0%;
          background: rgba(50, 50, 50, 0.7);
          width: 100%;
          height: 100%;
        "
      >
        <div
          class="in"
          style="
            position: relative;
            top: 5vh;
            width: 80vh;
            height: 80vh;
            margin: 0 auto;
          "
        >
          <vue-cropper
            autoCrop
            :img="pic"
            ref="cropper"
            centerBox
            :outputSize="1"
            :infoTrue="true"
            :outputType="'png'"
            fixed
            :fixedNumber="[1, 1]"
          />
          <el-button
            type="danger"
            style="margin-top: 25px; margin-inline: 20px"
            @click="
              $refs.upload.clearFiles();
              cropping = false;
              pic = '';
            "
            >取消裁剪</el-button
          >
          <el-button
            type="success"
            style="margin-top: 25px; margin-inline: 20px"
            @click="setAvatar"
            >裁剪完了</el-button
          >
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Vue from "vue";
import VueCropper from "vue-cropper";
import { blobToBase64, dataURLtoFile } from "../assets/utils/helper";
import uploadImg from "../assets/utils/uploadimg";
Vue.use(VueCropper);

export default {
  data() {
    return {
      options: null,
      avatar: null,
      pic: "",
      cropping: false,
      myInfo: {},
      isUpdatingAvatar: false,
      isUpdatingPsw: false,
      updatePswForm: {
        oldPsw: "",
        newPsw: "",
      },
      updatePswRules: {
        oldPsw: [
          {
            required: true,
            message: "旧密码不得为空",
            trigger: ["blur", "change"],
          },
        ],
        newPsw: [
          {
            required: true,
            message: "新密码不得为空",
            trigger: ["blur", "change"],
          },
        ],
      },
      needBind: false,
      inputingMyInfo: false,
      bindForm: {
        name: "",
        stuNum: "",
        code: "",
      },
      bindRules: {
        name: [
          {
            required: true,
            message: "姓名不得为空！",
            trigger: ["blur", "change"],
          },
        ],
        stuNum: [
          {
            required: true,
            message: "学号不得为空！",
            trigger: ["blur", "change"],
          },
          {
            pattern: /^\d{12}$/,
            message: "请输入12位学号！",
            trigger: ["blur", "change"],
          },
        ],
        code: [
          {
            required: true,
            message: "绑定码不得为空！",
            trigger: ["blur", "change"],
          },
          {
            pattern: /^[A-Z]{6}$/,
            message: "绑定码应是六位大写字母！",
            trigger: ["blur", "change"],
          },
        ],
      },
    };
  },
  methods: {
    async getMyInfo() {
      const token = localStorage.getItem("token");
      let res = await this.$ajax.post(
        "/user/info",
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (res.data.code == 0) {
        this.myInfo = res.data.data;
        this.$store.commit("setMyInfo", res.data.data);
        this.$set(this.myInfo, "avatar", res.data.data.avatar);
        this.$set(this.myInfo, "stuNum", res.data.data.stuNum);
        this.$set(this.myInfo, "name", res.data.data.name);
        this.needBind = res.data.data.stuNum == null;
      }
    },

    imgCropper(file) {
      const isJPG =
        file.raw.type === "image/jpeg" || file.raw.type === "image/png";
      const isLt4M = file.raw.size / 1024 / 1024 < 4;
      if (!isJPG) {
        this.$message.error("上传头像图片只能是 JPG 或 PNG 格式!");
        return;
      }
      if (!isLt4M) {
        this.$message.error("上传头像图片大小不能超过 4MB!");
        return;
      }
      let reader = new FileReader();
      reader.readAsDataURL(file.raw);
      reader.onload = () => {
        this.pic = reader.result;
        this.cropping = true;
      };
    },
    setAvatar() {
      this.load();
      this.cropping = false;
      this.$refs.cropper.getCropBlob(async (data) => {
        const base64 = await blobToBase64(data);
        const url = await uploadImg(dataURLtoFile(base64));
        let res = await this.$ajax.post(
          "/user/updateInfo",
          {
            nickname: this.$store.state.myInfo.nickname,
            name: this.$store.state.myInfo.name,
            avatar: url,
          },
          {
            headers: {
              Authorization: `Bearer ${this.$store.state.token}`,
            },
          }
        );
        this.finishLoading();
        if (res.data.code == 0) {
          this.$set(this.myInfo, "avatar", url);
          this.myInfo.avatar = url;
          this.$message.success("修改成功");
        } else {
          this.$message.error("信息修改失败，请重试");
        }
      });
    },
    load() {
      this.options = this.$message({
        message: "正在修改，请稍等...",
        duration: 0,
      });
    },
    finishLoading() {
      this.options ? this.options.close() : "";
    },

    updatePsw() {
      this.$refs.updatePswForm.validate(async (valid) => {
        if (valid) {
          console.log(valid);
        }
      });
    },
    async defaultAvatar() {},
    bind() {
      this.$refs.bindForm.validate(async (valid) => {
        if (valid) {
          let res = await this.$ajax.post(
            "/bind/bindStuNum",
            {
              name: this.bindForm.name,
              stuNum: this.bindForm.stuNum,
              code: this.bindForm.code,
            },
            {
              headers: {
                Authorization: `Bearer ${this.$store.state.token}`,
              },
            }
          );
          if (res.data.code == 0 && res.data.data) {
            this.getMyInfo().then(() => {
              this.inputingMyInfo = false;
              this.bindForm = {
                name: "",
                stuNum: "",
                code: "",
              };
              this.$message({
                message: "用户绑定成功！",
                type: "success",
              });
            });
          } else {
            this.$message({
              message: "用户绑定失败！",
              type: "error",
            });
          }
        }
      });
    },
    async deleteBind() {
      let res = await this.$ajax.post(
        "/bind/unBindStuNum",
        {},
        {
          headers: {
            Authorization: `Bearer ${this.$store.state.token}`,
          },
        }
      );
      if (res.data.code == 0 && res.data.data) {
        this.$notify({
          title: "成功",
          message: "解绑成功！",
          type: "success",
        });
        this.getMyInfo();
      } else {
        this.$notify({
          title: "失败",
          message: "解绑失败！",
          type: "error",
        });
      }
    },
  },
  beforeCreate() {
    if (this.$store.state.token === null) {
      this.$router.replace("/");
    }
  },
  created() {
    this.getMyInfo();
  },
};
</script>

<style scoped>
.infos {
  font-size: 20px;
}
</style>