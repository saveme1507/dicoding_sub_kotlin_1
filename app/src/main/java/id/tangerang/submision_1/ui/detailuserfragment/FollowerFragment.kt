package id.tangerang.submision_1.ui.detailuserfragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import id.tangerang.submision_1.ui.detailuser.USERNAME
import id.tangerang.submision_1.databinding.FollowerFragmentBinding
import id.tangerang.submision_1.utils.ApiRequest
import id.tangerang.submision_1.adapters.UserAdapter
import id.tangerang.submision_1.models.UserModel

class FollowerFragment : Fragment() {
    private lateinit var binding: FollowerFragmentBinding
    private lateinit var viewModel: FollowerViewModel
    private lateinit var userAdapter: UserAdapter
    private var users: MutableList<UserModel> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FollowerFragmentBinding.inflate(layoutInflater, container, false)

        userAdapter = UserAdapter(requireContext(), users)
        binding.rvList.adapter = userAdapter
        binding.rvList.layoutManager = LinearLayoutManager(context)

        userAdapter.setOnClickItem(object : UserAdapter.IOnClickItem{
            override fun onClick(position: Int) {

            }
        })

        viewModel = ViewModelProvider(this,  ViewModelProvider.AndroidViewModelFactory(requireActivity().application)).get(FollowerViewModel::class.java)
        viewModel.setApiRequest(ApiRequest(requireContext()))

        arguments?.getString(USERNAME)?.let { viewModel.loadUsers(it) }

        viewModel.getUsers().observe(requireActivity(), {
            users.clear()
            this.users.addAll(it)
            userAdapter.notifyDataSetChanged()

            if (binding.swipe.isRefreshing) binding.swipe.isRefreshing = false
            binding.progress.visibility = View.GONE
        })

        binding.swipe.setOnRefreshListener {
            arguments?.getString(USERNAME)?.let { viewModel.loadUsers(it) }
        }

        return binding.root
    }
}