package com.example.conferenceroomtabletversion.helper

class Constants {
    /**
     * it will provides some static final constants
     */
    companion object {

        /**
         * response code for response IsSuccessfull
         */
        const val OK_RESPONSE = 200


        /**
         * ip address for api call
         */
        const val IP_ADDRESS = "http://192.168.0.78/CRB/"

        const val API_REQUEST_TIME: Long = (30 * 1000)

        const val MAX_VALUE_FOR_5_DIGITS = 99999


        const val ROOM_AMINITIES = "Room Amenities"

        const val MIN_15 = 15

        const val MIN_30 = 30

        const val MIN_45 = 45

        const val MIN_60 = 60

        const val MILLIS_60 = (45 * 60 * 1000)

        const val MILLIS_45 = (45 * 60 * 1000)

        const val MILLIS_30 = (30 * 60 * 1000)

        const val MILLIS_15 = (15 * 60 * 1000)



        const val NOT_ACCEPTABLE = 406

        const val NO_CONTENT_FOUND = 204

        const val NOT_FOUND = 400

        const val NOT_FOUND_TAB = 404

        const val SUCCESSFULLY_CREATED = 201

        const val INTERNAL_SERVER_ERROR = 500


        const val UNAVAILABLE_SLOT = 409

        const val RES_CODE = 200

        const val PREFERENCE = "PREFERENCE"

        const val ROOM_ID = "roomID"

        const val BUILDING_ID = "buildingID"

        const val BUILDING_NAME = "buildingName"

        const val CAPACITY = "capacity"

        const val ROOM_NAME = "roomName"

        const val DEFAULT_STRING_PREFERENCE_VALUE = "Not set"

        const val DEFAULT_INT_PREFERENCE_VALUE = -1

        const val ONBORDING = "onboarding"

        const val DEFAULT_ONBOARDING = false

    }
}