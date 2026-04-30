<template>
    <div class="app-container">
        <!-- 查询条件 -->
        <el-form v-show="showSearch" ref="queryRef" :model="queryParams" :inline="true" label-width="90px">
            <el-form-item label="利润单元" prop="profitUnitName">
                <el-input v-model="queryParams.profitUnitName" placeholder="请输入利润单元名称" clearable
                    @keyup.enter="handleQuery" />
            </el-form-item>

            <el-form-item label="平台" prop="platform">
                <el-input v-model="queryParams.platform" placeholder="请输入平台" clearable @keyup.enter="handleQuery" />
            </el-form-item>

            <el-form-item label="品牌" prop="brandName">
                <el-input v-model="queryParams.brandName" placeholder="请输入品牌" clearable @keyup.enter="handleQuery" />
            </el-form-item>

            <el-form-item label="品类" prop="categoryName">
                <el-input v-model="queryParams.categoryName" placeholder="请输入品类" clearable @keyup.enter="handleQuery" />
            </el-form-item>

            <el-form-item label="部门" prop="deptName">
                <el-input v-model="queryParams.deptName" placeholder="请输入所属部门" clearable @keyup.enter="handleQuery" />
            </el-form-item>

            <el-form-item label="直接负责人" prop="ownerName">
                <el-input v-model="queryParams.ownerName" placeholder="请输入直接负责人" clearable @keyup.enter="handleQuery" />
            </el-form-item>

            <el-form-item label="成交类型" prop="dealType">
                <el-input v-model="queryParams.dealType" placeholder="请输入成交类型" clearable @keyup.enter="handleQuery" />
            </el-form-item>

            <el-form-item label="是否启动" prop="isEnabled">
                <el-input v-model="queryParams.isEnabled" placeholder="如：运营中/筹备中" clearable
                    @keyup.enter="handleQuery" />
            </el-form-item>

            <el-form-item label="是否授权" prop="isAuthorized">
                <el-select v-model="queryParams.isAuthorized" placeholder="请选择" clearable style="width: 160px">
                    <el-option label="是" value="是" />
                    <el-option label="否" value="否" />
                </el-select>
            </el-form-item>

            <el-form-item label="开始日期">
                <el-date-picker v-model="startDateRange" type="daterange" range-separator="-" start-placeholder="开始"
                    end-placeholder="结束" value-format="YYYY-MM-DD" style="width: 240px" />
            </el-form-item>

            <el-form-item label="结束日期">
                <el-date-picker v-model="transferDateRange" type="daterange" range-separator="-" start-placeholder="开始"
                    end-placeholder="结束" value-format="YYYY-MM-DD" style="width: 240px" />
            </el-form-item>

            <el-form-item>
                <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                <el-button icon="Refresh" @click="resetQuery">重置</el-button>
            </el-form-item>
        </el-form>

        <!-- 工具栏 -->
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button type="primary" plain icon="Plus" @click="handleAdd"
                    v-hasPermi="['biz:profitUnit:add']">新增</el-button>
            </el-col>

            <el-col :span="1.5">
                <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate"
                    v-hasPermi="['biz:profitUnit:edit']">修改</el-button>
            </el-col>

            <el-col :span="1.5">
                <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete"
                    v-hasPermi="['biz:profitUnit:remove']">删除</el-button>
            </el-col>

            <el-col :span="1.5">
                <el-button type="warning" plain icon="Upload" @click="handleImport">导入</el-button>
            </el-col>

            <el-col :span="1.5">
                <el-button type="warning" plain icon="Download" @click="handleExport"
                    v-hasPermi="['biz:profitUnit:export']">导出</el-button>
            </el-col>

            <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
        </el-row>

        <!-- 表格 -->
        <el-table v-loading="loading" :data="profitUnitList" border @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="50" align="center" />
            <el-table-column label="ID" prop="id" width="80" align="center" />
            <el-table-column label="利润单元名称" prop="profitUnitName" min-width="180" show-overflow-tooltip />
            <el-table-column label="平台" prop="platform" width="90" />
            <el-table-column label="品牌" prop="brandName" width="100" />
            <el-table-column label="品类" prop="categoryName" width="100" />
            <el-table-column label="所属部门" prop="deptName" width="120" />
            <el-table-column label="部门负责人" prop="deptOwnerName" width="110" />
            <el-table-column label="直接负责人" prop="ownerName" width="110" />
            <el-table-column label="店铺名称" prop="storeName" min-width="160" show-overflow-tooltip />
            <el-table-column label="账号ID" prop="accountId" min-width="120" show-overflow-tooltip />
            <el-table-column label="成交类型" prop="dealType" width="110" />
            <el-table-column label="直播间类型" prop="liveRoomType" width="130" show-overflow-tooltip />
            <el-table-column label="开始日期" prop="startDate" width="110" />
            <el-table-column label="结束日期" prop="transferDate" width="110" />
            <el-table-column label="停运日期" prop="stopDate" width="110" />
            <el-table-column label="是否启动" prop="isEnabled" width="100" />
            <el-table-column label="是否授权" prop="isAuthorized" width="90" />
            <el-table-column label="状态" prop="status" width="80" align="center">
                <template #default="scope">
                    <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
                </template>
            </el-table-column>
            <el-table-column label="更新时间" prop="updateTime" width="160" />

            <el-table-column label="操作" align="center" width="160" fixed="right">
                <template #default="scope">
                    <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                        v-hasPermi="['biz:profitUnit:edit']">修改</el-button>
                    <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
                        v-hasPermi="['biz:profitUnit:remove']">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
            v-model:limit="queryParams.pageSize" @pagination="getList" />

        <!-- 新增 / 修改弹窗 -->
        <el-dialog :title="title" v-model="open" width="1100px" append-to-body>
            <el-form ref="profitUnitRef" :model="form" :rules="rules" label-width="130px">
                <el-divider content-position="left">基础信息</el-divider>

                <el-row>
                    <el-col :span="8">
                        <el-form-item label="公司主体ID" prop="companySubjectId">
                            <el-input v-model="form.companySubjectId" placeholder="请输入公司主体ID" />
                        </el-form-item>
                    </el-col>

                    <el-col :span="8">
                        <el-form-item label="负责会计" prop="accountantName">
                            <el-input v-model="form.accountantName" placeholder="请输入负责会计" />
                        </el-form-item>
                    </el-col>

                    <el-col :span="8">
                        <el-form-item label="开始做账日期" prop="accountingStartDate">
                            <el-date-picker v-model="form.accountingStartDate" type="date" value-format="YYYY-MM-DD"
                                placeholder="请选择开始做账日期" style="width: 100%" />
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row>
                    <el-col :span="8">
                        <el-form-item label="利润单元名称" prop="profitUnitName">
                            <el-input v-model="form.profitUnitName" placeholder="请输入利润单元名称" />
                        </el-form-item>
                    </el-col>

                    <el-col :span="8">
                        <el-form-item label="利润单元编码" prop="profitUnitCode">
                            <el-input v-model="form.profitUnitCode" placeholder="请输入利润单元编码" />
                        </el-form-item>
                    </el-col>

                    <el-col :span="8">
                        <el-form-item label="平台" prop="platform">
                            <el-input v-model="form.platform" placeholder="请输入平台" />
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row>
                    <el-col :span="8">
                        <el-form-item label="品牌" prop="brandName">
                            <el-input v-model="form.brandName" placeholder="请输入品牌" />
                        </el-form-item>
                    </el-col>

                    <el-col :span="8">
                        <el-form-item label="品类" prop="categoryName">
                            <el-input v-model="form.categoryName" placeholder="请输入品类" />
                        </el-form-item>
                    </el-col>

                    <el-col :span="8">
                        <el-form-item label="所属部门" prop="deptName">
                            <el-input v-model="form.deptName" placeholder="请输入所属部门" />
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-divider content-position="left">负责人信息</el-divider>

                <el-row>
                    <el-col :span="8">
                        <el-form-item label="部门负责人工号" prop="deptOwnerJobNo">
                            <el-input v-model="form.deptOwnerJobNo" placeholder="请输入部门负责人工号" />
                        </el-form-item>
                    </el-col>

                    <el-col :span="8">
                        <el-form-item label="部门负责人" prop="deptOwnerName">
                            <el-input v-model="form.deptOwnerName" placeholder="请输入部门负责人" />
                        </el-form-item>
                    </el-col>

                    <el-col :span="8">
                        <el-form-item label="地区" prop="regionName">
                            <el-input v-model="form.regionName" placeholder="请输入地区" />
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row>
                    <el-col :span="8">
                        <el-form-item label="项目组" prop="projectGroup">
                            <el-input v-model="form.projectGroup" placeholder="请输入项目组" />
                        </el-form-item>
                    </el-col>

                    <el-col :span="8">
                        <el-form-item label="项目负责人" prop="projectOwnerName">
                            <el-input v-model="form.projectOwnerName" placeholder="请输入项目负责人" />
                        </el-form-item>
                    </el-col>

                    <el-col :span="8">
                        <el-form-item label="直接负责人工号" prop="ownerJobNo">
                            <el-input v-model="form.ownerJobNo" placeholder="请输入直接负责人工号" />
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row>
                    <el-col :span="8">
                        <el-form-item label="直接负责人" prop="ownerName">
                            <el-input v-model="form.ownerName" placeholder="请输入直接负责人" />
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-divider content-position="left">店铺与账号</el-divider>

                <el-row>
                    <el-col :span="8">
                        <el-form-item label="店铺授权ID" prop="storeAuthId">
                            <el-input v-model="form.storeAuthId" placeholder="请输入店铺授权ID" />
                        </el-form-item>
                    </el-col>

                    <el-col :span="8">
                        <el-form-item label="店铺编码" prop="storeCode">
                            <el-input v-model="form.storeCode" placeholder="请输入店铺编码" />
                        </el-form-item>
                    </el-col>

                    <el-col :span="8">
                        <el-form-item label="店铺名称" prop="storeName">
                            <el-input v-model="form.storeName" placeholder="请输入店铺名称" />
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row>
                    <el-col :span="8">
                        <el-form-item label="账号ID" prop="accountId">
                            <el-input v-model="form.accountId" placeholder="请输入账号ID" />
                        </el-form-item>
                    </el-col>

                    <el-col :span="8">
                        <el-form-item label="成交类型" prop="dealType">
                            <el-input v-model="form.dealType" placeholder="请输入成交类型" />
                        </el-form-item>
                    </el-col>

                    <el-col :span="8">
                        <el-form-item label="直播间类型" prop="liveRoomType">
                            <el-input v-model="form.liveRoomType" placeholder="请输入直播间类型" />
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-divider content-position="left">注册与财务</el-divider>

                <el-row>
                    <el-col :span="8">
                        <el-form-item label="核算依据" prop="accountingBasis">
                            <el-input v-model="form.accountingBasis" placeholder="请输入核算依据" />
                        </el-form-item>
                    </el-col>

                    <el-col :span="8">
                        <el-form-item label="注册手机号" prop="registerMobile">
                            <el-input v-model="form.registerMobile" placeholder="请输入注册手机号" />
                        </el-form-item>
                    </el-col>

                    <el-col :span="8">
                        <el-form-item label="注册实名人员" prop="registerRealName">
                            <el-input v-model="form.registerRealName" placeholder="请输入注册实名人员" />
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row>
                    <el-col :span="8">
                        <el-form-item label="实名注册介绍人" prop="registerReferrer">
                            <el-input v-model="form.registerReferrer" placeholder="请输入实名注册介绍人" />
                        </el-form-item>
                    </el-col>

                    <el-col :span="8">
                        <el-form-item label="注册日期" prop="registerDate">
                            <el-date-picker v-model="form.registerDate" type="date" value-format="YYYY-MM-DD"
                                placeholder="请选择注册日期" style="width: 100%" />
                        </el-form-item>
                    </el-col>

                    <el-col :span="8">
                        <el-form-item label="保证金金额" prop="depositAmount">
                            <el-input-number v-model="form.depositAmount" :precision="2" :min="0" style="width: 100%"
                                placeholder="请输入保证金金额" />
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-divider content-position="left">千川信息</el-divider>

                <el-row>
                    <el-col :span="8">
                        <el-form-item label="千川账户ID" prop="qianchuanAccountId">
                            <el-input v-model="form.qianchuanAccountId" placeholder="请输入千川账户ID" />
                        </el-form-item>
                    </el-col>

                    <el-col :span="8">
                        <el-form-item label="千川账户名称" prop="qianchuanAccountName">
                            <el-input v-model="form.qianchuanAccountName" placeholder="请输入千川账户名称" />
                        </el-form-item>
                    </el-col>

                    <el-col :span="8">
                        <el-form-item label="千川手机号" prop="qianchuanMobile">
                            <el-input v-model="form.qianchuanMobile" placeholder="请输入千川手机号" />
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row>
                    <el-col :span="8">
                        <el-form-item label="千川公司主体" prop="qianchuanCompanySubject">
                            <el-input v-model="form.qianchuanCompanySubject" placeholder="请输入千川公司主体" />
                        </el-form-item>
                    </el-col>

                    <el-col :span="8">
                        <el-form-item label="千川授权" prop="qianchuanAuth">
                            <el-input v-model="form.qianchuanAuth" placeholder="请输入千川授权" />
                        </el-form-item>
                    </el-col>

                    <el-col :span="8">
                        <el-form-item label="验数记录" prop="verifyRecord">
                            <el-input v-model="form.verifyRecord" placeholder="请输入验数记录" />
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-divider content-position="left">状态与日期</el-divider>

                <el-row>
                    <el-col :span="8">
                        <el-form-item label="UID" prop="uid">
                            <el-input v-model="form.uid" placeholder="请输入UID" />
                        </el-form-item>
                    </el-col>

                    <el-col :span="8">
                        <el-form-item label="是否启动" prop="isEnabled">
                            <el-input v-model="form.isEnabled" placeholder="如：运营中/筹备中" />
                        </el-form-item>
                    </el-col>

                    <el-col :span="8">
                        <el-form-item label="是否授权" prop="isAuthorized">
                            <el-select v-model="form.isAuthorized" placeholder="请选择" clearable style="width: 100%">
                                <el-option label="是" value="是" />
                                <el-option label="否" value="否" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row>
                    <el-col :span="8">
                        <el-form-item label="状态" prop="status">
                            <el-radio-group v-model="form.status">
                                <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :label="dict.value">{{
                                    dict.label
                                }}</el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>

                    <el-col :span="8">
                        <el-form-item label="开始日期" prop="startDate">
                            <el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD"
                                placeholder="请选择开始日期" style="width: 100%" />
                        </el-form-item>
                    </el-col>

                    <el-col :span="8">
                        <el-form-item label="结束日期" prop="transferDate">
                            <el-date-picker v-model="form.transferDate" type="date" value-format="YYYY-MM-DD"
                                placeholder="请选择结束日期" style="width: 100%" />
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row>
                    <el-col :span="8">
                        <el-form-item label="停运日期" prop="stopDate">
                            <el-date-picker v-model="form.stopDate" type="date" value-format="YYYY-MM-DD"
                                placeholder="请选择停运日期" style="width: 100%" />
                        </el-form-item>
                    </el-col>

                    <el-col :span="16">
                        <el-form-item label="备注" prop="remark">
                            <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" maxlength="255"
                                show-word-limit />
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>

            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="submitForm">确 定</el-button>
                    <el-button @click="cancel">取 消</el-button>
                </div>
            </template>
        </el-dialog>

        <!-- 导入弹窗 -->
        <el-dialog title="导入利润单元" v-model="upload.open" width="420px" append-to-body>
            <el-upload ref="uploadRef" :limit="1" accept=".xlsx,.xls" :headers="upload.headers" :action="upload.url"
                :disabled="upload.isUploading" :on-progress="handleFileUploadProgress" :on-success="handleFileSuccess"
                :on-error="handleFileError" :auto-upload="false" drag>
                <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                <div class="el-upload__text">将 Excel 文件拖到此处，或 <em>点击上传</em></div>
                <template #tip>
                    <div class="el-upload__tip text-center">
                        仅允许导入 xls、xlsx 文件。导入规则：相同
                        <b>直接负责人 + 开始日期 + 结束日期 + 利润单元名称 + 成交类型</b>
                        时更新，否则新增。
                    </div>
                </template>
            </el-upload>

            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="submitFileForm">确 定</el-button>
                    <el-button @click="upload.open = false">取 消</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup name="ProfitUnit">
