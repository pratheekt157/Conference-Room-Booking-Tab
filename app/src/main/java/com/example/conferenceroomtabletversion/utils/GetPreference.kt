package com.example.conferenceroomtabletversion.utils

import android.content.Context
import com.example.conferenceroomtabletversion.helper.Constants

class GetPreference {
    companion object {
        fun getRoomId(mContext: Context): Int {
            val ROOMID = -1
            return mContext.getSharedPreferences(Constants.PREFERENCE, Context.MODE_PRIVATE).getInt(Constants.ROOM_ID, ROOMID)
        }
    }
}