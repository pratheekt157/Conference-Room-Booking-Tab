package com.example.conferenceroomtabletversion.utils

import android.content.Context
import com.example.conferenceroomtabletversion.helper.Constants

class GetPreference {
    companion object {

        // get building name from shared preference
        fun getBuildingNameFromSharedPreference(mContext: Context): String {
            return mContext.getSharedPreferences(Constants.PREFERENCE, Context.MODE_PRIVATE).getString(Constants.BUILDING_NAME, Constants.DEFAULT_STRING_PREFERENCE_VALUE)
        }

        // get room capacity from shared preference
        fun getCapacityFromSharedPreference(mContext: Context): Int {
            return mContext.getSharedPreferences(Constants.PREFERENCE, Context.MODE_PRIVATE).getInt(Constants.CAPACITY, Constants.DEFAULT_INT_PREFERENCE_VALUE)
        }

        // get room Name from shared preference
        fun getRoomNameFromSharedPreference(mContext: Context): String {
            return mContext.getSharedPreferences(Constants.PREFERENCE, Context.MODE_PRIVATE).getString(Constants.ROOM_NAME, Constants.DEFAULT_STRING_PREFERENCE_VALUE)
        }

        // get room id from shared preference
        fun getRoomIdFromSharedPreference(mContext: Context): Int {
            return mContext.getSharedPreferences(Constants.PREFERENCE, Context.MODE_PRIVATE).getInt(Constants.ROOM_ID, Constants.DEFAULT_INT_PREFERENCE_VALUE)
        }
        // get building id from shared preference
        fun getBuildingIdFromSharedPreference(mContext: Context): Int {
            return mContext.getSharedPreferences(Constants.PREFERENCE, Context.MODE_PRIVATE).getInt(Constants.BUILDING_ID, Constants.DEFAULT_INT_PREFERENCE_VALUE)
        }
        fun getOnbordingFromSharedPreference(mContext: Context):Boolean{
        return mContext.getSharedPreferences(Constants.PREFERENCE,Context.MODE_PRIVATE).getBoolean(Constants.ONBORDING, Constants.DEFAULT_ONBOARDING)
        }
    }
}