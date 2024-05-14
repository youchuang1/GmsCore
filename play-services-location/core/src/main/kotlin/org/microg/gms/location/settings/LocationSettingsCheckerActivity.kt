/*
 * SPDX-FileCopyrightText: 2023 microG Project Team
 * SPDX-License-Identifier: Apache-2.0
 */

package org.microg.gms.location.settings

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.gms.common.internal.safeparcel.SafeParcelableSerializer
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import org.microg.gms.location.core.R
import org.microg.gms.location.manager.AskPermissionActivity
import org.microg.gms.location.manager.EXTRA_PERMISSIONS
import org.microg.gms.location.manager.granularityFromPermission
import org.microg.gms.ui.buildAlertDialog

// 定义一个用于检查位置设置的动作字符串，通常用于Intent中指定这个动作来启动相应的处理逻辑
const val ACTION_LOCATION_SETTINGS_CHECKER = "com.google.android.gms.location.settings.CHECK_SETTINGS"

// Intent中用来传递原始包名的额外数据字段，标识发起请求的应用的包名
const val EXTRA_ORIGINAL_PACKAGE_NAME = "originalPackageName"

// Intent中用来传递位置设置请求的额外数据字段，通常包含一些请求设置的具体内容
const val EXTRA_SETTINGS_REQUEST = "locationSettingsRequests"

// Intent中用来传递位置请求的额外数据字段，这可能包含一组位置请求详情，如更新频率等
const val EXTRA_REQUESTS = "locationRequests"

// Intent中用来传递位置设置状态的额外数据字段，这通常包含当前位置设置的状态信息，如是否开启GPS等
const val EXTRA_SETTINGS_STATES = "com.google.android.gms.location.LOCATION_SETTINGS_STATES"

// 用于标识位置相关的请求代码，常用于startActivityForResult的请求识别
private const val REQUEST_CODE_LOCATION = 120

// 用于标识权限请求的代码，常用于startActivityForResult时区分不同类型的权限请求
private const val REQUEST_CODE_PERMISSION = 121

// 用于日志记录的标签，常用于打印与位置设置相关的调试信息
private const val TAG = "LocationSettings"


