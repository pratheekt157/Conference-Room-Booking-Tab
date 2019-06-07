package com.example.conferenceroomtabletversion.ui

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.example.conferenceroomtabletversion.R
import com.example.conferenceroomtabletversion.helper.BookingForTheDayAdapter
import com.example.conferenceroomtabletversion.helper.GetProgress
import com.example.conferenceroomtabletversion.helper.NetworkState
import com.example.conferenceroomtabletversion.model.BookingDeatilsForTheDay
import com.example.conferenceroomtabletversion.model.Test
import com.example.conferenceroomtabletversion.viewmodel.BookingForTheDayViewModel

class ShowBookings : AppCompatActivity() {

    private lateinit var mBookingForTheDayViewModel: BookingForTheDayViewModel
    private lateinit var mProgressDialog: ProgressDialog
    private lateinit var mBookingListAdapter: BookingForTheDayAdapter
    private lateinit var mRecyclerView: RecyclerView
    private var mList = ArrayList<Test>()
    var mBookingList = ArrayList<BookingDeatilsForTheDay>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_bookings)
        init()
        //addDataToList()
        makeCall()
    }

    private fun init() {
        // hide status bar
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        val actionBar = supportActionBar
        actionBar!!.title = Html.fromHtml("<font color=\"#FFFFFF\">" + "Show Meetings" + "</font>")

        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        ) //show the activity in full screen
        mRecyclerView = findViewById(R.id.recycler_view_booking_list)
        mProgressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), this)
        mBookingForTheDayViewModel = ViewModelProviders.of(this).get(BookingForTheDayViewModel::class.java)
       observeData()
    }

    private fun makeCall() {
        if (NetworkState.appIsConnectedToInternet(this)) {
            getViewModel()
        } else {
            //val i = Intent(this, NoInternetConnectionActivity::class.java)
            //startActivityForResult(i, Constants.RES_CODE)
        }
    }

    private fun getViewModel() {
        mProgressDialog.show()
        mBookingForTheDayViewModel.getBookingList(22)
    }

    /**
     * all observer for LiveData
     */
    private fun observeData() {

        /**
         * observing data for booking list
         */
        mBookingForTheDayViewModel.returnSuccess().observe(this, Observer {
            mProgressDialog.dismiss()
            setFilteredDataToAdapter(it)
        })
        mBookingForTheDayViewModel.returnFailure().observe(this, Observer {
            mProgressDialog.dismiss()
//             else if (it == Constants.NO_CONTENT_FOUND && finalList.size == 0) {
//                //upcoming_empty_view.visibility = View.VISIBLE
//                //r1_dashboard.setBackgroundColor(Color.parseColor("#F7F7F7"))
//            } else {
//                //ShowToast.show(activity!!, it as Int)
//            }
        })

    }
    private fun setFilteredDataToAdapter(it: List<BookingDeatilsForTheDay>) {
        mBookingListAdapter = BookingForTheDayAdapter(
            it as ArrayList<BookingDeatilsForTheDay>,
            object : BookingForTheDayAdapter.ShowMembersListener {
                override fun showMembers(mEmployeeList: List<String>) {
                    showMeetingMembers(mEmployeeList)
                }
            }
        )
        mRecyclerView.adapter = mBookingListAdapter
    }

    //show details of the meeting in dialog
    private fun showMeetingMembers(mEmployeeList: List<String>) {

    }

    fun addDataToList() {
        var t1 = Test("2019-06-01T17:40:00", "2019-06-01T17:45:00", "Advantage client meeting", "Prateek Patidar")
        var t2 = Test("2019-06-01T18:44:00", "2019-06-31T18:45:00", "Planning second", "Kapil Patidar")
        var t3 = Test("2019-06-01T19:58:00", "2019-06-01T19:55:00", "Client meeting", "Payal")
        var t5 = Test("2019-06-01T18:25:00", "2019-06-01T18:30:00", "Wadavani Final demo", "Susheela")
        var t6 = Test("2019-06-01T19:45:00", "2019-06-01T19:55:00", "Narcissus Stand up", "Utarsh jain")
        mList.add(t1)
        mList.add(t2)
        mList.add(t3)
        mList.add(t5)
        mList.add(t6)
    }
}
