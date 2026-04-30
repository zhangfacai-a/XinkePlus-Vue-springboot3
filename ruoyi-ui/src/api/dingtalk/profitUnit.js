import request from '@/utils/request'

// ================= 查询列表 =================
export function listProfitUnit(query) {
    return request({
        url: '/biz/profitUnit/list',
        method: 'get',
        params: query
    })
}

// ================= 查询详情 =================
export function getProfitUnit(id) {
    return request({
        url: '/biz/profitUnit/' + id,
        method: 'get'
    })
}

// ================= 新增 =================
export function addProfitUnit(data) {
    return request({
        url: '/biz/profitUnit',
        method: 'post',
        data: data
    })
}

// ================= 修改 =================
export function updateProfitUnit(data) {
    return request({
        url: '/biz/profitUnit',
        method: 'put',
        data: data
    })
}

// ================= 删除 =================
export function delProfitUnit(id) {
    return request({
        url: '/biz/profitUnit/' + id,
        method: 'delete'
    })
}

// ================= 导出 =================
export function exportProfitUnit(query) {
    return request({
        url: '/biz/profitUnit/export',
        method: 'post',
        params: query
    })
}

// ================= Excel导入 =================
export function importProfitUnit(data) {
    return request({
        url: '/biz/import/PROFIT_UNIT',
        method: 'post',
        data: data,
        headers: {
            'Content-Type': 'multipart/form-data'
        },
        timeout: 300000
    })
}