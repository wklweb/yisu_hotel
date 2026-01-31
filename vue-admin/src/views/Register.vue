<template>
  <div class="register-container">
    <div class="register-content">
      <div class="register-header">
        <img src="https://element-plus.org/images/element-plus-logo.svg" alt="logo" class="logo-img">
        <h1 class="project-title">易宿酒店预订管理平台</h1>
      </div>
      <el-card class="register-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span>注册新账号</span>
          </div>
        </template>
        <el-form :model="form" :rules="rules" ref="formRef" size="large" @keyup.enter="register">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" :prefix-icon="User" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" show-password />
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <el-input v-model="form.confirmPassword" type="password" placeholder="请确认密码" :prefix-icon="Lock" show-password />
          </el-form-item>
          <el-form-item prop="phone">
            <el-input v-model="form.phone" placeholder="请输入手机号" :prefix-icon="Iphone" />
          </el-form-item>
          <el-form-item prop="role">
             <el-radio-group v-model="form.role" style="width: 100%">
                <el-radio-button label="MERCHANT" style="width: 50%">我是商户</el-radio-button>
                <el-radio-button label="ADMIN" style="width: 50%">我是管理员</el-radio-button>
             </el-radio-group>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" round @click="register" style="width: 100%">立即注册</el-button>
          </el-form-item>
          <div class="form-footer">
            <router-link to="/login" class="link">已有账号？立即登录</router-link>
          </div>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import request from '../utils/request'
import { useRouter } from 'vue-router'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { User, Lock, Iphone } from '@element-plus/icons-vue'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  role: 'MERCHANT',
  phone: ''
})

const validatePass2 = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const rules = reactive<FormRules>({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPassword: [{ validator: validatePass2, trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
})

const register = async () => {
  if (!formRef.value) return
  await formRef.value.validate((valid, fields) => {
    if (valid) {
      loading.value = true
      // Exclude confirmPassword before sending
      const { confirmPassword, ...submitData } = form
      request.post('/user/register', submitData).then(() => {
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      }).finally(() => {
        loading.value = false
      })
    }
  })
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-image: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
  background-size: cover;
}

.register-content {
  text-align: center;
}

.register-header {
  margin-bottom: 20px;
}

.logo-img {
  height: 40px;
  vertical-align: middle;
  margin-right: 10px;
}

.project-title {
  display: inline-block;
  vertical-align: middle;
  font-size: 24px;
  color: #fff;
  margin: 0;
  font-weight: 600;
}

.register-card {
  width: 420px;
  border-radius: 8px;
  text-align: left;
}

.card-header {
  text-align: center;
  font-weight: bold;
  font-size: 18px;
}

.form-footer {
  text-align: center;
  margin-top: 10px;
}

.link {
  color: #409eff;
  text-decoration: none;
  font-size: 14px;
}

.link:hover {
  text-decoration: underline;
}

/* Customizing Radio Button to look better */
:deep(.el-radio-button__inner) {
    width: 100%;
}
</style>
