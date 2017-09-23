package com.bortolan.iquadriv2.API.SpaggiariREST.models

import android.util.Base64
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

data class Grade(val canceled: Boolean,
                 val evtCode: String,
                 val componentPos: Int,
                 val evtDate: String,
                 val subjectDesc: String,
                 val evtId: Int,
                 val notesForFamily: String,
                 val periodPos: Int,
                 val periodDesc: String,
                 val displayValue: String,
                 val color: String,
                 val subjectId: Int,
                 val componentDesc: String,
                 val underlined: Boolean,
                 val mUser: String,
                 val decimalValue: Float,
                 val weightFactor: Double) {
    fun getHash(): String {
        val messageDigest: MessageDigest
        try {
            messageDigest = MessageDigest.getInstance("SHA-256")
            messageDigest.update((periodPos.toString() + componentDesc + evtDate + decimalValue + notesForFamily).toByteArray())
            return Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        return ""
    }
}