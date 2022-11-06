package com.example.subway_alarm.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.widget.Toast
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.example.subway_alarm.databinding.FragmentMainBinding
import com.example.subway_alarm.ui.activities.MainActivity
import com.example.subway_alarm.viewModel.ViewModelImpl
import org.koin.android.ext.android.bind
import org.koin.android.viewmodel.ext.android.viewModel
import com.example.subway_alarm.viewModel.ViewModelImpl.direction

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class MainFragment : Fragment() {
    var binding: FragmentMainBinding? = null
    val viewModel: ViewModelImpl by viewModel()
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater, container, false)
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager

        /* View Model과 View 연결 */
        viewModel.apis.observe(viewLifecycleOwner, Observer {
            var data = ""
            for( model in it) {
                data += "${model.bstatnNm}|${model.trainLineNm}|${model.arvlMsg2}\n"
            }
            binding?.txtStationData?.text = data
        })

        viewModel.curStation.observe(viewLifecycleOwner, Observer {
            binding?.txtStationName?.text = it.stationName
            binding?.txtLeftStation?.text = it.leftStation?.stationName?:"역 정보 없음"
            binding?.txtRightStation?.text = it.rightStation?.stationName?:"역 정보 없음"
        })

        //뒤로 버튼 클릭시 이벤트
        binding?.btnBack?.setOnClickListener {
            fragmentManager.beginTransaction().remove(this).commit()
            fragmentManager.popBackStack()
        }

        // 왼쪽 역 클릭시 이벤트
        binding?.btnLeft?.setOnClickListener {
            println("left button!")
            val array: Array<String>? = viewModel.isCrossedLine("left")
            if(array == null)
                viewModel.gotoStation(direction.LEFT)
            else{
                var index: Int = 0
                val builder = AlertDialog.Builder(this.activity)
                builder
                    .setTitle("Selct Station")
                    .setItems(array){dialog, which->
                        Toast.makeText(this.activity, "${array[which]} is Selected",Toast.LENGTH_SHORT).show()
                        index = which
                        println("index : $index")
                        viewModel.gotoStation(direction.LEFT, index)
                    }.show()

            }
        }

        //오른쪽 역 클릭시 이벤트
        binding?.btnRight?.setOnClickListener {
            println("right button!")
            val array: Array<String>? = viewModel.isCrossedLine("right")
            if(array == null)
                viewModel.gotoStation(direction.RIGHT)
            else{
                var index: Int = 1
                val builder = AlertDialog.Builder(this.activity)
                builder
                    .setTitle("Selct Station")
                    .setItems(array){dialog, which->
                        Toast.makeText(this.activity, "${array[which]} is Selected",Toast.LENGTH_SHORT).show()
                        index = which
                        println("index : $index")
                        viewModel.gotoStation(direction.RIGHT,index)
                    }.show()

            }
        }

        //알람 버튼 클릭시 이벤트
        binding?.btnAlarm?.setOnClickListener {
            viewModel.setAlarm()
        }

        viewModel.setStation("당산")
        viewModel.getService("당산")

        return binding?.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}