class LocationSettingsCheckerActivity : Activity(), DialogInterface.OnCancelListener, DialogInterface.OnClickListener {
    // 定义一些布尔值和列表，用于存储从Intent中解析的设置请求信息
    private var alwaysShow = false  // 是否始终显示位置设置对话框
    private var needBle = false  // 是否需要蓝牙低功耗（BLE）支持
    private var improvements = emptyList<Improvement>()  // 改进列表
    private var requests: List<LocationRequest>? = null  // 位置请求列表

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "LocationSettingsCheckerActivity onCreate")
        // 从Intent中提取位置设置请求
        if (intent.hasExtra(EXTRA_SETTINGS_REQUEST)) {
            try {
                // 反序列化位置设置请求
                val request = SafeParcelableSerializer.deserializeFromBytes(intent.getByteArrayExtra(EXTRA_SETTINGS_REQUEST), LocationSettingsRequest.CREATOR)
                alwaysShow = request.alwaysShow
                needBle = request.needBle
                requests = request.requests
            } catch (e: Exception) {
                Log.w(TAG, e)
            }
        }
        // 如果上述解析失败，尝试从另一个Extra获取位置请求列表
        if (requests == null && intent.hasExtra(EXTRA_REQUESTS)) {
            try {
                val arrayList = intent.getSerializableExtra(EXTRA_REQUESTS) as? ArrayList<*>
                requests = arrayList?.map {
                    SafeParcelableSerializer.deserializeFromBytes(it as ByteArray, LocationRequest.CREATOR)
                }
            } catch (e: Exception) {
                Log.w(TAG, e)
            }
        }
        // 处理请求：如果没有请求则直接结束Activity，如果有则根据情况处理
        if (requests == null) {
            finishResult(RESULT_CANCELED)
        } else {
            updateImprovements()  // 更新需要改进的位置设置
            if (improvements.isEmpty()) {
                finishResult(RESULT_OK)  // 如果没有需要改进的设置，返回成功结果
            } else {
                showDialog()  // 显示对话框让用户改进设置
            }
        }
    }

    // 定义可能需要改进的位置设置类型
    enum class Improvement {
        GPS, NLP, GPS_AND_NLP, WIFI, WIFI_SCANNING, BLUETOOTH, BLE_SCANNING, PERMISSIONS, DATA_SOURCE
    }

    private fun updateImprovements() {
        // 获取当前设备的详细位置设置状态
        val states = getDetailedLocationSettingsStates()
        // 处理请求列表，确定每个请求的优先级和粒度
        val requests = requests?.map {
            // 如果粒度是权限级别，则假设为精确位置（GRANULARITY_FINE）
            it.priority to (if (it.granularity == Granularity.GRANULARITY_PERMISSION_LEVEL) Granularity.GRANULARITY_FINE else it.granularity)
        }.orEmpty()
        // 检查是否有高精度的GPS位置请求
        val gpsRequested = requests.any { it.first == Priority.PRIORITY_HIGH_ACCURACY && it.second == Granularity.GRANULARITY_FINE }
        // 检查是否有网络位置请求
        val networkLocationRequested = requests.any { it.first <= Priority.PRIORITY_LOW_POWER && it.second >= Granularity.GRANULARITY_COARSE }
        // 根据当前位置设置状态和请求的需求，生成改进列表
        improvements = listOfNotNull(
            // 如果请求了高精度GPS和网络位置，但当前设备的GPS或网络位置不可用，那么添加GPS_AND_NLP改进项
            Improvement.GPS_AND_NLP.takeIf { gpsRequested && !states.gpsUsable || networkLocationRequested && !states.networkLocationUsable },
            // 如果当前设备没有足够的位置权限，添加权限改进项
            Improvement.PERMISSIONS.takeIf { !states.coarseLocationPermission || !states.fineLocationPermission },
        )
    }

    private fun showDialog() {
        // 构建一个警告对话框
        val alertDialog = buildAlertDialog()
            .setOnCancelListener(this)  // 设置取消监听
            .setPositiveButton(R.string.location_settings_dialog_btn_sure, this)  // 设置确认按钮
            .setNegativeButton(R.string.location_settings_dialog_btn_cancel, this)  // 设置取消按钮
            .create()
        alertDialog.setCanceledOnTouchOutside(false)  // 设置点击对话框外部不取消对话框

        // 加载对话框布局
        val view = layoutInflater.inflate(R.layout.location_settings_dialog, null)
        // 设置对话框标题，根据alwaysShow变量决定显示的标题文本
        view.findViewById<TextView>(R.id.message_title)
            .setText(if (alwaysShow) R.string.location_settings_dialog_message_title_to_continue else R.string.location_settings_dialog_message_title_for_better_experience)

        // 获取对话框中用于显示改进项的布局容器
        val messages = view.findViewById<LinearLayout>(R.id.messages)
        // 遍历改进项列表，为每一项创建视图，并设置相关属性
        for ((messageIndex, improvement) in improvements.withIndex()) {
            val item = layoutInflater.inflate(R.layout.location_settings_dialog_item, messages, false)
            // 根据改进类型设置显示的文本
            item.findViewById<TextView>(android.R.id.text1).text = when (improvement) {
                Improvement.GPS_AND_NLP -> getString(R.string.location_settings_dialog_message_location_services_gps_and_nlp)
                Improvement.PERMISSIONS -> getString(R.string.location_settings_dialog_message_grant_permissions)
                else -> {
                    Log.w(TAG, "Unsupported improvement: $improvement")
                    ""
                }
            }
            // 根据改进类型设置显示的图标
            item.findViewById<ImageView>(android.R.id.icon).setImageDrawable(
                when (improvement) {
                    Improvement.GPS_AND_NLP -> ContextCompat.getDrawable(this, R.drawable.ic_gps)
                    Improvement.PERMISSIONS -> ContextCompat.getDrawable(this, R.drawable.ic_location)
                    else -> {
                        Log.w(TAG, "Unsupported improvement: $improvement")
                        null
                    }
                }
            )
            // 将创建的视图添加到对话框中
            messages.addView(item, messageIndex + 1)
        }

        // 将视图设置到对话框并显示
        alertDialog.setView(view)
        alertDialog.show()
    }


    private fun handleContinue() {
        // 从改进列表中获取第一个需要的改进，如果没有需要的改进，直接结束并返回结果OK
        val improvement = improvements.firstOrNull() ?: return finishResult(RESULT_OK)

        // 根据需要的改进类型进行不同的处理
        when (improvement) {
            Improvement.PERMISSIONS -> {
                // 如果需要的改进是权限，构建并启动请求权限的活动
                val intent = Intent(this, AskPermissionActivity::class.java).apply {
                    putExtra(EXTRA_PERMISSIONS, locationPermissions.toTypedArray()) // 传递需要请求的权限
                }
                startActivityForResult(intent, REQUEST_CODE_PERMISSION)  // 启动活动，并等待结果
                return
            }

            // 如果需要的改进是开启GPS、网络位置提供者（NLP）或两者
            Improvement.GPS, Improvement.NLP, Improvement.GPS_AND_NLP -> {
                // 构建一个Intent，引导用户到位置源设置页面
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(intent, REQUEST_CODE_LOCATION)  // 启动设置，并等待结果
                return // 结果将在onActivityResult中处理
            }

            // 如果遇到不支持的改进类型，记录警告信息
            else -> {
                Log.w(TAG, "Unsupported improvement: $improvement")
            }
        }
        // 重新检查是否有其他需要的改进，并递归处理
        updateImprovements()
        handleContinue()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // 根据请求代码判断是否为位置设置或权限请求的结果
        if (requestCode == REQUEST_CODE_LOCATION || requestCode == REQUEST_CODE_PERMISSION) {
            // 检查设置或权限是否有所改进
            checkImprovements()
        } else {
            // 其他请求的结果交给超类处理
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun checkImprovements() {
        // 检查改进前后的状态是否有变化，以决定下一步操作
        val oldImprovements = improvements
        updateImprovements()  // 更新需要的改进列表
        if (oldImprovements == improvements) {
            // 如果改进没有变化，重新显示对话框
            showDialog()
        } else {
            // 如果有变化，继续处理改进
            handleContinue()
        }
    }

    private fun finishResult(resultCode: Int) {
        // 获取当前的位置设置状态，并设置结果
        val states = getDetailedLocationSettingsStates().toApi()
        setResult(resultCode, Intent().apply {
            // 序列化位置设置状态并放入结果Intent
            putExtra(EXTRA_SETTINGS_STATES, SafeParcelableSerializer.serializeToBytes(states))
        })
        finish()  // 结束当前活动
    }

    override fun onBackPressed() {
        // 处理返回键事件，取消当前操作
        finishResult(RESULT_CANCELED)
    }

    override fun onCancel(dialog: DialogInterface?) {
        // 处理对话框取消事件，取消当前操作
        finishResult(RESULT_CANCELED)
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        // 处理对话框按钮点击事件
        Log.d(TAG, "Not yet implemented: onClick")
        when (which) {
            DialogInterface.BUTTON_NEGATIVE -> finishResult(RESULT_CANCELED)  // 取消按钮，取消操作
            DialogInterface.BUTTON_POSITIVE -> handleContinue()  // 确认按钮，继续处理改进
        }
    }

    companion object {
        // 定义所需的位置权限列表
        private val locationPermissions = listOfNotNull(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            // 对于Android 10及以上版本，需要后台位置权限
            if (SDK_INT >= 29) Manifest.permission.ACCESS_BACKGROUND_LOCATION else null
        )
    }
}