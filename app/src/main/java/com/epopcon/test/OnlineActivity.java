package com.epopcon.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.epopcon.core.eums.exception.PException;
import com.epopcon.core.utils.Utils;
import com.epopcon.extra.ExtraLibHelper;
import com.epopcon.extra.card.CardConstant;
import com.epopcon.extra.online.OnlineConstant;
import com.epopcon.extra.online.OnlineDeliveryInquiry;
import com.epopcon.extra.online.OnlineDeliveryInquiryHandler;
import com.epopcon.extra.online.impl.OnlineDeliveryInquiryImpl;
import com.epopcon.extra.online.model.OrderDetail;
import com.epopcon.extra.online.model.ProductDetail;
import com.epopcon.extra.online.model.TrackingShipment;
import com.epopcon.test.logger.Log;
import com.epopcon.test.logger.LogFragment;
import com.epopcon.test.logger.LogView;
import com.epopcon.test.logger.LogWrapper;
import com.epopcon.test.logger.MessageOnlyLogFilter;

import java.util.ArrayList;
import java.util.List;

public class OnlineActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    private Context context;

    private LogFragment mLogFragment;
    private OnlineDeliveryInquiry inquiry;
    private OnlineDeliveryInquiryHandler handler;
    private List<OrderDetail> orderDetails = new ArrayList<>();

    private ExpandableListView mListView;
    private ArrayList<String> mGroupList = new ArrayList<>();
    private ArrayList<ArrayList<OnlineConstant>> mChildList = new ArrayList<>();
    private ArrayList<OnlineConstant> mOnlineStoreList = new ArrayList<>();
    private ExpandableAdapter mListAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);

        mListView = (ExpandableListView) findViewById(R.id.list_view);
        mListAdapter = new ExpandableAdapter(this, mGroupList, mChildList);
        mListView.setAdapter(mListAdapter);

        mGroupList.add("온라인 쇼핑몰");

        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                return false;
            }
        });

        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                mGroupList.clear();
                mGroupList.add(mOnlineStoreList.get(i1).name());
                expandableListView.collapseGroup(0);
                refresh();
                return false;
            }
        });

        mListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int i) {

            }
        });

        mListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int i) {

            }
        });

        context = this;

        initializeLogging();
        initializeApp();
        refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void initializeLogging() {
        // Using Log, front-end to the logging chain, emulates
        // android.util.log method signatures.

        // Wraps Android's native log framework
        LogWrapper logWrapper = new LogWrapper();
        Log.setLogNode(logWrapper);

        // A filter that strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        // On screen logging via a fragment with a TextView.
        mLogFragment = (LogFragment) getSupportFragmentManager().findFragmentById(R.id.logFragment);
        msgFilter.setNext(mLogFragment.getLogView());
    }

    private void initializeApp() {

        try {
            ExtraLibHelper.setWebViewUserAgentString(this);
            com.epopcon.extra.common.LogWrapper.setConsoleOutput(true);

            SharedPreferences prefs = getSharedPreferences("KEEPALIVE_INFO", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putString("ePopconId", "eppcn156443d729d6b8d19b3062a72");
            editor.commit();

            Utils.putPrefInt(this, "USER_SALT_VERSION", 2);
            Utils.putPrefString(this, "USER_SALT", "1491993057324");

            handler = new OnlineDeliveryInquiryHandler() {
                @Override
                public void onReadyFailure(int action, PException exception) {
                    Log.d(TAG, "onReadyFailure -> " + exception.getMessage());
                }

                @Override
                public void onReadySuccess(int action) {

                    Log.d(TAG, "onReadySuccess -> " + action);

                    if (action == OnlineConstant.ACTION_TRY_LOGIN) {
                        Log.d(TAG, "id -> " + inquiry.getStoredId() + ", auth -> " + inquiry.isAuthenticated());
                    }
                }

                @Override
                public boolean onQueryOrderDetails(boolean success, int page, List<OrderDetail> list, PException exception) {
                    Log.d(TAG, "onQueryOrderDetails -> " + success + ", page : " + page + ", message : " + exception);
                    if (success) {
                        for (OrderDetail orderDetail : list)
                            Log.d(TAG, orderDetail.toString());
                        orderDetails.addAll(list);
                    }
                    return true;
                }

                @Override
                public void onQueryPaymentDetails(boolean success, List<OrderDetail> list, PException exception) {
                    Log.d(TAG, "onQueryPaymentDetails -> " + success + ", message : " + exception);
                    if (success) {
                        for (OrderDetail orderDetail : list)
                            Log.d(TAG, orderDetail.toString());
                    }
                }

                @Override
                public void onQueryTrackingShipment(boolean success, TrackingShipment trackingShipment, PException exception) {
                    Log.d(TAG, "onQueryTrackingShipment -> " + success + ", message : " + exception);
                    if (success && trackingShipment != null)
                        Log.d(TAG, trackingShipment.toString());
                }
            };
            //inquiry = OnlineDeliveryInquiryImpl.newInstance(context, OnlineConstant.NAVER, handler);
            //inquiry = OnlineDeliveryInquiryImpl.newInstance(context, OnlineConstant.STYLE_NAN_DA, handler);
            //inquiry = OnlineDeliveryInquiryImpl.newInstance(context, OnlineConstant.GMARKET, handler);
            //inquiry = OnlineDeliveryInquiryImpl.newInstance(context, OnlineConstant.HALF_CLUB, handler);
            //inquiry = OnlineDeliveryInquiryImpl.newInstance(context, OnlineConstant.AUCTION, handler);
            //inquiry = OnlineDeliveryInquiryImpl.newInstance(context, OnlineConstant.TMON, handler);
            //inquiry = OnlineDeliveryInquiryImpl.newInstance(context, OnlineConstant.WEMAP, handler);
            //inquiry = OnlineDeliveryInquiryImpl.newInstance(context, OnlineConstant.AK_MALL, handler);
            //inquiry = OnlineDeliveryInquiryImpl.newInstance(context, OnlineConstant.KYOBO_BOOK, handler);
//            inquiry = OnlineDeliveryInquiryImpl.newInstance(context, OnlineConstant.GS_SHOP, handler);

            //((EditText) findViewById(R.id.id)).setText("downnike");
            //((EditText) findViewById(R.id.password)).setText("epop0313");

            //((EditText) findViewById(R.id.id)).setText("nikeo0o");
            //((EditText) findViewById(R.id.password)).setText("dlrbgkr1204!");

            //((EditText) findViewById(R.id.id)).setText("downnike");
            //((EditText) findViewById(R.id.password)).setText("epop0313");

            //((EditText) findViewById(R.id.id)).setText("nutriwmj1");
            //((EditText) findViewById(R.id.password)).setText("wonminjung0!");

//            ((EditText) findViewById(R.id.id)).setText("vkvkdlrjf");
//            ((EditText) findViewById(R.id.password)).setText("alsk1029");

            //inquiry.storeIdAndPassword("k15210", "aa*^^*27");

            //inquiry.storeIdAndPassword("sanghyuk.yi@gmail.com", "ysh1122");
            //inquiry.storeIdAndPassword("downnike", "Dlrbgkr1204");
            //inquiry.storeIdAndPassword("tkcho05243", "didtksqkr0!");
            //inquiry.storeIdAndPassword("downnike", "epop0313");
            //inquiry.storeIdAndPassword("k15210", "rhoa87");
            //inquiry.storeIdAndPassword("nikeo0o", "dlrbgkr1204!");
            //inquiry.storeIdAndPassword("hm10827@hanmail.net", "rhgoa0327");
            //inquiry.storeIdAndPassword("popo7251@naver.com", "conmik512");
            //inquiry.storeIdAndPassword("lmu008", "kaon639018");
            //inquiry.storeIdAndPassword("lmu000", "kaon2963!@");
            //inquiry.storeIdAndPassword("downnike@naver.com", "epop031311");
            //inquiry.storeIdAndPassword("downnike", "epop0313!");
            //inquiry.storeIdAndPassword("kongjuamy", "epop0313");
            //inquiry.storeIdAndPassword("downnike", "dlrbgkr6413!");1
            //inquiry.storeIdAndPassword("nutriwmj1", "wonminjung0!");

            //inquiry.storeIdAndPassword("k15210", "rhgoa0327");
            //inquiry.tryLoginIfNotAuthenticated();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public void refresh() {
        mOnlineStoreList.clear();
        mChildList.clear();

        mOnlineStoreList.add(OnlineConstant.COUPANG);
        mOnlineStoreList.add(OnlineConstant.TMON);
        mOnlineStoreList.add(OnlineConstant.WEMAP);
        mOnlineStoreList.add(OnlineConstant.GMARKET);
        mOnlineStoreList.add(OnlineConstant.AUCTION);
        mOnlineStoreList.add(OnlineConstant._11ST);
        mOnlineStoreList.add(OnlineConstant.NAVER);
        mOnlineStoreList.add(OnlineConstant.INTERPARK);
        mOnlineStoreList.add(OnlineConstant.LOTTE_COM);
        mOnlineStoreList.add(OnlineConstant.SINSEGAE_MALL);
        mOnlineStoreList.add(OnlineConstant.HYUNDAI_MALL);
        mOnlineStoreList.add(OnlineConstant.G9);
        mOnlineStoreList.add(OnlineConstant.AK_MALL);
        mOnlineStoreList.add(OnlineConstant.NS_MALL);
        mOnlineStoreList.add(OnlineConstant.GS_SHOP);
        mOnlineStoreList.add(OnlineConstant.HNS_MALL);
        mOnlineStoreList.add(OnlineConstant.CJ_MALL);
        mOnlineStoreList.add(OnlineConstant.HOME_PLUS);
        mOnlineStoreList.add(OnlineConstant.KURLY);

        mChildList.add(mOnlineStoreList);
        mChildList.add(mOnlineStoreList);
        mChildList.add(mOnlineStoreList);
        mChildList.add(mOnlineStoreList);
        mChildList.add(mOnlineStoreList);
        mChildList.add(mOnlineStoreList);
        mChildList.add(mOnlineStoreList);
        mChildList.add(mOnlineStoreList);
        mChildList.add(mOnlineStoreList);
        mChildList.add(mOnlineStoreList);
        mChildList.add(mOnlineStoreList);
        mChildList.add(mOnlineStoreList);
        mChildList.add(mOnlineStoreList);
        mChildList.add(mOnlineStoreList);
        mChildList.add(mOnlineStoreList);
        mChildList.add(mOnlineStoreList);
        mChildList.add(mOnlineStoreList);
        mChildList.add(mOnlineStoreList);
        mChildList.add(mOnlineStoreList);

        String onlineStore = mGroupList.get(0);
        inquiry = OnlineDeliveryInquiryImpl.newInstance(context, OnlineConstant._11ST, handler);
        //ExtraConfig.LOG_REPORT = false;
        //com.epopcon.extra.common.LogWrapper.setConsoleOutput(false);

        if (onlineStore.equals("COUPANG")) {
            inquiry = OnlineDeliveryInquiryImpl.newInstance(context, OnlineConstant.COUPANG, handler);
            ((EditText) findViewById(R.id.id)).setText("limeunbi@naver.com");
            ((EditText) findViewById(R.id.password)).setText("dmsql8610");
        } else if (onlineStore.equals("TMON")) {
            inquiry = OnlineDeliveryInquiryImpl.newInstance(context, OnlineConstant.TMON, handler);
            ((EditText) findViewById(R.id.id)).setText("lim1222");
            ((EditText) findViewById(R.id.password)).setText("dmsql8610");
        } else if (onlineStore.equals("WEMAP")) {
            inquiry = OnlineDeliveryInquiryImpl.newInstance(context, OnlineConstant.WEMAP, handler);
            ((EditText) findViewById(R.id.id)).setText("limeunbi@naver.com");
            ((EditText) findViewById(R.id.password)).setText("dmsql8610");
        } else if (onlineStore.equals("GMARKET")) {
            inquiry = OnlineDeliveryInquiryImpl.newInstance(context, OnlineConstant.GMARKET, handler);
            ((EditText) findViewById(R.id.id)).setText("limeunbi");
            ((EditText) findViewById(R.id.password)).setText("dmsql8610");
        } else if (onlineStore.equals("AUCTION")) {
            inquiry = OnlineDeliveryInquiryImpl.newInstance(context, OnlineConstant.AUCTION, handler);
            ((EditText) findViewById(R.id.id)).setText("limeunbi");
            ((EditText) findViewById(R.id.password)).setText("dmsql8610");
        } else if (onlineStore.equals("_11ST")) {
            inquiry = OnlineDeliveryInquiryImpl.newInstance(context, OnlineConstant._11ST, handler);
            ((EditText) findViewById(R.id.id)).setText("limeunbi");
            ((EditText) findViewById(R.id.password)).setText("dmsql8610");
        } else if (onlineStore.equals("NAVER")) {
            inquiry = OnlineDeliveryInquiryImpl.newInstance(context, OnlineConstant.NAVER, handler);
            ((EditText) findViewById(R.id.id)).setText("limeunbi");
            ((EditText) findViewById(R.id.password)).setText("dmsql1222^^");
        } else if (onlineStore.equals("INTERPARK")) {
            inquiry = OnlineDeliveryInquiryImpl.newInstance(context, OnlineConstant.INTERPARK, handler);
            ((EditText) findViewById(R.id.id)).setText("lolo4077");
            ((EditText) findViewById(R.id.password)).setText("rhdms883^^");
        } else if (onlineStore.equals("LOTTE_COM")) {
            inquiry = OnlineDeliveryInquiryImpl.newInstance(context, OnlineConstant.LOTTE_COM, handler);
            ((EditText) findViewById(R.id.id)).setText("limeb1222");
            ((EditText) findViewById(R.id.password)).setText("dmsql0302^^");
        } else if (onlineStore.equals("SINSEGAE_MALL")) {
            inquiry = OnlineDeliveryInquiryImpl.newInstance(context, OnlineConstant.SINSEGAE_MALL, handler);
            ((EditText) findViewById(R.id.id)).setText("yongjae89");
            ((EditText) findViewById(R.id.password)).setText("soho5468");
        } else if (onlineStore.equals("HYUNDAI_MALL")) {
            inquiry = OnlineDeliveryInquiryImpl.newInstance(context, OnlineConstant.HYUNDAI_MALL, handler);
            ((EditText) findViewById(R.id.id)).setText("");
            ((EditText) findViewById(R.id.password)).setText("");
        } else if (onlineStore.equals("G9")) {
            inquiry = OnlineDeliveryInquiryImpl.newInstance(context, OnlineConstant.G9, handler);
            ((EditText) findViewById(R.id.id)).setText("limeunbi");
            ((EditText) findViewById(R.id.password)).setText("dmsql8610");
        } else if (onlineStore.equals("AK_MALL")) {
            inquiry = OnlineDeliveryInquiryImpl.newInstance(context, OnlineConstant.AK_MALL, handler);
            ((EditText) findViewById(R.id.id)).setText("limeunbi");
            ((EditText) findViewById(R.id.password)).setText("dmsql0302^^");
        } else if (onlineStore.equals("NS_MALL")) {
            inquiry = OnlineDeliveryInquiryImpl.newInstance(context, OnlineConstant.NS_MALL, handler);
            ((EditText) findViewById(R.id.id)).setText("limeunbi@naver.com");
            ((EditText) findViewById(R.id.password)).setText("dmsql920317");
        } else if (onlineStore.equals("GS_SHOP")) {
            inquiry = OnlineDeliveryInquiryImpl.newInstance(context, OnlineConstant.GS_SHOP, handler);
            ((EditText) findViewById(R.id.id)).setText("yongjae89");
            ((EditText) findViewById(R.id.password)).setText("x34th6ng");
        } else if (onlineStore.equals("HNS_MALL")) {
            inquiry = OnlineDeliveryInquiryImpl.newInstance(context, OnlineConstant.HNS_MALL, handler);
            ((EditText) findViewById(R.id.id)).setText("lim5056");
            ((EditText) findViewById(R.id.password)).setText("lim5056812");
        } else if (onlineStore.equals("CJ_MALL")) {
            inquiry = OnlineDeliveryInquiryImpl.newInstance(context, OnlineConstant.CJ_MALL, handler);
            ((EditText) findViewById(R.id.id)).setText("");
            ((EditText) findViewById(R.id.password)).setText("");
        } else if (onlineStore.equals("HOME_PLUS")) {
            inquiry = OnlineDeliveryInquiryImpl.newInstance(context, OnlineConstant.HOME_PLUS, handler);
            ((EditText) findViewById(R.id.id)).setText("limeunbi");
            ((EditText) findViewById(R.id.password)).setText("dmsql0302^^");
        } else if (onlineStore.equals("KURLY")) {
            inquiry = OnlineDeliveryInquiryImpl.newInstance(context, OnlineConstant.KURLY, handler);
            ((EditText) findViewById(R.id.id)).setText("nutiwmj1");
            ((EditText) findViewById(R.id.password)).setText("wonminjung0!");
        } else {

        }

        mListAdapter.notifyDataSetChanged();
    }

    public class ExpandableAdapter extends BaseExpandableListAdapter {
        private ArrayList<String> groupList = null;
        private ArrayList<ArrayList<OnlineConstant>> childList = null;
        private LayoutInflater inflater = null;

        public ExpandableAdapter(Context c, ArrayList<String> groupList, ArrayList<ArrayList<OnlineConstant>> childList) {
            super();
            this.inflater = LayoutInflater.from(c);
            this.groupList = groupList;
            this.childList = childList;
        }

        @Override
        public int getGroupCount() {
            return groupList.size();
        }

        @Override
        public int getChildrenCount(int i) {
            return childList.size();
        }

        @Override
        public Object getGroup(int i) {
            return groupList.get(i);
        }

        @Override
        public Object getChild(int i, int i1) {
            return childList.get(i).get(i1);
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i1) {
            return i1;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            ExpandableAdapter.ViewHolder viewHolder;

            if (view == null) {
                viewHolder = new ExpandableAdapter.ViewHolder();
                view = inflater.inflate(R.layout.list_row, viewGroup, false);
                viewHolder.groupName = (TextView) view.findViewById(R.id.group_name);
                viewHolder.childName = (TextView) view.findViewById(R.id.child_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ExpandableAdapter.ViewHolder) view.getTag();
            }

            viewHolder.groupName.setText(getGroup(i).toString());
            viewHolder.childName.setVisibility(View.GONE);

            return view;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {

            ExpandableAdapter.ViewHolder viewHolder = new ExpandableAdapter.ViewHolder();
            if (view == null) {
                view = inflater.inflate(R.layout.list_row, null);
                viewHolder.groupName = (TextView) view.findViewById(R.id.group_name);
                viewHolder.childName = (TextView) view.findViewById(R.id.child_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ExpandableAdapter.ViewHolder) view.getTag();
            }

            viewHolder.groupName.setVisibility(View.GONE);
            viewHolder.childName.setText(getChild(i, i1).toString());
            return view;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }

        class ViewHolder {
            public TextView groupName;
            public TextView childName;
        }
    }


    public void showPreference(View v) {
        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);

        Log.d(TAG, prefs.getAll().toString());
    }

    public void tryLogin(View v) {
        showProgress();
        try {
            String id = ((EditText) findViewById(R.id.id)).getText().toString();
            String password = ((EditText) findViewById(R.id.password)).getText().toString();

            Log.d(TAG, "id -> " + id + ", password -> " + password);

            if (TextUtils.isEmpty(id) || TextUtils.isEmpty(password)) {
                Toast.makeText(context, "ID 와 Password 를 확인하세요.", Toast.LENGTH_LONG).show();
                return;
            }

            inquiry.removeIdAndPassword();
            inquiry.storeIdAndPassword(id, password);
            inquiry.tryLoginIfNotAuthenticated();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }


    public void queryOrderDetails(View v) {
        try {
            orderDetails.clear();
            inquiry.queryOrderDetails(OnlineConstant.PERIOD_MAX, "", "");

            /*new Thread(new Runnable() {
                @Override
                public void run() {
                    OrderDetail od = new OrderDetail();
                    ProductDetail pd = new ProductDetail();

                    od.addProductDetails(pd);

                    pd.setProductUrl("https://m.pay.naver.com/inflow/outlink?url=http%3A%2F%2Fstorefarm.naver.com%2Fmain%2Fproducts%2F452186367&accountid=s_519202750490573063&tr=ppc");
                    inquiry.fillOutProductDetails(Arrays.asList(od));
                }
            }).start();*/
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public void queryTrackingShipment(View v) {
        try {
            List<TrackingShipment> list = new ArrayList<>();

            for (OrderDetail orderDetail : orderDetails) {
                for (ProductDetail productDetail : orderDetail.getProductDetails()) {
                    TrackingShipment trackingShipment = new TrackingShipment();
                    trackingShipment.setItemName(productDetail.getProductName());
                    trackingShipment.setTrackingQueryString(productDetail.getTrackingQueryString());

                    list.add(trackingShipment);
                }
            }

            inquiry.queryTrackingShipment(list);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public void clearView(View v) {
        LogView view = mLogFragment.getLogView();
        view.setText("");
    }

    private void showProgress() {
        /*runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setMessage("Please wait a minute..");
                    progressDialog.setIndeterminate(true);
                    progressDialog.setCancelable(false);
                }
                progressDialog.show();
            }
        });*/
    }

    private void hideProgress() {
       /* runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null)
                    progressDialog.hide();
            }
        });*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (inquiry != null) {
            //inquiry.destory();
            inquiry.destory();
        }
    }
}
