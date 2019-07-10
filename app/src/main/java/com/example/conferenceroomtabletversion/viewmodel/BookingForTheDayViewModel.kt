package com.example.conferenceroomtabletversion.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferenceroomtabletversion.model.*
import com.example.conferenceroomtabletversion.repository.BookingDetailsForTheDayRepository
import com.example.conferenceroomtabletversion.service.ResponseListener

class BookingForTheDayViewModel : ViewModel() {

    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    var mBookingDetailsForTheDayRepository: BookingDetailsForTheDayRepository? = null

    /**
     * a MutableLivedata variables which will hold the Positive and Negative response from server
     */
    private var mBookingList = MutableLiveData<List<BookingDeatilsForTheDay>>()

    private var mFailureCodeForBookingList = MutableLiveData<Any>()


    private var mSuccessForEndMeeting = MutableLiveData<Any>()

    private var mFailureCodeForEndMeeting = MutableLiveData<Any>()


    private var mSuccessForStartMeeting = MutableLiveData<Any>()

    private var mFailureCodeForStartMeeting = MutableLiveData<Any>()

    private var mSuccessForFeedback = MutableLiveData<Any>()

    private var mFailureCodeForFeedback = MutableLiveData<Any>()

    private var mSuccessCodeForBlockRoom =  MutableLiveData<Any>()
    private var mFailureCodeForBlockRoom =  MutableLiveData<Any>()



    /**
     * a MutableLivedata variable which will hold the Value for the Livedata
     */
    var mSuccessForBooking = MutableLiveData<Int>()

    var mErrorCodeFromServerFromBooking = MutableLiveData<Any>()

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun getBookingList(roomId: Int) {
        mBookingDetailsForTheDayRepository = BookingDetailsForTheDayRepository.getInstance()
        mBookingDetailsForTheDayRepository!!.getBookingList(
            roomId,
            object : ResponseListener {
                override fun onSuccess(success: Any) {
                    mBookingList.value = success as List<BookingDeatilsForTheDay>
                }

                override fun onFailure(failure: Any) {
                    mFailureCodeForBookingList.value = failure
                }

            })
    }

    /**
     * function will return the MutableLiveData of List of dashboard
     */
    fun returnSuccess(): MutableLiveData<List<BookingDeatilsForTheDay>> {
        return mBookingList
    }

    /**
     * function will return the MutableLiveData of Int if something went wrong at server
     */
    fun returnFailure(): MutableLiveData<Any> {
        return mFailureCodeForBookingList
    }

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun endMeeting(mEndMeeting: EndMeeting) {
        mBookingDetailsForTheDayRepository = BookingDetailsForTheDayRepository.getInstance()
        mBookingDetailsForTheDayRepository!!.endMeeting(
            mEndMeeting,
            object : ResponseListener {
                override fun onSuccess(success: Any) {
                    mSuccessForEndMeeting.value = success
                }

                override fun onFailure(failure: Any) {
                    mFailureCodeForEndMeeting.value = failure
                }

            })
    }

    /**
     * function will return the MutableLiveData of List of dashboard
     */
    fun returnSuccessForEndMeeting(): MutableLiveData<Any> {
        return mSuccessForEndMeeting
    }

    /**
     * function will return the MutableLiveData of Int if something went wrong at server
     */
    fun returnFailureForEndMeeting(): MutableLiveData<Any> {
        return mFailureCodeForEndMeeting
    }


    /**
     * -------------------------------------add Local booking-------------------------------------
     */
    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun addBookingDetails(mBooking: NewBookingInput) {
        mBookingDetailsForTheDayRepository = BookingDetailsForTheDayRepository.getInstance()
        mBookingDetailsForTheDayRepository!!.addBookingDetails(mBooking, object : ResponseListener {
            override fun onFailure(failure: Any) {
                mErrorCodeFromServerFromBooking.value = failure
            }

            override fun onSuccess(success: Any) {
                mSuccessForBooking.value = success as Int
            }

        })
    }

    /**
     * function will return MutableLiveData of List of EmployeeList
     */
    fun returnSuccessForBooking(): MutableLiveData<Int> {
        return mSuccessForBooking
    }

