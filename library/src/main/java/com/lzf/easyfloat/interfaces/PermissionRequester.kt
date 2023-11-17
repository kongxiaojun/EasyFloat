package com.lzf.easyfloat.interfaces

/**
 * @author kongxiaojun
 * @date 2023/11/17
 * @description 权限申请器
 */
interface PermissionRequester {

    fun requestPermission(resultCallback: OnPermissionResult)

}