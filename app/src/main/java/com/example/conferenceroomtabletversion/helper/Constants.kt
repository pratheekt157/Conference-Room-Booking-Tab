package com.example.conferenceroomtabletversion.helper

class Constants {
    /**
     * it will provides some static final constants
     */
    companion object {


        /**
         * to check the status of user whether registered or not
         */
        const val EXTRA_REGISTERED = "com.example.conferencerommapp.Activity.EXTRA_REGISTERED"

        /**
         * for set and get intent data
         */
        const val EXTRA_INTENT_DATA = "com.example.conferencerommapp.Activity.EXTRA_INTENT_DATA"

        /**
         * response code for response IsSuccessfull
         */
        const val OK_RESPONSE = 200

        /**
         * building id Name for intents
         */
        const val EXTRA_BUILDING_ID = "com.example.conferencerommapp.Activity.EXTRA_BUILDING_ID"

        /**
         * ip address for api call
         */
        var IP_ADDRESS = "http://192.168.1.189/CRB/"

        const val SOME_EXCEPTION = 400

        const val Facility_Manager = 13

        const val HR_CODE = 11

        const val MANAGER_CODE = 12

        const val EMPLOYEE_CODE = 10

        const val BOOKING_DASHBOARD_TYPE_UPCOMING = "upcoming"

        const val BOOKING_DASHBOARD_TYPE_PREVIOUS = "previous"

        const val BOOKING_DASHBOARD_TYPE_CANCELLED = "cancelled"

        const val BOOKING_DASHBOARD_TAGGED = "Tagged"

        const val INVALID_TOKEN = 401

        const val NOT_ACCEPTABLE = 406

        const val NO_CONTENT_FOUND = 204

        const val NOT_FOUND = 400

        const val SUCCESSFULLY_CREATED = 201

        const val INTERNAL_SERVER_ERROR = 500

        const val MATCHER = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$"

        const val UNAVAILABLE_SLOT = 409

        const val RES_CODE = 200

        const val RES_CODE2 = 201

        const val RES_CODE3 = 202

        const val RES_CODE4 = 203

        const val PREFERENCE = "PREFERENCE"

        const val ROOM_ID = "roomID"

        const val BUILDING_ID = "buildingID"

        const val BUILDING_NAME = "buildingName"

        const val CAPACITY = "capacity"

        const val ROOM_NAME = "roomName"



    }
}