import { getToken } from '@/utils/auth'
import {
    listProfitUnit,
    getProfitUnit,
    addProfitUnit,
    updateProfitUnit,
    delProfitUnit
} from '@/api/dingtalk/profitUnit'

const { proxy } = getCurrentInstance()
const { sys_normal_disable } = proxy.useDict('sys_normal_disable')

const loading = ref(false)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref('')
const open = ref(false)

const profitUnitList = ref([])
const startDateRange = ref([])
const transferDateRange = ref([])

const data = reactive({
    queryParams: {
        pageNum: 1,
        pageSize: 10,
        profitUnitName: null,
        platform: null,
        brandName: null,
        categoryName: null,
        deptName: null,
        deptOwnerName: null,
        ownerName: null,
        storeName: null,
        accountId: null,
        dealType: null,
        isEnabled: null,
        isAuthorized: null,
        startDateBegin: null,
        startDateEnd: null,
        transferDateBegin: null,
        transferDateEnd: null
    },
    form: {},
    rules: {
        profitUnitName: [
            { required: true, message: '利润单元名称不能为空', trigger: 'blur' }
        ],
        ownerName: [
            { required: true, message: '直接负责人不能为空', trigger: 'blur' }
        ],
        startDate: [
            { required: true, message: '开始日期不能为空', trigger: 'change' }
        ],
        transferDate: [
            { required: true, message: '结束日期不能为空', trigger: 'change' }
        ],
        dealType: [
            { required: true, message: '成交类型不能为空', trigger: 'blur' }
        ]
    }
})

