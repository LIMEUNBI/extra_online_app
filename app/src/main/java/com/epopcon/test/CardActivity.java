package com.epopcon.test;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
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
import com.epopcon.extra.card.CardUsageInquiry;
import com.epopcon.extra.card.CardUsageInquiryHandler;
import com.epopcon.extra.card.impl.CardUsageInquiryImpl;
import com.epopcon.extra.card.model.CardUsageDetail;
import com.epopcon.test.logger.Log;
import com.epopcon.test.logger.LogFragment;
import com.epopcon.test.logger.LogView;
import com.epopcon.test.logger.LogWrapper;
import com.epopcon.test.logger.MessageOnlyLogFilter;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardActivity extends AppCompatActivity {

    private final String TAG = "CardActivity";

    private Context context;

    private LogFragment mLogFragment;

    private CardUsageInquiry inquiry;
    private CardUsageInquiryHandler handler;
    private List<CardUsageDetail> cardUsageDetails = new ArrayList<>();

    private final String CARD_BILL_SUMMARY = "CARD_BILL_SUMMARY";

    private ExpandableListView mListView;
    private ArrayList<String> mGroupList = new ArrayList<>();
    private ArrayList<ArrayList<CardConstant>> mChildList = new ArrayList<>();
    private ArrayList<CardConstant> mCardCompanyList = new ArrayList<>();
    private ExpandableAdapter mListAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        mListView = (ExpandableListView) findViewById(R.id.list_view);
        mListAdapter = new ExpandableAdapter(this, mGroupList, mChildList);
        mListView.setAdapter(mListAdapter);

        mGroupList.add("카드사");

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
                mGroupList.add(mCardCompanyList.get(i1).name());
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
        checkPermission();

        initializeLogging();
        initializeApp();
        refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    public void refresh() {
        mCardCompanyList.clear();
        mChildList.clear();

        mCardCompanyList.add(CardConstant.HYUNDAI_CARD);
        mCardCompanyList.add(CardConstant.SHINHAN_CARD);
        mCardCompanyList.add(CardConstant.SAMSUNG_CARD);
        mCardCompanyList.add(CardConstant.BC_CARD);
        mCardCompanyList.add(CardConstant.KB_CARD);
        mCardCompanyList.add(CardConstant.HANA_CARD);
        mCardCompanyList.add(CardConstant.LOTTE_CARD);
        mCardCompanyList.add(CardConstant.NH_CARD);

        mChildList.add(mCardCompanyList);
        mChildList.add(mCardCompanyList);
        mChildList.add(mCardCompanyList);
        mChildList.add(mCardCompanyList);
        mChildList.add(mCardCompanyList);
        mChildList.add(mCardCompanyList);
        mChildList.add(mCardCompanyList);
        mChildList.add(mCardCompanyList);

        String cardCompany = mGroupList.get(0);
        inquiry = CardUsageInquiryImpl.newInstance(context, CardConstant.KB_CARD, handler);
        //ExtraConfig.LOG_REPORT = false;
        //com.epopcon.extra.common.LogWrapper.setConsoleOutput(false);

        if (cardCompany.equals("HYUNDAI_CARD")) {
            inquiry = CardUsageInquiryImpl.newInstance(context, CardConstant.HYUNDAI_CARD, handler);
            ((EditText) findViewById(R.id.id)).setText("lmu0000");
            ((EditText) findViewById(R.id.password)).setText("alsdnd1423!");
        } else if (cardCompany.equals("SHINHAN_CARD")) {
            inquiry = CardUsageInquiryImpl.newInstance(context, CardConstant.SHINHAN_CARD, handler);
            ((EditText) findViewById(R.id.id)).setText("lmu0000");
            ((EditText) findViewById(R.id.password)).setText("alsdnd1423!");
        } else if (cardCompany.equals("SAMSUNG_CARD")) {
            inquiry = CardUsageInquiryImpl.newInstance(context, CardConstant.SAMSUNG_CARD, handler);
            ((EditText) findViewById(R.id.id)).setText("limeunbi");
            ((EditText) findViewById(R.id.password)).setText("dmsql0302^^");
        } else if (cardCompany.equals("BC_CARD")) {
            inquiry = CardUsageInquiryImpl.newInstance(context, CardConstant.BC_CARD, handler);
            ((EditText) findViewById(R.id.id)).setText("limeunbi");
            ((EditText) findViewById(R.id.password)).setText("dmsql0302^^");
        } else if (cardCompany.equals("KB_CARD")) {
            inquiry = CardUsageInquiryImpl.newInstance(context, CardConstant.KB_CARD, handler);
            ((EditText) findViewById(R.id.id)).setText("limeunbi0306");
            ((EditText) findViewById(R.id.password)).setText("dmsql0302");
        } else if (cardCompany.equals("HANA_CARD")) {
            inquiry = CardUsageInquiryImpl.newInstance(context, CardConstant.HANA_CARD, handler);
            ((EditText) findViewById(R.id.id)).setText("MANGO519");
            ((EditText) findViewById(R.id.password)).setText("Epop0313#");
        } else if (cardCompany.equals("LOTTE_CARD")) {
            inquiry = CardUsageInquiryImpl.newInstance(context, CardConstant.LOTTE_CARD, handler);
            ((EditText) findViewById(R.id.id)).setText("LMU0000");
            ((EditText) findViewById(R.id.password)).setText("alsdnd1423!");
        } else if (cardCompany.equals("NH_CARD")) {
            inquiry = CardUsageInquiryImpl.newInstance(context, CardConstant.NH_CARD, handler);
            ((EditText) findViewById(R.id.id)).setText("tkcho05243");
            ((EditText) findViewById(R.id.password)).setText("didtksqkr0!");
        }

        mListAdapter.notifyDataSetChanged();
    }

    public class ExpandableAdapter extends BaseExpandableListAdapter {
        private ArrayList<String> groupList = null;
        private ArrayList<ArrayList<CardConstant>> childList = null;
        private LayoutInflater inflater = null;

        public ExpandableAdapter(Context c, ArrayList<String> groupList, ArrayList<ArrayList<CardConstant>> childList) {
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
            ViewHolder viewHolder;

            if (view == null) {
                viewHolder = new ViewHolder();
                view = inflater.inflate(R.layout.list_row, viewGroup, false);
                viewHolder.groupName = (TextView) view.findViewById(R.id.group_name);
                viewHolder.childName = (TextView) view.findViewById(R.id.child_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.groupName.setText(getGroup(i).toString());
            viewHolder.childName.setVisibility(View.GONE);

            return view;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {

            ViewHolder  viewHolder = new ViewHolder();
            if (view == null) {
                view = inflater.inflate(R.layout.list_row, null);
                viewHolder.groupName = (TextView) view.findViewById(R.id.group_name);
                viewHolder.childName = (TextView) view.findViewById(R.id.child_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
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

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "Read/Write external storage", Toast.LENGTH_SHORT).show();
                }

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
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

            handler = new CardUsageInquiryHandler() {
                @Override
                public void onEndAction(int action, boolean success, PException exception) {
                    if (success) {
                        Log.d(TAG, "onEndAction -> " + success + ", action : " + action + ", message : " + exception);
                    } else {

                    }
                }

                @Override
                public boolean onQueryCardUsageDetails(boolean success, int page, List<CardUsageDetail> list, PException exception) {
                    Log.d(TAG, "onQueryCardUsageDetails -> " + success + ", page : " + page + ", message : " + exception);
                    if (success) {
                        for (CardUsageDetail detail : list) {
                            Log.d(TAG, detail.toString());
                            //Log.d(TAG, detail.getMerchantDetail().toString());
                        }

                        cardUsageDetails.addAll(list);

                        if (page == -1) {
                            for (Object map : handler.getObject(CARD_BILL_SUMMARY, Collections.EMPTY_LIST)) {
                                Log.d(TAG, map.toString());
                            }
                        }
                    }
                    return true;
                }

                @Override
                public boolean onQueryMerchantDetails(boolean success, int i, CardUsageDetail cardUsageDetail, PException exception) {
                    if (i > -1)
                        Log.d(TAG, cardUsageDetail.getMerchantDetail().toString());
                    return true;
                }
            };

//            ((EditText) findViewById(R.id.id)).setText("kim63826382");
//            ((EditText) findViewById(R.id.password)).setText("epopcon!@#");
            //((EditText) findViewById(R.id.id)).setText("tkcho05243");
            //((EditText) findViewById(R.id.password)).setText("jomosi00");
            //((EditText) findViewById(R.id.password)).setText("jOmosi0!");
            //((EditText) findViewById(R.id.id)).setText("downnike82");
            //((EditText) findViewById(R.id.password)).setText("dlrbgkr1!");
            //((EditText) findViewById(R.id.id)).setText("lmu0000");
            //((EditText) findViewById(R.id.password)).setText("alsdnd1!2@3#");
            //((EditText) findViewById(R.id.id)).setText("downnike82");
            //((EditText) findViewById(R.id.password)).setText("dlrbgkr1!");
            //((EditText) findViewById(R.id.id)).setText("entasis1");
            //((EditText) findViewById(R.id.password)).setText("!shlee1122");
            //((EditText) findViewById(R.id.id)).setText("entasis");
            //((EditText) findViewById(R.id.password)).setText("shlee1122");
            //((EditText) findViewById(R.id.id)).setText("downnike");
            //((EditText) findViewById(R.id.password)).setText("dlrbgkr1204!");

            //((EditText) findViewById(R.id.id)).setText("sundol0910");
            //((EditText) findViewById(R.id.password)).setText("dygks&521024");

            //((EditText) findViewById(R.id.id)).setText("LKH820");
            //((EditText) findViewById(R.id.password)).setText("dlrbgkr1");

            //((EditText) findViewById(R.id.id)).setText("munman77");
            //((EditText) findViewById(R.id.password)).setText("bmhwan1q");

            //((EditText) findViewById(R.id.id)).setText("limeunbi");
            //((EditText) findViewById(R.id.password)).setText("dmsql0302^^");

            //((EditText) findViewById(R.id.id)).setText("yongjae89");
            //((EditText) findViewById(R.id.password)).setText("arki1989!");

            //((EditText) findViewById(R.id.id)).setText("zururux0");
            //((EditText) findViewById(R.id.password)).setText("alsk1029!");

            //((EditText) findViewById(R.id.id)).setText("TKCHO05243");
            //((EditText) findViewById(R.id.password)).setText("didtksqkr0!");

            //CardUsageDetail cardUsageDetail = new CardUsageDetail();
            //MerchantDetail merchantDetail = new MerchantDetail();

            //merchantDetail.setMerchantNumber("207829");

            //cardUsageDetail.setMerchantDetail(merchantDetail);

            //cardUsageDetails.add(cardUsageDetail);


        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public void showPreference(View v) {
        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);

        Log.d(TAG, prefs.getAll().toString());
    }

    public void tryLogin(View v) {
        try {
            String id = ((EditText) findViewById(R.id.id)).getText().toString();
            String password = ((EditText) findViewById(R.id.password)).getText().toString();

            if (TextUtils.isEmpty(id) || TextUtils.isEmpty(password)) {
                Toast.makeText(context, "ID 와 Password 를 확인하세요.", Toast.LENGTH_LONG).show();
                return;
            }

            Log.d(TAG, "id -> " + id + ", password -> " + password);

            inquiry.removeIdAndPassword();
            inquiry.storeIdAndPassword(id, password);
            inquiry.tryLoginIfNotAuthenticated();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }


    public void queryOrderDetails(View v) {
        try {
            cardUsageDetails.clear();
            inquiry.queryCardUsageDetails(CardConstant.PERIOD_MAX, false);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public void queryTrackingShipment(View v) {
        try {
            inquiry.queryMerchantDetails(cardUsageDetails);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public void clearView(View v) {
        LogView view = mLogFragment.getLogView();
        view.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (inquiry != null) {
            inquiry.destory();
        }
    }
}
