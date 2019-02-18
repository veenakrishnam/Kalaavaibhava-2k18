package com.veenam.kalaavaibhava2k18.kalaavaibhava2k18

import java.io.Serializable

data class HttpMessage(
    val error: Boolean?,
    val message: String?
)

data class EventsData(
    val id: Int?,
    val title: String?,
    val date: String?,
    val time: String?,
    val imageUrl: String?
): Serializable

data class EventData(
    val id: Int?,
    val title: String?,
    val imageUrl: String?,
    val date: String?,
    val time: String?,
    val quote: String?,
    val rules: String?,
    val student_co_ordinators: String?,
    val faculty_co_ordinators: String?
):Serializable

data class SponsorsData(
    val id: Int?,
    val name: String?,
    val imageUrl: String?
): Serializable

data class CrewsData(
    val id: Int?,
    val name: String?,
    val semester_department: String?,
    val contactNumber: String?,
    val email: String?,
    val imageUrl: String?
): Serializable

data class ProfileData(
    var name: String?,
    var email: String?,
    var phoneNumber: String?,
    var college: String?
): Serializable

data class VerifyRegisteredUserData(
    var user_email: String?
): Serializable