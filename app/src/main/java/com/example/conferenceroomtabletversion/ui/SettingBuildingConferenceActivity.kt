package com.example.conferenceroomtabletversion.ui

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.conferenceroomtabletversion.R
import com.example.conferenceroomtabletversion.helper.*
import com.example.conferenceroomtabletversion.model.Buildings
import com.example.conferenceroomtabletversion.model.ConferenceList
import com.example.conferenceroomtabletversion.viewmodel.SettingsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_setting_building_conference.*

class SettingBuildingConferenceActivity : AppCompatActivity() {
    /**
     * Decleration of VieModel and variable
     */

    private lateinit var mConferenceViewModel : SettingsViewModel

    private lateinit var mBuildingsViewModel: SettingsViewModel

    private lateinit var progressDialog: ProgressDialog

    private lateinit var configure: TextView

    private var valid: Boolean = false

    /**
     * OnCreate function to create the activity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_building_conference)
        //Initialization of fields and viewModel
        init()
        //Observe the
        settingObserveData()
    }


    private fun getConference(buildingId: Int) {
        progressDialog.show()
        mConferenceViewModel.getConferenceRoomList(buildingId)
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

    private fun buildingListFromBackend(buildingList: List<Buildings>?) {
        sendDataForSpinner(buildingList!!)

    }

    private fun sendDataForSpinner(buildingList: List<Buildings>) {
        val items = mutableListOf<String>()
        val itemsId = mutableListOf<Int>()
        items.add("Select Building")
        itemsId.add(-1)
        for (item in buildingList) {
            items.add(item.buildingName!!)
            itemsId.add(item.buildingId!!.toInt())
        }
        building_spinner.adapter = ArrayAdapter<String>(this@SettingBuildingConferenceActivity,R.layout.custom_spinner,R.id.list,items)
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

    private fun conferenceObserveData() {
        mConferenceViewModel.returnConferenceRoomList().observe(this, Observer {
            progressDialog.dismiss()
            setAdapter(it)
        })
        mConferenceViewModel.returnFailureForConferenceRoom().observe(this, Observer {
            progressDialog.dismiss()
        })
    }

    private fun setAdapter(it:List<ConferenceList>) {
        val conferencename = mutableListOf<String>()
        val conferenceid = mutableListOf<Int>()
        val buildingName = mutableListOf<String>()
        val buildingId = mutableListOf<Int>()
        val conferenceCapacity = mutableListOf<Int>()
        val conferenceAminities= mutableListOf<List<String>>()

        if (it.isEmpty()) {
            conferencename.add("No Room in the Buildings")
            conferenceid.add(-1)
            conferenceCapacity.add(-1)
            buildingId.add(-1)
            buildingName.add("")
            conferenceAminities.add(emptyList())
        } else {
            conferencename.add("Select Room")
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
                R.layout.custom_spinner,R.id.list,
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
                    valid=validate(conferenceid[position])
                    if (valid == false )
                    //
                    else {
                        setValuesInsidePreferences(conferenceCapacity[position], conferenceid[position], conferencename[position], buildingName[position], buildingId[position],conferenceAminities[position])
                        startActivity(Intent(this@SettingBuildingConferenceActivity,ConferenceBookingActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }


    private fun validate(conferenceid: Int):Boolean {
        return conferenceid != -1
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.RES_CODE && resultCode == Activity.RESULT_OK) {
            getViewModel()
        }
    }

    private fun setValuesInsidePreferences(capacity: Int, roomId: Int, roomName: String, buildingName: String, buildingId: Int , aminities: List<String>)  {
        val edit = getSharedPreferences(Constants.PREFERENCE, Context.MODE_PRIVATE).edit()
        edit.putInt(Constants.ROOM_ID, roomId)
        edit.putBoolean(Constants.ONBORDING,true)
        edit.putInt(Constants.BUILDING_ID, buildingId)
        edit.putString(Constants.BUILDING_NAME, buildingName)
        edit.putInt(Constants.CAPACITY, capacity)
        edit.putString(Constants.ROOM_NAME, roomName)
        edit.putString(Constants.ROOM_AMINITIES,aminities.joinToString())
        edit.apply()
    }


    private fun init(){
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), this)
        configure = findViewById(R.id.set_up_room)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        mBuildingsViewModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        mConferenceViewModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        if (NetworkState.appIsConnectedToInternet(this)) {
            getViewModel()
        } else {
            Toast.makeText(this,"No Internet",Toast.LENGTH_SHORT).show()
        }
    }

    private fun getViewModel() {
        // making API call
        progressDialog.show()
        mBuildingsViewModel.getBuildingList()
    }


}