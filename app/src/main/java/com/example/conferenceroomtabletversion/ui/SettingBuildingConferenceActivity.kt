package com.example.conferenceroomtabletversion.ui

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.conferenceroomtabletversion.R
import com.example.conferenceroomtabletversion.helper.Constants
import com.example.conferenceroomtabletversion.helper.GetProgress
import com.example.conferenceroomtabletversion.helper.NetworkState
import com.example.conferenceroomtabletversion.model.Buildings
import com.example.conferenceroomtabletversion.model.ConferenceList
import com.example.conferenceroomtabletversion.viewmodel.SettingsViewModel
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_setting_building_conference.*

class SettingBuildingConferenceActivity : AppCompatActivity() {
    /**
     * Decleration of VieModel and variable
     */
    private lateinit var mBuildingsViewModel: SettingsViewModel

    private lateinit var progressDialog: ProgressDialog

    private lateinit var configure: TextView

    private var buildingId: Int = -1

    private var roomId: Int = -1

    private var roomDetails = ConferenceList()

    /**
     * OnCreate function to create the activity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_building_conference)
        init()
        configure.setOnClickListener {
            if (buildingId == -1 || roomId == -1) {
                Toasty.info(this, "Please select", Toasty.LENGTH_SHORT, true).show()
            } else {
                setValuesOfRoomInsidePreference()
                startActivity(Intent(this@SettingBuildingConferenceActivity, ConferenceBookingActivity::class.java))
                finish()
            }
        }

    }

    private fun getConference(buildingId: Int) {
        progressDialog.show()
        mBuildingsViewModel.getConferenceRoomList(buildingId)
    }

    private fun settingObserveData() {
        mBuildingsViewModel.returnMBuildingSuccess().observe(this, Observer {
            progressDialog.dismiss()
            buildingListFromBackend(it)

        })
        mBuildingsViewModel.returnMBuildingFailure().observe(this, Observer {
            progressDialog.dismiss()

        })

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    private fun buildingListFromBackend(buildingList: List<Buildings>?) {
        //sendDataForSpinner(buildingList!!)
        setSpinnerForBuildings(buildingList as ArrayList<Buildings>)

    }

    private fun setSpinnerForBuildings(buildingList: ArrayList<Buildings>) {
        buildingList.add(0, Buildings(-1, getString(R.string.select_building)))
        val buildingNameList = ArrayList<String>()
        for (building in buildingList) {
            buildingNameList.add(building.buildingName!!)
        }
        building_spinner.adapter =
            ArrayAdapter<String>(
                this@SettingBuildingConferenceActivity,
                R.layout.custom_spinner,
                R.id.list,
                buildingNameList
            )
        building_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                /**
                 * It selects the first building
                 */
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.i(
                    "-------building list",
                    buildingList[position].buildingName + " " + buildingList[position].buildingId
                )
                buildingId = buildingList[position].buildingId!!
                getConference(buildingId)
            }
        }
    }

    private fun setSpinnerForConferenceRoom(roomList: ArrayList<ConferenceList>) {
        val message: String = if (roomList.isEmpty()) {
            getString(R.string.no_rooms_in_building)
        } else {
            getString(R.string.select_rooms)
        }
        roomList.add(
            0, ConferenceList(
                message, 0, "", -1, -1, emptyList(), ""
            )
        )
        val roomNameList = ArrayList<String>()
        for (room in roomList) {
            roomNameList.add(room.roomName!!)
        }
        conference_spinner.adapter = ArrayAdapter<String>(
            this@SettingBuildingConferenceActivity,
            R.layout.custom_spinner,
            R.id.list,
            roomNameList
        )
        conference_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                /**
                 * nothing selected
                 */
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                roomId = roomList[position].roomId!!
                roomDetails = roomList[position]
            }

        }
    }

    private fun conferenceObserveData() {
        mBuildingsViewModel.returnConferenceRoomList().observe(this, Observer {
            progressDialog.dismiss()
            setSpinnerForConferenceRoom(it as ArrayList<ConferenceList>)
        })
        mBuildingsViewModel.returnFailureForConferenceRoom().observe(this, Observer {
            progressDialog.dismiss()
        })
    }

    private fun init() {
        hideStatusBar()
        progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), this)
        configure = findViewById(R.id.set_up_room)
        mBuildingsViewModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        settingObserveData()
        conferenceObserveData()
        getViewModel()
    }

    private fun hideStatusBar() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

    }

    private fun getViewModel() {
        if (NetworkState.appIsConnectedToInternet(this)) {
            progressDialog.show()
            mBuildingsViewModel.getBuildingList()
        } else {
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setValuesOfRoomInsidePreference() {
        val edit = getSharedPreferences(Constants.PREFERENCE, Context.MODE_PRIVATE).edit()
        edit.putInt(Constants.ROOM_ID, roomDetails.roomId!!)
        edit.putBoolean(Constants.ONBORDING, true)
        edit.putInt(Constants.BUILDING_ID, roomDetails.buildingId!!)
        edit.putString(Constants.BUILDING_NAME, roomDetails.buildingName)
        edit.putInt(Constants.CAPACITY, roomDetails.capacity!!)
        edit.putString(Constants.ROOM_NAME, roomDetails.roomName)
        edit.putString(Constants.ROOM_AMINITIES, roomDetails.amenities!!.joinToString())
        edit.apply()
    }
}


/*
     private fun sendDataForSpinner(buildingList: List<Buildings>) {
        val items = mutableListOf<String>()
        val itemsId = mutableListOf<Int>()
        items.add(getString(R.string.select_building))
        itemsId.add(-1)
        for (item in buildingList) {
            items.add(item.buildingName!!)
            itemsId.add(item.buildingId!!.toInt())
        }
        building_spinner.adapter =
            ArrayAdapter<String>(this@SettingBuildingConferenceActivity, R.layout.custom_spinner, R.id.list, items)
        building_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                /**
                 * It selects the first building
                 */
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                getConference(itemsId[position])
                conferenceObserveData()
            }
        }

    }

     private fun validate(conferenceid: Int): Boolean {
        return conferenceid != -1
    }



      private fun setValuesInsidePreferences(
        capacity: Int,
        roomId: Int,
        roomName: String,
        buildingName: String,
        buildingId: Int,
        aminities: List<String>
    ) {
        val edit = getSharedPreferences(Constants.PREFERENCE, Context.MODE_PRIVATE).edit()
        edit.putInt(Constants.ROOM_ID, roomId)
        edit.putBoolean(Constants.ONBORDING, true)
        edit.putInt(Constants.BUILDING_ID, buildingId)
        edit.putString(Constants.BUILDING_NAME, buildingName)
        edit.putInt(Constants.CAPACITY, capacity)
        edit.putString(Constants.ROOM_NAME, roomName)
        edit.putString(Constants.ROOM_AMINITIES, aminities.joinToString())
        edit.apply()
    }



      private fun setAdapter(it: List<ConferenceList>) {
        val conferencename = mutableListOf<String>()
        val conferenceid = mutableListOf<Int>()
        val buildingName = mutableListOf<String>()
        val buildingId = mutableListOf<Int>()
        val conferenceCapacity = mutableListOf<Int>()
        val conferenceAminities = mutableListOf<List<String>>()

        if (it.isEmpty()) {
            conferencename.add(getString(R.string.no_rooms_in_building))
            conferenceid.add(-1)
            conferenceCapacity.add(-1)
            buildingId.add(-1)
            buildingName.add("")
            conferenceAminities.add(emptyList())
        } else {
            conferencename.add(getString(R.string.select_rooms))
            conferenceid.add(-1)
            conferenceCapacity.add(-1)
            buildingId.add(-1)
            buildingName.add("")
            conferenceAminities.add(emptyList())
        }
        for (item in it) {
            conferencename.add(item.roomName!!)
            conferenceid.add(item.roomId!!)
            conferenceCapacity.add(item.capacity!!)
            buildingId.add(item.buildingId!!)
            buildingName.add(item.buildingName!!)
            conferenceAminities.add(item.amenities!!)
        }
        conference_spinner.adapter =
            ArrayAdapter<String>(
                this@SettingBuildingConferenceActivity,
                R.layout.custom_spinner, R.id.list,
                conferencename
            )
        conference_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                /**
                 * It selects the first conference room
                 */
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                configure.setOnClickListener {
                    valid = validate(conferenceid[position])
                    if (valid) {
                        setValuesInsidePreferences(
                            conferenceCapacity[position],
                            conferenceid[position],
                            conferencename[position],
                            buildingName[position],
                            buildingId[position],
                            conferenceAminities[position]
                        )
                        startActivity(
                            Intent(
                                this@SettingBuildingConferenceActivity,
                                ConferenceBookingActivity::class.java
                            )
                        )
                        finish()
                    }
                }
            }
        }
    }
     */