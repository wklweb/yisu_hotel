<template>
  <div class="room-list-container">
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>{{ hotelName }} - 房型管理</span>
          <el-button type="primary" link @click="addRoom">
             <img src="/fabu.png" style="width: 14px; height: 14px; margin-right: 5px; vertical-align: middle;" />
             添加房型
          </el-button>
        </div>
      </template>
      
      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column prop="name" label="房型名称" />
        <el-table-column prop="price" label="价格">
            <template #default="scope">
                ¥ {{ scope.row.price }}
            </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" />
        <el-table-column label="操作" width="150" align="center">
          <template #default="scope">
            <el-button link type="primary" icon="Edit" @click="editRoom(scope.row)">编辑</el-button>
            <el-button link type="danger" icon="Delete" @click="deleteRoom(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑房型' : '添加房型'" width="500px">
        <el-form :model="form" label-width="80px">
            <el-form-item label="房型名称" required>
                <el-input v-model="form.name" placeholder="例如：豪华大床房" />
            </el-form-item>
            <el-form-item label="价格" required>
                <el-input v-model="form.price" type="number" placeholder="请输入价格">
                    <template #prefix>¥</template>
                </el-input>
            </el-form-item>
            <el-form-item label="库存" required>
                <el-input v-model="form.stock" type="number" placeholder="请输入库存数量" />
            </el-form-item>
            <el-form-item label="房型图片">
                 <!-- Image upload for room can be added here if needed, keeping it simple for now as per previous req -->
                 <el-input v-model="form.imageUrl" placeholder="图片链接（可选）" />
            </el-form-item>
            <el-form-item label="描述">
                <el-input v-model="form.description" type="textarea" />
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="save">确定</el-button>
        </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import request from '../../utils/request'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Edit, Delete } from '@element-plus/icons-vue'

const route = useRoute()
const hotelId = route.params.hotelId
const hotelName = ref('')
const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)

const form = reactive({
    id: undefined,
    hotelId: hotelId,
    name: '',
    price: '',
    stock: '',
    description: '',
    imageUrl: ''
})

const loadData = () => {
    loading.value = true
    // Get hotel name first
    request.get(`/hotel/detail/${hotelId}`).then((res: any) => {
        hotelName.value = res.hotel.name
        tableData.value = res.rooms
    }).finally(() => loading.value = false)
}

const addRoom = () => {
    isEdit.value = false
    form.id = undefined
    form.name = ''
    form.price = ''
    form.stock = ''
    form.description = ''
    form.imageUrl = ''
    dialogVisible.value = true
}

const editRoom = (row: any) => {
    isEdit.value = true
    Object.assign(form, row)
    dialogVisible.value = true
}

const save = () => {
    if(!form.name || !form.price || !form.stock) {
        ElMessage.warning('请填写完整信息')
        return
    }
    request.post('/room/save', form).then(() => {
        ElMessage.success('保存成功')
        dialogVisible.value = false
        loadData()
    })
}

const deleteRoom = (id: number) => {
    request.delete(`/room/${id}`).then(() => {
        ElMessage.success('删除成功')
        loadData()
    })
}

onMounted(() => {
    if(hotelId) {
        loadData()
    }
})
</script>

<style scoped>
.room-list-container {
    padding: 0;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}
</style>
