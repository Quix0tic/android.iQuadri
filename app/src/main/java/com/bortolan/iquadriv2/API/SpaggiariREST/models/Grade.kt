package com.bortolan.iquadriv2.API.SpaggiariREST.models

import java.util.*

data class Grade(val canceled: Boolean,
                 val evtCode: String,
                 val componentPos: Int,
                 val evtDate: Date,
                 val subjectDesc: String,
                 val evtId: Int,
                 val notesForFamily: String,
                 val periodPos: Int,
                 val periodDesc: String,
                 val displayValue: String,
                 val subjectId: Int,
                 val componentDesc: String,
                 val underlined: Boolean,
                 val mUser: String,
                 val decimalValue: Float,
                 val weightFactor: Double)