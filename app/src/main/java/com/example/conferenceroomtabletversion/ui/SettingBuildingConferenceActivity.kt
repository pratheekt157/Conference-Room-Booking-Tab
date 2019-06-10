package com.example.conferenceroomtabletversion.ui

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.telecom.Conference
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.conferenceroomtabletversion.R
import com.example.conferenceroomtabletversion.helper.*
import com.example.conferenceroomtabletversion.model.Buildings
import com.example.conferenceroomtabletversion.model.ConferenceList
import com.example.conferenceroomtabletversion.utils.GetPreference
import com.example.conferenceroomtabletversion.viewmodel.BuildingViewModel
import com.example.conferenceroomtabletversion.viewmodel.ConferenceViewModel
import com.google.android.material.snackbar.Snackbar
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner
import kotlinx.android.synthetic.main.activity_setting_building_conference.*

class SettingBuildingConferenceActivity : AppCompatActivity() {


    private lateinit var mConferenceViewModel : ConferenceViewModel

    private lateinit var relativeLayout: RelativeLayout

    private lateinit var mBuildingsViewModel: BuildingViewModel

    private lateinit var mProgressDialog: ProgressDialog

    private lateinit var configure: Button

    private var valid: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_building_conference)
        if(GetPreference.getBuildingIdFromSharedPreference(this)!=-1){
            finish()
        }
        init()
        buildingObserveData()
   }


    private fun getConference(buildingId: Int) {
        mConferenceViewModel.getConferenceRoomList(buildingId)
    }

    private fun buildingObserveData() {
        mBuildingsViewModel.returnMBuildingSuccess().observe(this, Observer {
            buildingListFromBackend(it)

        })
        mBuildingsViewModel.returnMBuildingFailure().observe(this, Observer {
            mProgressDialog.dismiss()

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
            setAdapter(it)
        })
        mConferenceViewModel.returnFailureForConferenceRoom().observe(this, Observer {

        })
    }

    private fun setAdapter(it:List<ConferenceList>) {
        val conferencename = mutableListOf<String>()
        val conferenceid = mutableListOf<Int>()
        val buildingName = mutableListOf<String>()
        val buildingId = mutableListOf<Int>()
        val conferenceCapacity = mutableListOf<Int>()
        if (it.isEmpty()) {
            conferencename.add("No Room in the Buildings")
            conferenceid.add(-1)
        } else {
            conferencename.add("Select Room")
        }
        conferenceid.add(-1)
        for (item in it) {
            conferencename.add(item.roomName!!)
            conferenceid.add(item.roomId!!)
            conferenceCapacity.add(item.capacity!!)
            buildingId.add(item.buildingId!!)
            buildingName.add(item.buildingName!!)

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
                            Snackbar.make(relativeLayout,"Select the Room",Snackbar.LENGTH_SHORT).show()
                        else {
                            setValuesInsidePreferences(conferenceCapacity[position-1], conferenceid[position-1], conferencename[position-1], buildingName[position-1], buildingId[position-1])
                            startActivity(Intent(this@SettingBuildingConferenceActivity,BookingDetailsActivity::class.java))
                        }
                    }
            }
        }
    }


    private fun validate(conferenceid: Int):Boolean {
        if(conferenceid==-1)
        {
           return false
        }
        else
            return true
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.RES_CODE && resultCode == Activity.RESULT_OK) {
            getViewModel()
        }
    }

    private fun setValuesInsidePreferences(capacity: Int, roomId: Int, roomName: String, buildingName: String, buildingId: Int)  {
        val edit = getSharedPreferences(Constants.PREFERENCE, Context.MODE_PRIVATE).edit()
        edit.putInt(Constants.ROOM_ID, roomId)
        edit.putInt(Constants.BUILDING_ID, buildingId)
        edit.putString(Constants.BUILDING_NAME, buildingName)
        edit.putInt(Constants.CAPACITY, capacity)
        edit.putString(Constants.ROOM_NAME, roomName)
        edit.apply()
    }
    private fun init(){
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        relativeLayout = findViewById(R.id.setting_activity)
        configure = findViewById(R.id.set_up_room)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        mBuildingsViewModel = ViewModelProviders.of(this).get(BuildingViewModel::class.java)
        mConferenceViewModel = ViewModelProviders.of(this).get(ConferenceViewModel::class.java)
        if (NetworkState.appIsConnectedToInternet(this)) {
            getViewModel()
        } else {
                Toast.makeText(this,"No Internet",Toast.LENGTH_SHORT).show()
        }
    }

    private fun getViewModel() {

        // making API call
        mBuildingsViewModel.getBuildingList()
    }

    override fun onBackPressed(){
       finishAffinity()
    }

}
