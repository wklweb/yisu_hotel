<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span class="title">个人中心</span>
        </div>
      </template>

      <el-tabs v-model="activeTab" class="profile-tabs">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="info">
          <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" class="profile-form">
            <el-form-item label="用户名">
              <el-input v-model="form.username" disabled></el-input>
            </el-form-item>
            
            <el-form-item label="角色">
              <el-tag :type="form.role === 'ADMIN' ? 'danger' : 'primary'">
                {{ form.role === 'ADMIN' ? '管理员' : '商户' }}
              </el-tag>
            </el-form-item>

            <el-form-item label="头像" prop="avatar">
              <div class="avatar-upload">
                <el-upload
                  class="avatar-uploader"
                  action="http://localhost:9090/file/upload"
                  :show-file-list="false"
                  :on-success="handleAvatarSuccess"
                  :on-error="handleAvatarError"
                  :before-upload="beforeAvatarUpload"
                  :headers="uploadHeaders"
                  accept="image/jpeg,image/png"
                >
                  <img v-if="form.avatar" :src="form.avatar" class="avatar" />
                  <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                </el-upload>
                <div class="avatar-tip">支持 JPG、PNG 格式，大小不超过 2MB</div>
              </div>
            </el-form-item>

            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11"></el-input>
            </el-form-item>

            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" type="email"></el-input>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="saveProfile" :loading="saving" round>
                保存修改
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 修改密码 -->
        <el-tab-pane label="修改密码" name="password">
          <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px" class="profile-form">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input 
                v-model="passwordForm.oldPassword" 
                type="password" 
                placeholder="请输入原密码"
                show-password
              ></el-input>
            </el-form-item>

            <el-form-item label="新密码" prop="newPassword">
              <el-input 
                v-model="passwordForm.newPassword" 
                type="password" 
                placeholder="请输入新密码（至少6位）"
                show-password
              ></el-input>
            </el-form-item>

            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input 
                v-model="passwordForm.confirmPassword" 
                type="password" 
                placeholder="请再次输入新密码"
                show-password
              ></el-input>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="savePassword" :loading="savingPassword" round>
                修改密码
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '../utils/request'
import type { FormInstance, FormRules } from 'element-plus'

const activeTab = ref('info')
const formRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()
const saving = ref(false)
const savingPassword = ref(false)

const form = reactive({
  id: null as number | null,
  username: '',
  role: '',
  phone: '',
  email: '',
  avatar: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 上传请求头（包含 token）
const uploadHeaders = ref({
  Authorization: `Bearer ${localStorage.getItem('token') || ''}`
})

const validateConfirmPassword = (rule: any, value: any, callback: any) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = reactive<FormRules>({
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
})

const passwordRules = reactive<FormRules>({
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
})

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const userInfo = await request.get('/user/info')
    Object.assign(form, userInfo)
  } catch (error: any) {
    ElMessage.error('加载用户信息失败：' + (error.message || '未知错误'))
  }
}

// 保存基本信息
const saveProfile = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      saving.value = true
      try {
        await request.put('/user/profile', {
          phone: form.phone,
          email: form.email,
          avatar: form.avatar
        })
        ElMessage.success('保存成功')
        // 更新localStorage中的用户信息
        const userStr = localStorage.getItem('user')
        if (userStr) {
          const user = JSON.parse(userStr)
          user.phone = form.phone
          user.email = form.email
          user.avatar = form.avatar
          localStorage.setItem('user', JSON.stringify(user))
          // 触发自定义事件，通知 Layout 组件更新用户信息
          window.dispatchEvent(new CustomEvent('user-updated'))
        }
      } catch (error: any) {
        ElMessage.error('保存失败：' + (error.message || '未知错误'))
      } finally {
        saving.value = false
      }
    }
  })
}

// 保存密码
const savePassword = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      savingPassword.value = true
      try {
        await request.put('/user/password', {
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        })
        ElMessage.success('密码修改成功，请重新登录')
        // 清空表单
        passwordForm.oldPassword = ''
        passwordForm.newPassword = ''
        passwordForm.confirmPassword = ''
        passwordFormRef.value.resetFields()
        // 延迟跳转到登录页
        setTimeout(() => {
          localStorage.clear()
          window.location.href = '/login'
        }, 1500)
      } catch (error: any) {
        ElMessage.error('密码修改失败：' + (error.message || '未知错误'))
      } finally {
        savingPassword.value = false
      }
    }
  })
}

// 头像上传成功
const handleAvatarSuccess = (response: any) => {
  // el-upload 使用原生 XMLHttpRequest，返回的是完整的 Result 对象
  if (response && response.code === '200') {
    form.avatar = response.data
    ElMessage.success('头像上传成功')
    // 立即更新 localStorage 和触发事件，让右上角头像立即更新
    const userStr = localStorage.getItem('user')
    if (userStr) {
      const user = JSON.parse(userStr)
      user.avatar = response.data
      localStorage.setItem('user', JSON.stringify(user))
      window.dispatchEvent(new CustomEvent('user-updated'))
    }
  } else {
    ElMessage.error('头像上传失败：' + (response?.msg || '未知错误'))
  }
}

// 头像上传失败
const handleAvatarError = (error: any) => {
  console.error('头像上传错误:', error)
  ElMessage.error('头像上传失败，请检查网络连接或稍后重试')
}

// 头像上传前验证
const beforeAvatarUpload = (file: File) => {
  console.log('准备上传文件:', file.name, file.type, file.size)
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('头像只能是 JPG/PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!')
    return false
  }
  // 更新 headers 中的 token（防止 token 过期）
  uploadHeaders.value.Authorization = `Bearer ${localStorage.getItem('token') || ''}`
  return true
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.profile-card {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.profile-tabs {
  margin-top: 20px;
}

.profile-form {
  max-width: 600px;
  margin: 20px auto;
}

.avatar-upload {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-uploader :deep(.el-upload) {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-uploader:hover {
  border-color: #409EFF;
}

.avatar {
  width: 120px;
  height: 120px;
  object-fit: cover;
  display: block;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}

.avatar-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}
</style>
