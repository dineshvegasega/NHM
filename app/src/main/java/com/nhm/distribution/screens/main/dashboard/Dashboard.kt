package com.nhm.distribution.screens.main.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.google.gson.Gson
import com.nhm.distribution.R
import com.nhm.distribution.databinding.DashboardBinding
import com.nhm.distribution.datastore.DataStoreKeys.LOGIN_DATA
import com.nhm.distribution.datastore.DataStoreUtil.readData
import com.nhm.distribution.models.Login
import com.nhm.distribution.networking.*
import com.nhm.distribution.screens.mainActivity.MainActivity
import com.nhm.distribution.screens.mainActivity.MainActivity.Companion.networkFailed
import com.nhm.distribution.screens.mainActivity.MainActivityVM.Companion.isProductLoad
import com.nhm.distribution.utils.callNetworkDialog
import com.nhm.distribution.utils.getAbbreviatedFromDateTime
import com.nhm.distribution.utils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@AndroidEntryPoint
class Dashboard : Fragment() {
    private val viewModel: DashboardVM by viewModels()
    private var _binding: DashboardBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DashboardBinding.inflate(inflater)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // val menuHost: MenuHost = requireActivity()
        //createMenu(menuHost)
        MainActivity.mainActivity.get()?.callFragment(1)

        binding.apply {
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = viewModel.dashboardAdapter
            viewModel.isScheme.observe(viewLifecycleOwner, Observer {
//                Log.e("TAG","isScheme "+it)
                if (it) {
                    viewModel.itemMain?.get(1)?.apply {
                        isNew = true
                    }
                } else {
                    viewModel.itemMain?.get(1)?.apply {
                        isNew = false
                    }
                }
                viewModel.dashboardAdapter.notifyDataSetChanged()
            })
//            viewModel.isNotice.observe(viewLifecycleOwner, Observer {
//                if (it) {
//                    viewModel.itemMain?.get(2)?.apply {
//                        isNew = true
//                    }
//                } else {
//                    viewModel.itemMain?.get(2)?.apply {
//                        isNew = false
//                    }
//                }
//                viewModel.dashboardAdapter.notifyDataSetChanged()
//            })
//            viewModel.isTraining.observe(viewLifecycleOwner, Observer {
//                if (it) {
//                    viewModel.itemMain?.get(3)?.apply {
//                        isNew = true
//                    }
//                } else {
//                    viewModel.itemMain?.get(3)?.apply {
//                        isNew = false
//                    }
//                }
//                viewModel.dashboardAdapter.notifyDataSetChanged()
//            })
//            viewModel.isComplaintFeedback.observe(viewLifecycleOwner, Observer {
//                if (it) {
//                    viewModel.itemMain?.get(4)?.apply {
//                        isNew = true
//                    }
//                } else {
//                    viewModel.itemMain?.get(4)?.apply {
//                        isNew = false
//                    }
//                }
//                viewModel.dashboardAdapter.notifyDataSetChanged()
//            })
//            viewModel.isInformationCenter.observe(viewLifecycleOwner, Observer {
//                if (it) {
//                    viewModel.itemMain?.get(5)?.apply {
//                        isNew = true
//                    }
//                } else {
//                    viewModel.itemMain?.get(5)?.apply {
//                        isNew = false
//                    }
//                }
//                viewModel.dashboardAdapter.notifyDataSetChanged()
//            })
            viewModel.dashboardAdapter.notifyDataSetChanged()
            viewModel.dashboardAdapter.submitList(viewModel.itemMain)




            if(networkFailed) {
                callApis()
            } else {
                requireContext().callNetworkDialog()
            }

           viewModel.itemLiveSchemesCount.observe(viewLifecycleOwner, Observer {
               textNumberOfNBPA.text = ""+it.meta!!.total_items
           })

           viewModel.itemLiveSchemesMembersCount.observe(viewLifecycleOwner, Observer {
               var jsonObject = JSONObject(it.data!!.toString())
               textNumberOfDistributers.text = ""+jsonObject.getString("total_user")
//               textNumberOfDistributers.text = ""+it.meta!!.total_items
           })

//            viewModel.adsList(view)
//            val adapter = BannerViewPagerAdapter(requireContext())
//
//            viewModel.itemAds.observe(viewLifecycleOwner, Observer {
//                if (it != null) {
//                    viewModel.itemAds.value?.let { it1 ->
//                        adapter.submitData(it1)
//                        banner.adapter = adapter
//                        tabDots.setupWithViewPager(banner, true)
//                        banner.autoScroll()
//                    }
//                }
//            })
        }
    }

    private fun callApis() {
        readData(LOGIN_DATA) { loginUser ->
            if (loginUser != null) {
                val user = Gson().fromJson(loginUser, Login::class.java)
                when (user.status) {
                    "approved" -> {
                        if (user.user_role == USER_TYPE) {
                            binding.linearMember.visibility = View.GONE
                            binding.linearNBPA.visibility = View.VISIBLE

                            val calendarStart = Calendar.getInstance()
                            val dateStart = getAbbreviatedFromDateTime(calendarStart, "yyyy-MM-dd")
                            val calendarEnd = Calendar.getInstance()
                            calendarEnd.add(Calendar.DATE, -7)
                            val dateEnd = getAbbreviatedFromDateTime(calendarEnd, "yyyy-MM-dd")
                            val obj: JSONObject = JSONObject().apply {
                                put(page, "1")
                                put(user_id, user.id)
                                put(filterByStartDate, dateEnd+" 00:00:00")
                                put(filterByEndDate, dateStart+" 23:59:59")
                            }
                            viewModel.liveSchemeCount(obj)
                        } else if (user.user_role == USER_TYPE_ADMIN) {
                            binding.linearMember.visibility = View.VISIBLE
                            binding.linearNBPA.visibility = View.VISIBLE

                            val calendarStart = Calendar.getInstance()
                            val dateStart = getAbbreviatedFromDateTime(calendarStart, "yyyy-MM-dd")
                            val calendarEnd = Calendar.getInstance()
                            calendarEnd.add(Calendar.DATE, -7)
                            val dateEnd = getAbbreviatedFromDateTime(calendarEnd, "yyyy-MM-dd")
                            val obj: JSONObject = JSONObject().apply {
                                put(page, "1")
                                put(filterByStartDate, dateEnd+" 00:00:00")
                                put(filterByEndDate, dateStart+" 23:59:59")
                            }
                            viewModel.liveSchemeCount(obj)

                            val objMember: JSONObject = JSONObject().apply {
                                put(page, "1")
                                put(from_date, dateEnd)
                                put(to_date, dateStart)
                            }
                            viewModel.liveSchemeMembersCount(objMember)
                        }
                    }
                    "unverified" -> {

                    }
                    "pending" -> {

                    }
                    "rejected" -> {

                    }
                }




//                viewModel.liveTraining(view = requireView(), obj)
//                viewModel.liveNotice(view = requireView(), obj)
//                val obj2: JSONObject = JSONObject().apply {
//                    put(user_id, _id)
//                }
//                viewModel.complaintFeedbackHistory(view = requireView(), obj2)
//                viewModel.informationCenter(view = requireView(), obj)
                viewModel.profile(view = requireView(), ""+Gson().fromJson(loginUser, Login::class.java).id)
            }
        }
    }




    override fun onStop() {
        super.onStop()
        isProductLoad = true
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        isProductLoad = false
    }

}