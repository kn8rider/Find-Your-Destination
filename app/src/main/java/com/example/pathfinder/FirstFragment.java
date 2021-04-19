
package com.example.pathfinder;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment {
    private List<CsvData> data = new ArrayList<>();
    private List<CsvData> backup = new ArrayList<>();
    private RecyclerView recView;
    private  Adapter adapter;
    private SearchView searchView;
    public String city;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_first, container, false);
        recView = myView.findViewById(R.id.recView);
        searchView = ((MapActivity2)getActivity()).searchView;
        city = ((MapActivity2)getActivity()).city;
        recView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //fill data to the list
        fill_data_to_List();
        adapter = new Adapter(data,backup,getActivity());
        recView.setAdapter(adapter);
        if(city.length()==0){ recView.setVisibility(View.INVISIBLE);
        }
        return myView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                ((MapActivity2)getActivity()).add_map_fragment();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s.length()>0){
                    recView.setVisibility(View.VISIBLE);
                }
                city=s;
                adapter.getFilter().filter(s);
                return false;
            }
        });
    }

    // create Adapter class
    public class Adapter extends RecyclerView.Adapter<ViewHolder>  implements Filterable {
        List<CsvData> dataList;
        List<CsvData> backup;
        Context context;

        public Adapter(List<CsvData> dataList, List<CsvData> backup, Context context) {
            this.dataList = dataList;
            this.backup = backup;
            this.context = context;
        }

        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.single_row,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position)  {
            final CsvData temp = dataList.get(position);
            holder.name.setText(dataList.get(position).getCity()+", "+dataList.get(position).getState()+", "+dataList.get(position).getCountry());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = temp.getCity()+", "+temp.getState()+", "+temp.getCountry();
                   // ((MapActivity2)getActivity()).remove_fragment();
                    searchView.setQuery(name,false);
                    ((MapActivity2)getActivity()).add_map_fragment();

                }
            });

        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        @Override
        public Filter getFilter() {
            return filter;
        }
        //
        Filter filter = new Filter() {
            @Override
            //Background Thread_________________________

            protected FilterResults performFiltering(CharSequence key) {
                ArrayList<CsvData> filterData = new ArrayList<>();

                if(key.toString().isEmpty()) {
//                    filterData.addAll(dataList);
                }
                else
                {
                    for(CsvData obj : backup)
                    {    String fullName = obj.getCity().toLowerCase()+" "+obj.getState().toLowerCase()+" "+obj.getCountry().toLowerCase();
                        if(fullName.contains(key.toString().toLowerCase()))
                            filterData.add(obj);
                    }
                }
                FilterResults results = new FilterResults();
                results.values= filterData;
                return results;
            }
            //UI Thread_________

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                dataList.clear();
                dataList.addAll((ArrayList<CsvData>)results.values);
                notifyDataSetChanged();
            }
        };

    }
    //create a method to fill data in the List
    public void fill_data_to_List(){
        InputStream is = getResources().openRawResource(R.raw.city_name);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF_8"))
        );

        String line = "";
        try {
            while ((line = reader.readLine())!=null){

                //split by ','
                String[] tokens = line.split(",");

                //Read data and add to the arrayList

                CsvData csvData = new CsvData();
                csvData.setCity(tokens[0]);
                csvData.setState(tokens[1]);
                csvData.setCountry(tokens[2]);
                data.add(csvData);
                backup.add(csvData);
            }
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

}