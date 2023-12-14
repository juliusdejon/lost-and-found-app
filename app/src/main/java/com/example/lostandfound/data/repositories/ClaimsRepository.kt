package com.example.lostandfound.data.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.lostandfound.models.Case
import com.example.lostandfound.models.Claims
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import java.lang.Exception

class ClaimsRepository(private val context : Context) {

    private val TAG = this.toString()

    private val db = Firebase.firestore

    private val COLLECTION_CLAIMS = "Claims"
    private val FIELD_CASEID = "caseId"
    private val FIELD_EMAILID = "emailId"
    private val FIELD_ID = "id"

    var allClaims : MutableLiveData<List<Claims>> = MutableLiveData<List<Claims>>()

    fun addClaimsToDB(newClaims: Claims){
        // to be implemented
        try{
            val data: MutableMap<String, Any> = HashMap();
            data[FIELD_CASEID] = newClaims.caseId
            data[FIELD_EMAILID] = newClaims.emailId
            data[FIELD_ID] = newClaims.id

            // Add the claim document to the Claims collection
            db.collection(COLLECTION_CLAIMS)
                .add(data)
                .addOnSuccessListener { docRef ->
                    Log.d(TAG, "addClaimToDB: Successfully added to Database with ID: ${docRef.id}")
                }
                .addOnFailureListener { ex ->
                    Log.e(TAG, "addClaimToDB: Exception occurred while adding a claim document: $ex")
                }

        }catch(ex: Exception){
            Log.e(TAG, "addCasetoDB: Couldn't perform insert on Claims collection due to exception $ex", )
        }
    }
    fun retrieveAllClaims() {
        try {
            db.collection(COLLECTION_CLAIMS)
                .addSnapshotListener(EventListener { result, error ->
                    if (error != null) {
                        Log.e(TAG, "retrieveAllClaims: Listening to Claims collection failed due to error: $error")
                        return@EventListener
                    }

                    if (result != null) {
                        Log.d(TAG, "retrieveAllClaims: Number of Documents retrieved: ${result.size()}")

                        val tempList: MutableList<Claims> = ArrayList<Claims>()

                        for (docChanges in result.documentChanges) {
                            val cCaseId = docChanges.document.data[FIELD_CASEID] as String
                            val cEmailId = docChanges.document.data[FIELD_EMAILID] as String
                            val cId = docChanges.document.data[FIELD_ID] as String

                            val claim = Claims(cCaseId, cEmailId, cId)

                            Log.d(TAG, "retrieveAllClaims: Current Document: $claim")

                            when (docChanges.type) {
                                DocumentChange.Type.ADDED -> {
                                    tempList.add(claim)
                                }
                                DocumentChange.Type.MODIFIED -> {}
                                DocumentChange.Type.REMOVED -> {}
                            }
                        }

                        // Update the value in allClaims
                        allClaims.postValue(tempList)
                    } else {
                        Log.d(TAG, "retrieveAllClaims: No data in the result after retrieving")
                    }
                })

        } catch (ex: java.lang.Exception) {
            Log.e(TAG, "retrieveAllClaims: Unable to retrieve all claims: $ex")
        }
    }

    // You can add additional functions for managing claims as needed.
}