<template>
  <div class="hotel-list-container">
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="query" class="search-form">
        <el-form-item label="酒店名称">
          <el-input v-model="query.name" placeholder="输入酒店名称搜索" clearable />
        </el-form-item>
        <el-form-item label="审核状态" v-if="user.role === 'ADMIN'">
          <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 150px">
             <el-option label="审核中" :value="0" />
             <el-option label="已发布" :value="1" />
             <el-option label="已下线" :value="2" />
             <el-option label="审核不通过" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="loadData">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never" style="margin-top: 16px">
      <template #header>
        <div class="card-header">
          <span>酒店列表</span>
          <el-button v-if="user.role === 'MERCHANT'" type="primary" icon="Plus" @click="$router.push('/dashboard/hotel-add')">发布酒店</el-button>
        </div>
      </template>
      
      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column label="酒店信息" min-width="200">
           <template #default="scope">
             <div style="font-weight: bold">{{ scope.row.name }}</div>
             <div style="color: #909399; font-size: 12px">{{ scope.row.city }}</div>
           </template>
        </el-table-column>
        <el-table-column prop="starRating" label="星级" width="120" align="center">
           <template #default="scope">
             <el-rate v-model="starRatings[scope.$index]" disabled text-color="#ff9900" />
           </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" effect="dark">
               {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="scope">
            <div class="action-buttons">
              <el-button v-if="user.role === 'MERCHANT'" link type="primary" icon="Edit" @click="editHotel(scope.row)">编辑</el-button>
              
              <template v-if="user.role === 'ADMIN' && scope.row.status === 0">
                 <el-button link type="success" icon="Check" @click="auditHotel(scope.row, 1)">通过</el-button>
                 <el-button link type="danger" icon="Close" @click="openRejectDialog(scope.row)">拒绝</el-button>
              </template>
              
              <el-button v-if="user.role === 'MERCHANT' && scope.row.status === 1" link type="warning" icon="VideoPause" @click="changeStatus(scope.row, 2)">下线</el-button>
              <el-button v-if="user.role === 'MERCHANT' && scope.row.status === 2" link type="success" icon="VideoPlay" @click="changeStatus(scope.row, 1)">上线</el-button>
            </div>
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

    <el-dialog v-model="rejectDialogVisible" title="审核拒绝" width="400px">
      <el-form>
         <el-form-item label="拒绝原因">
            <el-input v-model="rejectReason" type="textarea" :rows="3" placeholder="请输入拒绝原因" />
         </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="rejectDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmReject">确定拒绝</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import request from '../../utils/request'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Plus, Edit, Check, Close, VideoPause, VideoPlay } from '@element-plus/icons-vue'

const router = useRouter()
const userStr = localStorage.getItem('user')
const user = userStr ? JSON.parse(userStr) : {}
const loading = ref(false)

const tableData = ref([])
const total = ref(0)
const query = reactive({
  pageNum: 1,
  pageSize: 10,
  name: '',
  status: undefined,
  merchantId: user.id
})

// Helper for rate component to display star rating number as stars
const starRatings = computed(() => {
    return tableData.value.map((item: any) => {
        const match = item.starRating ? item.starRating.match(/\d+/) : null
        return match ? parseInt(match[0]) : 0
    })
})

const rejectDialogVisible = ref(false)
const rejectReason = ref('')
const currentRejectId = ref(0)

const loadData = () => {
  loading.value = true
  let url = '/hotel/list'
  if (user.role === 'MERCHANT') {
    url = '/hotel/my-list'
  } else if (user.role === 'ADMIN') {
    url = '/hotel/admin/list'
  }
  
  request.get(url, { params: query }).then((res: any) => {
    tableData.value = res.records
    total.value = res.total
  }).finally(() => loading.value = false)
}

const resetQuery = () => {
    query.name = ''
    query.status = undefined
    query.pageNum = 1
    loadData()
}

const handleCurrentChange = (val: number) => {
  query.pageNum = val
  loadData()
}

const editHotel = (row: any) => {
  router.push(`/dashboard/hotel-edit/${row.id}`)
}

const auditHotel = (row: any, status: number) => {
  request.post('/hotel/audit', { id: row.id, status }).then(() => {
    ElMessage.success('操作成功')
    loadData()
  })
}

const changeStatus = (row: any, status: number) => {
    const newHotel = { ...row, status }
    request.post('/hotel/save', newHotel).then(() => {
        ElMessage.success('操作成功')
        loadData()
    })
}

const openRejectDialog = (row: any) => {
  currentRejectId.value = row.id
  rejectReason.value = ''
  rejectDialogVisible.value = true
}

const confirmReject = () => {
  request.post('/hotel/audit', { id: currentRejectId.value, status: 3, rejectReason: rejectReason.value }).then(() => {
    ElMessage.success('已拒绝')
    rejectDialogVisible.value = false
    loadData()
  })
}

const getStatusType = (status: number) => {
  switch (status) {
    case 0: return 'warning';
    case 1: return 'success';
    case 2: return 'info';
    case 3: return 'danger';
    default: return '';
  }
}

const getStatusText = (status: number) => {
  switch (status) {
    case 0: return '审核中';
    case 1: return '已发布';
    case 2: return '已下线';
    case 3: return '未通过';
    default: return '未知';
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.hotel-list-container {
    padding: 0;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
}
.action-buttons .el-button {
    margin-left: 5px;
}
</style>
