package com.tencent.medianote.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.tencent.medianote.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnMenuFragmentListener} interface
 * to handle interaction events.
 * Use the {@link MenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuFragment extends Fragment {

    private static final String TAG = MenuFragment.class.getSimpleName();

    private GridView menuGridView;

    private OnMenuFragmentListener mListener;


    private List<String> menuList;
    private MenuAdapter adapter;
    private Map<String,Class> fragmentMap;

    public MenuFragment() {
    }


    // TODO: Rename and change types and number of parameters
    public static MenuFragment newInstance() {
        MenuFragment fragment = new MenuFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        menuList = new ArrayList<>();
        fragmentMap = new LinkedHashMap<>();

        menuList.add("图像处理");
        menuList.add("音频处理");
        menuList.add("视频处理");
        menuList.add("音视频处理");

        fragmentMap.put("图像处理",ImageDrawingFragment.class);
        fragmentMap.put("音频处理",AudioFragment.class);
        fragmentMap.put("视频处理",CameraFragment.class);
        fragmentMap.put("音视频处理",MediaFragment.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        menuGridView = view.findViewById(R.id.grid_view_menu);
        menuGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG,"onItemClick " + position);

                String menu = menuList.get(position);

                Log.d(TAG,"click = " + menu);

                mListener.onMenuClick(menu,fragmentMap.get(menu));
            }
        });
        adapter = new MenuAdapter();
        menuGridView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMenuFragmentListener) {
            mListener = (OnMenuFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnMenuFragmentListener {
        void onMenuClick(String menu,Class c);
    }

    protected class MenuAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return menuList.size();
        }

        @Override
        public String getItem(int position) {
            return menuList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_menu,parent,false);
            }
            TextView menu = convertView.findViewById(R.id.menu);
            menu.setText(getItem(position));

            return convertView;
        }
    }
}
