package com.example.conferenceroomtabletversion.service

interface ResponseListener {

    /**
     * interface method for positive response from server which will take any type(generic) of value aas argument
     */
    fun onSuccess(success : Any)

    /**
     * interface method for negative response from server which will take response code as argument
     */
    fun onFailure(failure : Any)
}