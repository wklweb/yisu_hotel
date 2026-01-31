<template>
  <div class="layout">
    <el-container class="main-container">
      <el-aside width="220px" class="aside">
        <div class="brand">
          <el-icon class="brand-icon"><OfficeBuilding /></el-icon>
          <span class="brand-text">易宿管理后台</span>
        </div>
        <el-menu
          router
          :default-active="$route.path"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
          class="el-menu-vertical"
        >
          <el-menu-item index="/dashboard">
            <el-icon><House /></el-icon>
            <span>酒店列表</span>
          </el-menu-item>
          <el-menu-item index="/dashboard/hotel-add" v-if="user.role === 'MERCHANT'">
            <el-icon><Plus /></el-icon>
            <span>发布酒店</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <el-container>
        <el-header class="header">
          <div class="header-left">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item>{{ $route.meta.title || '当前页面' }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="user-dropdown">
                <el-avatar :size="30" icon="UserFilled" style="margin-right: 8px; background: #409EFF" />
                {{ user.username }}
                <el-icon class="el-icon--right"><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        
        <el-main class="main-content">
          <router-view v-slot="{ Component }">
             <transition name="fade-transform" mode="out-in">
               <component :is="Component" />
             </transition>
          </router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { House, Plus, OfficeBuilding, ArrowDown, UserFilled } from '@element-plus/icons-vue'

const router = useRouter()
const userStr = localStorage.getItem('user')
const user = userStr ? JSON.parse(userStr) : { username: 'Guest', role: 'MERCHANT' }

const handleCommand = (command: string) => {
  if (command === 'logout') {
    localStorage.clear()
    router.push('/login')
  }
}
</script>

<style scoped>
.layout {
  height: 100vh;
  width: 100%;
}

.main-container {
  height: 100%;
}

.aside {
  background-color: #304156;
  color: white;
  display: flex;
  flex-direction: column;
}

.brand {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b2f3a;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
}

.brand-icon {
  margin-right: 8px;
  font-size: 22px;
}

.el-menu-vertical {
  border-right: none;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
}

.user-dropdown {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #606266;
}

.main-content {
  background-color: #f0f2f5;
  padding: 20px;
}

/* Transitions */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>
