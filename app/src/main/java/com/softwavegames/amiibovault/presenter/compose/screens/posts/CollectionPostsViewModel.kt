package com.softwavegames.amiibovault.presenter.compose.screens.posts

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.softwavegames.amiibovault.domain.util.Constants
import com.softwavegames.amiibovault.domain.util.SharedPrefUtils
import com.softwavegames.amiibovault.model.CollectionPost
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CollectionPostsViewModel @Inject constructor(private val sharedPref: SharedPreferences) :
    ViewModel() {

    private var _collectionPosts = MutableLiveData<List<CollectionPost>?>()
    var collectionPost: LiveData<List<CollectionPost>?> = _collectionPosts

    private var _likedPostsIds = MutableLiveData<List<String>?>()
    var likedPostsIds: LiveData<List<String>?> = _likedPostsIds

    private val database: DatabaseReference = Firebase.database.reference

    init {
        getCollectionPosts()
    }

    private fun getCollectionPosts() {
        val postsList: MutableList<CollectionPost?> = mutableListOf()
        database.child(Constants.FIREBASE_DB_POSTS).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postsList.clear()
                for (postSnapshot in dataSnapshot.children) {
                    val postItem = postSnapshot.getValue(CollectionPost::class.java)
                    postsList.add(postItem)
                }
                _collectionPosts.value = postsList.filterNotNull()
                getLikedPostsIds()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.d("CollectionPosts", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }

    fun getLikedPostsIds() {
        _likedPostsIds.value =
            SharedPrefUtils.getSet(sharedPref, Constants.SHARED_PREFERENCES_LIKES_LIST, emptySet())
                ?.toList() ?: emptyList()
    }

    fun likeDislikePost(postId: String) {
        val likedPostsIds =
            SharedPrefUtils.getSet(sharedPref, Constants.SHARED_PREFERENCES_LIKES_LIST, emptySet())
                ?.toMutableList() ?: mutableListOf()
        if (likedPostsIds.contains(postId)) {
            likedPostsIds.remove(postId)
            SharedPrefUtils.putSet(
                sharedPref,
                Constants.SHARED_PREFERENCES_LIKES_LIST,
                likedPostsIds.toSet()
            )
            updateFirebasePostLikes(postId, false)
            return
        } else {
            likedPostsIds.add(postId)
            SharedPrefUtils.putSet(
                sharedPref,
                Constants.SHARED_PREFERENCES_LIKES_LIST,
                likedPostsIds.toSet()
            )
            updateFirebasePostLikes(postId, true)
        }
    }

    private fun updateFirebasePostLikes(postId: String, isLiked: Boolean) {
        database.child(Constants.FIREBASE_DB_POSTS)
            .orderByChild(Constants.FIREBASE_DB_POSTS_POST_ID)
            .equalTo(postId)
            .addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (likesPost in dataSnapshot.children) {
                        val currentLikes = likesPost.child(Constants.FIREBASE_DB_POSTS_LIKES).value
                        val currentLikesString = currentLikes.toString()
                        if (isLiked) {
                            likesPost.ref.child(Constants.FIREBASE_DB_POSTS_LIKES)
                                .setValue((currentLikesString.toInt() + 1).toString())
                        } else {
                            likesPost.ref.child(Constants.FIREBASE_DB_POSTS_LIKES)
                                .setValue((currentLikesString.toInt() - 1).toString())
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("CollectionPosts", "loadLikes:onCancelled", databaseError.toException())
                }
            })
    }
}