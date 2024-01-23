package com.example.instagramclone.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.instagramclone.Models.Post
import com.example.instagramclone.adapters.MyPostRvAdapter
import com.example.instagramclone.databinding.FragmentMyPostBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject


class MyPostFragment : Fragment() {
   private lateinit var binding:FragmentMyPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentMyPostBinding.inflate(inflater, container, false)
        var postList=ArrayList<Post>()
        var adapter=MyPostRvAdapter(requireContext(),postList)
        binding.rv2.layoutManager=StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL)
        binding.rv2.adapter=adapter
        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {
            var tempList= arrayListOf<Post>()
            for (i in it.documents){
               var post:Post=i.toObject<Post>()!!
                tempList.add(post)

            }
            postList.addAll(tempList)
            adapter.notifyDataSetChanged()
        }
        return binding.root
    }

    companion object {

    }
}