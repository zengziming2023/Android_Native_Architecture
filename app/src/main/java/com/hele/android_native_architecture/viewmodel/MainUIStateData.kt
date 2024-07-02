package com.hele.android_native_architecture.viewmodel

data class MainUIStateData(
    val name: String = "Android Compose",
    val text: String = "222",
    val textClick: () -> Unit = {},
    val showDialog: Boolean = false,
    val showMyDialog: () -> Unit = {},
    val dialogConfirm: () -> Unit = {}
) {
}