    /**
     * function will return the MutableLiveData of Int if something went wrong at server
     */
    fun returnFailureForBooking(): MutableLiveData<Any> {
        return mErrorCodeFromServerFromBooking
    }

    /**
     * ----------------------------------------------start meeting----------------
     */
    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun startMeeting(mEndMeeting: EndMeeting) {
        mBookingDetailsForTheDayRepository = BookingDetailsForTheDayRepository.getInstance()
        mBookingDetailsForTheDayRepository!!.startMeeting(
            mEndMeeting,
            object : ResponseListener {
                override fun onSuccess(success: Any) {
                    mSuccessForStartMeeting.value = success
                }

                override fun onFailure(failure: Any) {
                    mFailureCodeForStartMeeting.value = failure
                }

            })
    }

    /**
     * function will return the MutableLiveData of List of dashboard
     */
    fun returnSuccessForStartMeeting(): MutableLiveData<Any> {
        return mSuccessForStartMeeting
    }

    /**
     * function will return the MutableLiveData of Int if something went wrong at server
     */
    fun returnFailureForStartMeeting(): MutableLiveData<Any> {
        return mFailureCodeForStartMeeting
    }
  // ------------------------------------------extend meeting time-------------------
    /**
     * a MutableLivedata variable which will hold the positive response from repository
     */
    var mSuccessForUpdate =  MutableLiveData<Int>()

    /**
     * a MutableLivedata variable which will hold the positive response from repository
     */
    var mFailureForUpdate =  MutableLiveData<Any>()

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun updateBookingDetails(mUpdateBooking: UpdateBooking) {
        mBookingDetailsForTheDayRepository = BookingDetailsForTheDayRepository.getInstance()
        mBookingDetailsForTheDayRepository!!.updateBookingDetails(mUpdateBooking, object: ResponseListener {
            override fun onSuccess(success: Any) {
                mSuccessForUpdate.value = success as Int
            }

            override fun onFailure(failure: Any) {
                mFailureForUpdate.value = failure
            }

        })
    }

    /**
     * function will return the MutableLiveData of Int
     */
    fun returnBookingUpdated(): MutableLiveData<Int> {
        return mSuccessForUpdate
    }

    /**
     * function will return the MutableLiveData of Int if something went wrong at server
     */
    fun returnUpdateFailed(): MutableLiveData<Any> {
        return mFailureForUpdate
    }


    /**
     * -------------------------------------add Local booking-------------------------------------
     */
    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun addFeedback(mFeedback: Feedback) {
        mBookingDetailsForTheDayRepository = BookingDetailsForTheDayRepository.getInstance()
        mBookingDetailsForTheDayRepository!!.addFeedback(mFeedback, object : ResponseListener {
            override fun onFailure(failure: Any) {
                mFailureCodeForFeedback.value = failure
            }

            override fun onSuccess(success: Any) {
                mSuccessForFeedback.value = success as Int
            }

        })
    }

    /**
     * function will return MutableLiveData of List of EmployeeList
     */
    fun returnSuccessForFeedback(): MutableLiveData<Any> {
        return mSuccessForFeedback
    }

    /**
     * function will return the MutableLiveData of Int if something went wrong at server
     */
    fun returnFailureForFeedback(): MutableLiveData<Any> {
        return mFailureCodeForFeedback
    }


    /**token
     * -------------------------------------unblock room-------------------------------------
     */
    fun unBlockRoom(bookingId: Int) {
        mBookingDetailsForTheDayRepository = BookingDetailsForTheDayRepository.getInstance()
        mBookingDetailsForTheDayRepository!!.unblockRoom(bookingId, object:
            ResponseListener {
            override fun onSuccess(success: Any) {
                mSuccessCodeForBlockRoom.value = success
            }

            override fun onFailure(failure: Any) {
                mFailureCodeForBlockRoom.value = failure
            }

        })
    }

    fun returnSuccessCodeForUnBlockRoom(): MutableLiveData<Any> {
        return mSuccessCodeForBlockRoom
    }
    fun returnFailureCodeForUnBlockRoom(): MutableLiveData<Any> {
        return mFailureCodeForBlockRoom
    }
}