package com.application.skuadassignment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.efx.networking.ApiService
import com.application.skuadassignment.adapter.RecyclerViewGenricAdapter
import com.application.skuadassignment.common.RestaurantModel
import com.application.skuadassignment.databinding.ItemRestLayoutBinding
import com.application.skuadassignment.networking.ApiClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.search_layout.view.*
import java.util.*
import kotlin.collections.ArrayList

class HomeActivity : AppCompatActivity() {


    private var service: ApiService? = null
    private val disposable = CompositeDisposable()

    private var mList  = ArrayList<RestaurantModel.Restaurant>()
    private var mListCopy  = ArrayList<RestaurantModel.Restaurant>()
    private var mAdapter: RecyclerViewGenricAdapter<RestaurantModel.Restaurant, ItemRestLayoutBinding> ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        service = ApiClient().getClient(this).create(ApiService::class.java)

       search.searchET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

                filter(p0.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })

        mAdapter = RecyclerViewGenricAdapter(mList, R.layout.item_rest_layout) { binder, model, _, itemView ->


            binder.nameTxt.text = model.name!!
            binder.addressTxt.text = model.vicinity!!
            binder.rating.rating = model.rating!!

//            if (model.opening_hours!!.open_now!!){
//                binder.status.text = "OPEN NOW"
//            }else{
//                binder.status.text = "CLOSE NOW"
//            }

        }

        val mLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recView.layoutManager = mLayoutManager
        recView.adapter = mAdapter

        getResturants()

    }

    private fun getResturants() {

        val bundle = intent.extras

        disposable.add(
            service!!.getResturants(
                bundle!!.getString("LAT_LNG")!!,
                "2500",
                "restaurant",
                "AIzaSyD0AQBJ_BwInY5Tv_0tqGPJIWL7FcllnH0"
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<RestaurantModel>() {

                    override fun onSuccess(resturants: RestaurantModel) {

                        spin_kit.visibility = View.GONE

                        mList.clear()
                        mListCopy.clear()

                        mList.addAll(resturants.results)
                        mListCopy.addAll(resturants.results)

                        mAdapter!!.notifyDataSetChanged()

                    }

                    override fun onError(e: Throwable) {

                        spin_kit.visibility = View.GONE
                    }
                })
        )
    }

    private fun filter(text: String) {
        val temp: ArrayList<RestaurantModel.Restaurant> = ArrayList()
        for (category in mListCopy) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (category.name.isNullOrEmpty())
                category.name = ""

            if (category.name!!.toLowerCase(Locale.ENGLISH).contains(text.toLowerCase(Locale.ENGLISH))) {
                temp.add(category)
            }
        }
        //update recyclerview
        updateList(temp)
    }

    private fun updateList(list: MutableList<RestaurantModel.Restaurant>) {

        mList.clear()
        mList.addAll(list)
        mAdapter!!.notifyDataSetChanged()
    }
}