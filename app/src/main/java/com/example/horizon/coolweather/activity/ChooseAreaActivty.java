package com.example.horizon.coolweather.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.horizon.coolweather.R;
import com.example.horizon.coolweather.db.CoolWeatherDB;
import com.example.horizon.coolweather.model.City;
import com.example.horizon.coolweather.model.County;
import com.example.horizon.coolweather.model.Province;
import com.example.horizon.coolweather.util.HttpCallbackListener;
import com.example.horizon.coolweather.util.HttpUtil;
import com.example.horizon.coolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActivty extends AppCompatActivity {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView tv_title;
    private ListView lv_areas_list;
    private ArrayAdapter<String> adapter;
    private CoolWeatherDB coolWeatherDB;
    //存储各省的名称
    private List<String> dataList = new ArrayList<>();

    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<County> countyList;
    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 选中的城市
     */
    private City selectedCity;
    /**
     * 当前选中的级别
     */
    private int currentLevel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_area);
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        initUi();

        coolWeatherDB = CoolWeatherDB.getInstance(this);
        lv_areas_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryCounties();
                }
            }
        });
        //加载省级数据
        queryProvinces();
    }

    private void initUi() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        lv_areas_list = (ListView) findViewById(R.id.lv_areas_list);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        lv_areas_list.setAdapter(adapter);
    }

    /**
     * 查询全国所有的省，优先从数据库中查找，如果没有查到则去服务器上去查找
     */
    private void queryProvinces() {
        provinceList = coolWeatherDB.loadProvinces();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());

            }
            adapter.notifyDataSetChanged();
            lv_areas_list.setSelection(0);
            tv_title.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else {
            //从服务器查询
            queryFromServer(null, "province");
        }
    }

    /**
     * 查询某一省的所有市，优先从数据库中查找，如果没有查到则去服务器上去查找
     */
    private void queryCities() {
        cityList = coolWeatherDB.loadCities(selectedProvince.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            lv_areas_list.setSelection(0);
            tv_title.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }

    }

    /**
     * 查询某一市的所有县，优先从数据库中查找，如果没有查到则去服务器上去查找
     */
    private void queryCounties() {
        countyList = coolWeatherDB.loadConties(selectedCity.getId());
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }

            adapter.notifyDataSetChanged();
            lv_areas_list.setSelection(0);
            tv_title.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer(selectedCity.getCityCode(), "county");
        }
    }

    /**
     * @param code 省市县的代码
     * @param type 省市县等级
     *             根据传入的代号和类型向服务器查询省市县数据
     */
    private void queryFromServer(final String code, final String type) {
        String address;
        if (!TextUtils.isEmpty(code)) {
            address = "http://guolin.tech/api/china/" + code + ".xml";
        } else {
            address = "http://guolin.tech/api/china";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(coolWeatherDB, response);

                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(coolWeatherDB, response, selectedProvince.getId());

                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(coolWeatherDB, response, selectedCity.getId());

                }

                if (result) {
                    //通过runOnUiThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            //上述操作已经将服务器中的数据存储到数据库中，可以到数据库中查询
                            if ("province".equals(type)) {
                                queryProvinces();

                            } else if ("city".equals(type)) {
                                queryCities();

                            } else if ("county".equals(type)) {
                                queryCounties();

                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                //通过runOnUiThread()方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivty.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }

        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * 捕获back键，根据当前的级别来判断，此时应该返回市列表、省列表，还是直接退出。
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (currentLevel == LEVEL_COUNTY){
            queryCities();
        }else if (currentLevel == LEVEL_CITY){
            queryProvinces();
        }else {
            finish();
        }
    }
}
