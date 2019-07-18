package com.example.conferenceroomtabletversion.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.conferenceroomtabletversion.R
import com.example.conferenceroomtabletversion.helper.Constants
import com.example.conferenceroomtabletversion.helper.GetProgress
import com.example.conferenceroomtabletversion.model.NewBookingInput
import com.example.conferenceroomtabletversion.utils.GetPreference
import com.example.conferenceroomtabletversion.viewmodel.BookingForTheDayViewModel
import com.github.nkzawa.socketio.client.Socket
import kotlinx.android.synthetic.main.activity_booking_status.*


class MainActivity : AppCompatActivity() {
    lateinit var mSocket: Socket
    private lateinit var mProgressDialog: ProgressDialog
    private lateinit var mBookingForTheDayViewModel: BookingForTheDayViewModel
    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.booking_layout)
        checkForSetup()
        init()
        loadFragment()
    }

    private fun init() {
        initStatusBar()
        initLateInitFields()
        setRoomDetails()
    }
    /**
     * hide status bar
     */
    private fun initStatusBar() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    @SuppressLint("SetTextI18n")
    private fun setRoomDetails() {
        val roomName = GetPreference.getRoomNameFromSharedPreference(this)
        val buildingName = GetPreference.getBuildingNameFromSharedPreference(this)
        val roomCapacity = GetPreference.getCapacityFromSharedPreference(this)
        val roomAmenities = GetPreference.getRoomAmenitiesFromSharedPreference(this)
        if (
            roomName == Constants.DEFAULT_STRING_PREFERENCE_VALUE ||
            buildingName == Constants.DEFAULT_STRING_PREFERENCE_VALUE ||
            roomCapacity == Constants.DEFAULT_INT_PREFERENCE_VALUE
        ) {
            goForSetup()
        } else {
            room_name_text_view.text = "$roomName, $buildingName"
            room_capacity.text = "$roomCapacity seater"
            room_amenities.text = roomAmenities!!
        }
    }

    /**
     * function will check room details inside shared preference
     */
    private fun checkForSetup() {
        val roomName = GetPreference.getRoomNameFromSharedPreference(this)
        val buildingName = GetPreference.getBuildingNameFromSharedPreference(this)
        val roomCapacity = GetPreference.getCapacityFromSharedPreference(this)
        if (
            roomName == Constants.DEFAULT_STRING_PREFERENCE_VALUE ||
            buildingName == Constants.DEFAULT_STRING_PREFERENCE_VALUE ||
            roomCapacity == Constants.DEFAULT_INT_PREFERENCE_VALUE
        ) {
            goForSetup()
        }
    }

    /**
     * intent to the setting tablet activity
     */
    private fun goForSetup() {
        startActivity(Intent(this@MainActivity, SettingBuildingConferenceActivity::class.java))
        finish()
    }

    /**
     * on press of back button clear activity stack
     */
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    /**
     * initialize lateinit fields
     */
    private fun initLateInitFields() {
        mProgressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), this)
        mBookingForTheDayViewModel = ViewModelProviders.of(this).get(BookingForTheDayViewModel::class.java)
    }

    // -----------------------------------------------------------------------all api calls ------------------------------------------------------------------



    /**
     * code for hide soft keyboard
     */
    fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     *  function will load load main fragment
     */
    private fun loadFragment() {
        supportFragmentManager.beginTransaction().replace(
            R.id.container_frame_layout,
            MainFragment()
        ).commit()
    }

    override fun onDestroy() {
        super.onDestroy()
      //  mSocket.disconnect()
    }

    /**
     * replace fragment from another fragment
     */
    fun replaceFragments(fragmentClass: Class<*>) {
        var fragment: Fragment? = null
        try {
            fragment = fragmentClass.newInstance() as Fragment
        } catch (e: Exception) {
            e.printStackTrace()
        }

        /**
         * Insert the fragment by replacing any existing fragment
         */
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.container_frame_layout, fragment!!).commit()
    }

    /**
     * replace fragment from another fragment
     */
    fun replaceFragmentsWithData(fragmentClass: Class<*>, data: Any) {
        var fragment: Fragment? = null
        try {
            fragment = fragmentClass.newInstance() as Fragment
        } catch (e: Exception) {
            e.printStackTrace()
        }

        /**
         * Insert the fragment by replacing any existing fragment
         */
        val fragmentManager = supportFragmentManager
        val args = Bundle()
        args.putString(Constants.DATA, data as String)
        fragment!!.arguments = args
        fragmentManager.beginTransaction().replace(R.id.container_frame_layout, fragment).commit()
    }

    /**
     * replace fragment from another fragment
     */
    fun replaceFragmentsWithDataForExtendMeeting(fragmentClass: Class<*>,
                                                 startTime: String,
                                                 runningMeetStartTime: String,
                                                 endTime: String,
                                                 mMeetingId: Int,
                                                 isNextMeetingPresent: Boolean) {
        var fragment: Fragment? = null
        try {
            fragment = fragmentClass.newInstance() as Fragment
        } catch (e: Exception) {
            e.printStackTrace()
        }

        /**
         * Insert the fragment by replacing any existing fragment
         */
        val fragmentManager = supportFragmentManager
        val args = Bundle()
        args.putString(Constants.START_TIME, startTime)
        args.putString(Constants.END_TIME, endTime)
        args.putString(Constants.RUNNING_MEET_START_TIME, runningMeetStartTime)
        args.putBoolean(Constants.IS_NEXT_MEETING_PRESENT, isNextMeetingPresent)
        args.putInt(Constants.MEET_ID, mMeetingId)
        fragment!!.arguments = args
        fragmentManager.beginTransaction().replace(R.id.container_frame_layout, fragment).commit()
    }


}


/*
val androidDeviceId = Settings.Secure.getString(this.contentResolver,
            Settings.Secure.ANDROID_ID)
        val app = application as BaseApplication
        mSocket = app.getmSocket()!!
        mSocket.connect()
        if(!mSocket.connected()) {
           Toast.makeText(this, "Not connected", Toast.LENGTH_SHORT).show()
        }
        mSocket.on("make_call") {
            //make api call to get refreshed data
        }
 */