const { queryParams, form, rules } = toRefs(data)

const upload = reactive({
    open: false,
    isUploading: false,
    headers: { Authorization: 'Bearer ' + getToken() },
    url: import.meta.env.VITE_APP_BASE_API + '/biz/import/PROFIT_UNIT'
})

function getList() {
    loading.value = true

    queryParams.value.startDateBegin = startDateRange.value?.[0] || null
    queryParams.value.startDateEnd = startDateRange.value?.[1] || null
    queryParams.value.transferDateBegin = transferDateRange.value?.[0] || null
    queryParams.value.transferDateEnd = transferDateRange.value?.[1] || null

    listProfitUnit(queryParams.value).then(response => {
        profitUnitList.value = response.rows || []
        total.value = response.total || 0
        loading.value = false
    }).catch(() => {
        loading.value = false
    })
}

function reset() {
    form.value = {
        id: null,
        companySubjectId: null,
        accountantName: null,
        accountingStartDate: null,
        profitUnitName: null,
        profitUnitCode: null,
        platform: null,
        brandName: null,
        categoryName: null,
        deptName: null,
        deptOwnerJobNo: null,
        deptOwnerName: null,
        regionName: null,
        projectGroup: null,
        projectOwnerName: null,
        ownerJobNo: null,
        ownerName: null,
        storeAuthId: null,
        storeCode: null,
        storeName: null,
        accountId: null,
        dealType: null,
        liveRoomType: null,
        accountingBasis: null,
        registerMobile: null,
        registerRealName: null,
        registerReferrer: null,
        registerDate: null,
        depositAmount: null,
        isEnabled: null,
        isAuthorized: null,
        qianchuanAccountId: null,
        qianchuanAccountName: null,
        qianchuanMobile: null,
        qianchuanCompanySubject: null,
        qianchuanAuth: null,
        verifyRecord: null,
        uid: null,
        status: '0',
        remark: null,
        startDate: null,
        transferDate: null,
        stopDate: null
    }
    proxy.resetForm('profitUnitRef')
}

