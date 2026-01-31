<template>
  <div class="hotel-detail-container">
    <el-page-header @back="$router.back()" :content="isEdit ? '编辑酒店信息' : '发布新酒店'" class="page-header" />
    
    <div class="content-wrapper">
       <el-row :gutter="20">
          <el-col :span="16">
             <el-card shadow="never" class="section-card">
                <template #header>
                   <div class="card-header">
                      <span>基础信息</span>
                   </div>
                </template>
                <el-form :model="form" label-width="100px" status-icon>
                    <el-row>
                        <el-col :span="12">
                            <el-form-item label="酒店名称" required>
                                <el-input v-model="form.name" placeholder="请输入酒店名称" />
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                             <el-form-item label="星级" required>
                                <el-select v-model="form.starRating" placeholder="请选择星级" style="width: 100%">
                                    <el-option label="1星" value="1星" />
                                    <el-option label="2星" value="2星" />
                                    <el-option label="3星" value="3星" />
                                    <el-option label="4星" value="4星" />
                                    <el-option label="5星" value="5星" />
                                </el-select>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col :span="12">
                            <el-form-item label="所在城市" required>
                                <el-input v-model="form.city" placeholder="例如：北京" />
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                             <el-form-item label="开业日期">
                                <el-date-picker v-model="form.openDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" placeholder="选择日期" />
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-form-item label="详细地址" required>
                        <el-input v-model="form.address" placeholder="请输入详细地址" />
                    </el-form-item>
                    <el-form-item label="酒店描述">
                        <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入酒店特色描述" />
                    </el-form-item>
                </el-form>
             </el-card>

             <el-card shadow="never" class="section-card" style="margin-top: 20px">
                <template #header>
                   <div class="card-header">
                      <span>设施服务</span>
                   </div>
                </template>
                 <el-checkbox-group v-model="facilities">
                    <el-checkbox label="Wifi" border />
                    <el-checkbox label="停车场" border />
                    <el-checkbox label="游泳池" border />
                    <el-checkbox label="健身房" border />
                    <el-checkbox label="餐厅" border />
                    <el-checkbox label="会议室" border />
                    <el-checkbox label="接送机" border />
                </el-checkbox-group>
             </el-card>
             
             <div class="footer-actions">
                <el-button type="primary" size="large" @click="save" style="width: 200px">保存并提交</el-button>
             </div>
          </el-col>
          
          <el-col :span="8">
             <!-- Room Management Section only visible when editing existing hotel -->
             <el-card shadow="never" class="section-card" v-if="isEdit">
                <template #header>
                   <div class="card-header">
                      <span>房型管理</span>
                      <el-button type="primary" link icon="Plus" @click="addRoom">添加</el-button>
                   </div>
                </template>
                <div v-if="rooms.length === 0" class="empty-text">暂无房型数据</div>
                <div v-else class="room-list">
                    <div v-for="room in rooms" :key="room.id" class="room-item">
                        <div class="room-info">
                            <div class="room-name">{{ room.name }}</div>
                            <div class="room-price">¥ {{ room.price }}</div>
                            <div class="room-stock">库存: {{ room.stock }}</div>
                        </div>
                        <div class="room-actions">
                            <el-button type="danger" circle icon="Delete" size="small" @click="deleteRoom(room.id)" />
                        </div>
                    </div>
                </div>
             </el-card>
             
             <el-card shadow="never" class="section-card" style="margin-top: 20px" v-else>
                 <el-result icon="info" title="提示" sub-title="请先保存酒店基础信息，再添加房型数据"></el-result>
             </el-card>
          </el-col>
       </el-row>
    </div>

    <el-dialog v-model="roomDialogVisible" title="添加房型" width="400px">
        <el-form :model="roomForm" label-width="80px">
            <el-form-item label="房型名称">
                <el-input v-model="roomForm.name" />
            </el-form-item>
            <el-form-item label="价格">
                <el-input v-model="roomForm.price" type="number">
                    <template #prefix>¥</template>
                </el-input>
            </el-form-item>
            <el-form-item label="库存">
                <el-input v-model="roomForm.stock" type="number" />
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="roomDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="saveRoom">确定</el-button>
        </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import request from '../../utils/request'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStr = localStorage.getItem('user')
const user = userStr ? JSON.parse(userStr) : {}

const isEdit = computed(() => !!route.params.id)
const facilities = ref<string[]>([])

const form = reactive({
  id: undefined,
  name: '',
  city: '',
  address: '',
  starRating: '',
  openDate: '',
  description: '',
  merchantId: user.id,
  facilities: '',
  status: 0
})

const rooms = ref([])
const roomDialogVisible = ref(false)
const roomForm = reactive({
    hotelId: undefined,
    name: '',
    price: '',
    stock: ''
})

onMounted(() => {
  if (isEdit.value) {
    loadHotel(route.params.id)
    loadRooms(route.params.id)
  }
})

const loadHotel = (id: any) => {
  request.get(`/hotel/detail/${id}`).then((res: any) => {
    Object.assign(form, res.hotel)
    if (res.hotel.facilities) {
        facilities.value = res.hotel.facilities.split(',')
    }
  })
}

const loadRooms = (id: any) => {
    request.get(`/room/list/${id}`).then((res: any) => {
        rooms.value = res
    })
}

const save = () => {
  form.facilities = facilities.value.join(',')
  request.post('/hotel/save', form).then((res: any) => {
    ElMessage.success('保存成功')
    if(!isEdit.value) {
       router.push('/dashboard')
    }
  })
}

const addRoom = () => {
    roomForm.hotelId = form.id
    roomDialogVisible.value = true
}

const saveRoom = () => {
    request.post('/room/save', roomForm).then(() => {
        ElMessage.success('添加成功')
        roomDialogVisible.value = false
        loadRooms(form.id)
    })
}

const deleteRoom = (id: number) => {
    request.delete(`/room/${id}`).then(() => {
        ElMessage.success('删除成功')
        loadRooms(form.id)
    })
}
</script>

<style scoped>
.hotel-detail-container {
    padding: 0;
}
.page-header {
    background: #fff;
    padding: 16px 24px;
    border-bottom: 1px solid #eee;
    margin: -20px -20px 20px -20px;
}
.section-card {
    margin-bottom: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}
.footer-actions {
    text-align: center;
    margin-top: 30px;
    padding-bottom: 30px;
}
.room-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;
}
.room-item:last-child {
    border-bottom: none;
}
.room-name {
    font-weight: 500;
    font-size: 16px;
}
.room-price {
    color: #f56c6c;
    font-weight: bold;
}
.room-stock {
    font-size: 12px;
    color: #909399;
}
.empty-text {
    text-align: center;
    color: #909399;
    padding: 20px;
}
</style>
