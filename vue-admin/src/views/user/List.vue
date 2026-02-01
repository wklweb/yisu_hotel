<template>
  <div class="user-list-container">
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="query" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="query.username" placeholder="输入用户名搜索" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" round @click="loadData">
            <img src="/chaxun.png" style="width: 14px; height: 14px; margin-right: 5px; vertical-align: middle;" />
            查询
          </el-button>
          <el-button round @click="resetQuery">
            <img src="/chongzhi.png" style="width: 14px; height: 14px; margin-right: 5px; vertical-align: middle;" />
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never" style="margin-top: 16px">
      <template #header>
        <div class="card-header">
          <span>酒店账号管理</span>
        </div>
      </template>
      
      <el-table 
        :data="tableData" 
        v-loading="loading" 
        border 
        stripe 
        style="width: 100%"
        row-key="id"
        @expand-change="handleExpand"
      >
        <el-table-column type="expand">
          <template #default="scope">
            <div class="expand-content" v-if="scope.row.hotels">
              <div class="hotel-summary">
                <el-tag type="info">酒店总数：{{ scope.row.hotels.hotelCount || 0 }}</el-tag>
                <el-tag type="info" style="margin-left: 10px;">房间总数：{{ scope.row.hotels.totalRoomCount || 0 }}</el-tag>
              </div>
              <el-table :data="scope.row.hotels.hotels || []" border size="small" style="margin-top: 10px;">
                <el-table-column prop="name" label="酒店名称" width="200" />
                <el-table-column prop="city" label="城市" width="100" />
                <el-table-column prop="starRating" label="星级" width="80" />
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="hotelScope">
                    <el-tag :type="getStatusType(hotelScope.row.status)" size="small">
                      {{ getStatusText(hotelScope.row.status) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="roomCount" label="房间数" width="80" align="center" />
                <el-table-column prop="minPrice" label="最低价" width="100" align="center">
                  <template #default="hotelScope">
                    ¥ {{ hotelScope.row.minPrice || 0 }}
                  </template>
                </el-table-column>
                <el-table-column label="房间列表" min-width="300">
                  <template #default="hotelScope">
                    <div class="room-list">
                      <el-tag 
                        v-for="room in hotelScope.row.rooms" 
                        :key="room.id" 
                        size="small" 
                        style="margin-right: 5px; margin-bottom: 5px;"
                      >
                        {{ room.name }} - ¥{{ room.price }}
                      </el-tag>
                      <span v-if="!hotelScope.row.rooms || hotelScope.row.rooms.length === 0" style="color: #909399;">暂无房间</span>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <div v-else class="expand-loading">
              <el-icon class="is-loading"><Loading /></el-icon>
              <span style="margin-left: 10px;">加载中...</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="username" label="用户名" width="150" align="center" />
        <el-table-column label="头像" width="100" align="center">
          <template #default="scope">
            <el-avatar :size="40" :src="scope.row.avatar" icon="UserFilled" />
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" align="center" />
        <el-table-column prop="email" label="邮箱" width="200" align="center" />
        <el-table-column prop="role" label="角色" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.role === 'ADMIN' ? 'danger' : 'primary'">
              {{ scope.row.role === 'ADMIN' ? '管理员' : '商户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'danger' : 'success'">
              {{ scope.row.status === 1 ? '已禁用' : '正常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" align="center" />
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="scope">
            <el-button link type="primary" icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button 
              link 
              :type="scope.row.status === 1 ? 'success' : 'warning'" 
              :icon="scope.row.status === 1 ? 'Check' : 'Close'"
              @click="handleToggleStatus(scope.row)"
            >
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          background
          layout="total, prev, pager, next, jumper"
          :total="total"
          :page-size="query.pageSize"
          :current-page="query.pageNum"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑用户" width="500px">
      <el-form :model="editForm" :rules="rules" ref="editFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="editForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="editForm.phone" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editForm.email" placeholder="请输入邮箱" type="email" />
        </el-form-item>
        <el-form-item label="头像">
          <el-input v-model="editForm.avatar" placeholder="头像URL" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveEdit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Delete, UserFilled, Check, Close, Loading } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const expandedRows = ref<number[]>([])
const editDialogVisible = ref(false)
const editFormRef = ref<FormInstance>()
const editForm = reactive({
  id: null as number | null,
  username: '',
  phone: '',
  email: '',
  avatar: ''
})

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  username: ''
})

const rules = reactive<FormRules>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
})

const loadData = () => {
  loading.value = true
  request.get('/user/admin/merchant-list', { params: query }).then((res: any) => {
    tableData.value = res.records
    total.value = res.total
  }).catch((error: any) => {
    ElMessage.error('加载失败：' + (error.message || '未知错误'))
  }).finally(() => {
    loading.value = false
  })
}

const resetQuery = () => {
  query.username = ''
  query.pageNum = 1
  loadData()
}

const handleCurrentChange = (val: number) => {
  query.pageNum = val
  loadData()
}

const handleEdit = (row: any) => {
  editForm.id = row.id
  editForm.username = row.username
  editForm.phone = row.phone || ''
  editForm.email = row.email || ''
  editForm.avatar = row.avatar || ''
  editDialogVisible.value = true
}

const saveEdit = async () => {
  if (!editFormRef.value) return
  
  await editFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await request.put('/user/admin/update', editForm)
        ElMessage.success('更新成功')
        editDialogVisible.value = false
        loadData()
      } catch (error: any) {
        ElMessage.error('更新失败：' + (error.message || '未知错误'))
      }
    }
  })
}

const handleToggleStatus = (row: any) => {
  const action = row.status === 1 ? '启用' : '禁用'
  ElMessageBox.confirm(
    `确定要${action}用户 "${row.username}" 吗？`,
    `${action}确认`,
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    const newStatus = row.status === 1 ? 0 : 1
    request.put('/user/admin/status', {
      userId: row.id,
      status: newStatus
    }).then(() => {
      ElMessage.success(`${action}成功`)
      loadData()
    }).catch((error: any) => {
      ElMessage.error(`${action}失败：` + (error.message || '未知错误'))
    })
  }).catch(() => {
    // 用户取消
  })
}

const handleExpand = (row: any, expandedRows: any) => {
  if (expandedRows.length > 0 && !row.hotels) {
    // 展开时加载酒店和房间数据
    loadUserHotels(row)
  }
}

const loadUserHotels = (row: any) => {
  request.get(`/user/admin/${row.id}/hotels`).then((res: any) => {
    row.hotels = res
  }).catch((error: any) => {
    ElMessage.error('加载酒店信息失败：' + (error.message || '未知错误'))
  })
}

const getStatusType = (status: number) => {
  switch (status) {
    case 0: return 'warning'
    case 1: return 'success'
    case 2: return 'info'
    case 3: return 'danger'
    default: return ''
  }
}

const getStatusText = (status: number) => {
  switch (status) {
    case 0: return '审核中'
    case 1: return '已发布'
    case 2: return '已下线'
    case 3: return '未通过'
    default: return '未知'
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.user-list-container {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.search-card {
  margin-bottom: 16px;
}

.search-form {
  margin: 0;
}

.expand-content {
  padding: 10px;
}

.hotel-summary {
  margin-bottom: 10px;
}

.room-list {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

.expand-loading {
  padding: 20px;
  text-align: center;
  color: #909399;
}
</style>