function handleQuery() {
    queryParams.value.pageNum = 1
    getList()
}

function resetQuery() {
    startDateRange.value = []
    transferDateRange.value = []
    proxy.resetForm('queryRef')
    handleQuery()
}

function handleSelectionChange(selection) {
    ids.value = selection.map(item => item.id)
    single.value = selection.length !== 1
    multiple.value = !selection.length
}

function handleAdd() {
    reset()
    open.value = true
    title.value = '新增利润单元'
}

function handleUpdate(row) {
    reset()
    const id = row.id || ids.value[0]
    getProfitUnit(id).then(response => {
        form.value = response.data
        open.value = true
        title.value = '修改利润单元'
    })
}

function submitForm() {
    proxy.$refs.profitUnitRef.validate(valid => {
        if (!valid) return

        if (form.value.id != null) {
            updateProfitUnit(form.value).then(() => {
                proxy.$modal.msgSuccess('修改成功')
                open.value = false
                getList()
            })
        } else {
            addProfitUnit(form.value).then(() => {
                proxy.$modal.msgSuccess('新增成功')
                open.value = false
                getList()
            })
        }
    })
}

function handleDelete(row) {
    const deleteIds = row.id || ids.value
    proxy.$modal.confirm('是否确认删除利润单元编号为 "' + deleteIds + '" 的数据？').then(() => {
        return delProfitUnit(deleteIds)
    }).then(() => {
        getList()
        proxy.$modal.msgSuccess('删除成功')
    }).catch(() => { })
}

function handleExport() {
    proxy.download(
        '/biz/profitUnit/export',
        {
            ...queryParams.value
        },
        `profit_unit_${new Date().getTime()}.xlsx`
    )
}

function cancel() {
    open.value = false
    reset()
}

function handleImport() {
    upload.open = true
}

function handleFileUploadProgress() {
    upload.isUploading = true
}

function handleFileSuccess(response) {
    upload.open = false
    upload.isUploading = false
    proxy.$refs.uploadRef.handleRemove()
    proxy.$alert(
        response.msg || '导入完成',
        '导入结果',
        { dangerouslyUseHTMLString: true }
    )
    getList()
}

function handleFileError() {
    upload.isUploading = false
    proxy.$modal.msgError('导入失败，请检查后端日志或稍后重试')
}

function submitFileForm() {
    proxy.$refs.uploadRef.submit()
}

getList()
</script>