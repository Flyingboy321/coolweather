package com.hhxy.coolweather.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hhxy.coolweather.R;
import com.hhxy.coolweather.db.City;
import com.hhxy.coolweather.db.County;
import com.hhxy.coolweather.db.Province;
import com.hhxy.coolweather.util.HttpUtil;
import com.hhxy.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/4/11.
 */

public class ChooseAreaFragment extends Fragment {
    private static final String TAG = "ChooseAreaFragment";
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    public static int currentLevel;

    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private List<String> dataList;
    private ArrayAdapter<String> adapter;
    private List<Province> provinceList;
    private Province selectedProvince;
    private List<City> cityList;
    private City selectedCity;
    private List<County> countyList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.choose_area,container,false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
//        创建list集合中存储的是当前界面的数据,不过当前数据是空的
        dataList = new ArrayList<>();
//        创建adapter对象
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1, dataList);
//        加载当前页面listview
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        实现listview的点击监听事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                首先要获得当前点击的是哪个省，或哪个市，哪个县，后面再查询的时候得到了一个省或市或县的集合
//                  ，这里可以直接用，然后通过省对象来获得省的编号既可以查询市信息
//                另外如果当前是省的话就查询市
                if (currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
//                下一步查询市通过id
                    queryCity();
                }else if (currentLevel == LEVEL_CITY){
//                    现在代表在市的页面
//                    获取点击了那个市
                    selectedCity = cityList.get(position);
                    queryCounty();

                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY){
                    queryCity();
                }else if (currentLevel == LEVEL_CITY){
                    queryProvince();
                }
            }
        });
//        当activity创建好之后，下一步要来请求数据来填充datalist集合
        queryProvince();
    }

    private void queryCounty() {
//        查询县
        titleText.setText("县/区");
        backButton.setVisibility(View.VISIBLE);
//        从数据库中查询
        countyList = DataSupport.where("cityId = ?",String.valueOf(selectedCity.getCityCode())).find(County.class);
        if (countyList.size()>0){
            Log.e(TAG, "queryCounty: ");
            dataList.clear();
            for (County county: countyList){
                dataList.add(county.getCountyName());
                Log.e(TAG, "queryCounty: "+county.toString());
            }
            adapter.notifyDataSetChanged();
            currentLevel = LEVEL_COUNTY;
        }else {
            Log.e(TAG, "queryCounty: ");
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            Log.e(TAG, "queryCounty: "+provinceCode+"===="+cityCode);

            String address = "http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            Log.e(TAG, "queryCounty: "+address);
            queryFromServer(address,"county");

        }


    }

    private void queryCity() {
        titleText.setText("市");
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceId = ?",String.valueOf(selectedProvince.getId())).find(City.class);
//        判断是否数据为空
        if (cityList.size()>0){
            dataList.clear();
            for (City city: cityList){
                dataList.add(city.getCityName());
                Log.e(TAG, "queryCity: "+city.toString());
            }
            adapter.notifyDataSetChanged();
//            注意在成功之后才将当前页面设置为市
            currentLevel = LEVEL_CITY;

        }else {
//            从网络上请求
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/"+provinceCode;
            queryFromServer(address,"city");
        }


    }

    private void queryProvince() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
//        在这里查询省的信息
//        首先从数据库中查询
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() >0){
//            清除当前list中数据
            dataList.clear();
//            遍历省集合
            for (Province province: provinceList){
                dataList.add(province.getProvinceName());
            }
//            通知adapter重新绘制
            adapter.notifyDataSetChanged();
//            同时将当前页面的标志位变为省
            currentLevel = LEVEL_PROVINCE;
        }else {
//            否则就从网络获取(服务器)
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }

    }

    private void queryFromServer(String address, final String type) {
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                这里表示加载数据失败
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                Log.e(TAG, "onResponse: "+responseText);
                boolean result = false;
                if (type.equals("province")){
//                    解析过程就将数据存到了数据库中,注意在解析过程会有个返回数据如果是true就继续执行
                     result = Utility.handleProvinceResponse(responseText);
                }
                if (type.equals("city")){
                    result = Utility.handleCityResponse(responseText,selectedProvince.getProvinceCode());
                }
                if (type.equals("county")){
                    result = Utility.handleCountyResponse(responseText,selectedCity.getCityCode());
                    Log.e(TAG, "onResponse: 这个是解析返回的result"+result);
                }

                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (type.equals("province")){
//                                切换到主线程中
                                queryProvince();
                            }
                            if (type.equals("city")){
//                                此时数据库中有了数据，就可以在次从数据库中获取
                                queryCity();
                            }
                            if (type.equals("county")){
                                queryCounty();
                            }

                        }
                    });
                }


            }
        });
    }


}
