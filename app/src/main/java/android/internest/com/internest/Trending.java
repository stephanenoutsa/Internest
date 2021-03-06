package android.internest.com.internest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by stephnoutsa on 6/1/16.
 */
public class Trending extends Fragment {
    List<Scanned> trends;
    ListAdapter listAdapter;
    ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;

    public Trending() {
        // Required empty constructor
    }

    public static Trending newInstance() {
        Trending fragment = new Trending();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_trending, container, false);

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                /**
                 * Get all trending items from server
                 */
                // Trailing slash is needed
                final String BASE_URL = "http://192.168.43.170:8080/tracker/webapi/"; // Localhost value is 10.0.2.2

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                InternestService internestService = retrofit.create(InternestService.class);

                final Call<List<Scanned>> call = internestService.getAllTrends();
                call.clone().enqueue(new retrofit2.Callback<List<Scanned>>() {
                    @Override
                    public void onResponse(Call<List<Scanned>> call, Response<List<Scanned>> response) {
                        int statusCode = response.code();
                        if (statusCode == 200) {
                            trends = response.body();

                            // Check if nothing was returned and notify user
                            if (trends.size() == 0) {
                                Toast.makeText(getContext(), getString(R.string.list_empty), Toast.LENGTH_SHORT).show();
                            }

                            // Get the trending items into an adapter's list
                            listAdapter = new CustomAdapter(getContext(), trends);

                            // Set the adapter to display the scanned items
                            listView = (ListView) rootView.findViewById(R.id.trends);
                            listView.setAdapter(listAdapter);

                            listView.setOnItemClickListener(
                                    new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            Scanned scanned = (Scanned) parent.getItemAtPosition(position);

                                            TextView scanType = (TextView) view.findViewById(R.id.scanType);

                                            String sType = scanType.getText().toString();
                                            final String sDetails = scanned.getSdetails();

                                            if (sType.equals(getString(R.string.scanned_type_text))) {
                                                Intent intent = new Intent(getContext(), TextDisplay.class);
                                                intent.putExtra("text", sDetails);
                                                intent.putExtra("previous", "trending");
                                                startActivity(intent);
                                            } else if (sType.equals(getString(R.string.url_simple))) {
                                                Intent intent = new Intent(getContext(), URLDisplay.class);
                                                intent.putExtra("url", sDetails);
                                                intent.putExtra("previous", "trending");
                                                startActivity(intent);
                                            }
                                        }
                                    }
                            );
                        } else {
                            Toast.makeText(getContext(), "Unable to communicate with server", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Scanned>> call, Throwable t) {
                        Toast.makeText(getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                // Instantiate refreshLayout
                swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);

                // Set color schemes for SwipeRefreshLayout
                swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorAccent),
                        ContextCompat.getColor(getContext(), R.color.colorPrimaryLight),
                        ContextCompat.getColor(getContext(), R.color.colorPrimary),
                        ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));

                /**
                 * Implement {@link SwipeRefreshLayout.OnRefreshListener}. When users do the "swipe to
                 * refresh" gesture, SwipeRefreshLayout invokes
                 * {@link SwipeRefreshLayout.OnRefreshListener#onRefresh onRefresh()}. In
                 * {@link SwipeRefreshLayout.OnRefreshListener#onRefresh onRefresh()}, call a method that
                 * refreshes the content. Call the same method in response to the Refresh action from the
                 * toolbar bar.
                 */
                swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        call.clone().enqueue(new retrofit2.Callback<List<Scanned>>() {
                            @Override
                            public void onResponse(Call<List<Scanned>> call, Response<List<Scanned>> response) {
                                int statusCode = response.code();
                                if (statusCode == 200) {
                                    trends = response.body();
                                    swipeRefreshLayout.setRefreshing(false);
                                    Toast.makeText(getContext(), "List updated", Toast.LENGTH_SHORT).show();

                                    // Check if nothing was returned and notify user
                                    if (trends.size() == 0) {
                                        Toast.makeText(getContext(), getString(R.string.list_empty), Toast.LENGTH_SHORT).show();
                                    }

                                    // Get the trending items into an adapter's list
                                    listAdapter = new CustomAdapter(getContext(), trends);

                                    // Set the adapter to display the scanned items
                                    listView = (ListView) rootView.findViewById(R.id.trends);
                                    listView.setAdapter(listAdapter);

                                    listView.setOnItemClickListener(
                                            new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    Scanned scanned = (Scanned) parent.getItemAtPosition(position);

                                                    TextView scanType = (TextView) view.findViewById(R.id.scanType);

                                                    String sType = scanType.getText().toString();
                                                    final String sDetails = scanned.getSdetails();

                                                    if (sType.equals(getString(R.string.scanned_type_text))) {
                                                        Intent intent = new Intent(getContext(), TextDisplay.class);
                                                        intent.putExtra("text", sDetails);
                                                        intent.putExtra("previous", "trending");
                                                        startActivity(intent);
                                                    } else if (sType.equals(getString(R.string.url_simple))) {
                                                        Intent intent = new Intent(getContext(), URLDisplay.class);
                                                        intent.putExtra("url", sDetails);
                                                        intent.putExtra("previous", "trending");
                                                        startActivity(intent);
                                                    }
                                                }
                                            }
                                    );
                                } else {
                                    swipeRefreshLayout.setRefreshing(false);
                                    Toast.makeText(getContext(), "Unable to communicate with server", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<List<Scanned>> call, Throwable t) {
                                swipeRefreshLayout.setRefreshing(false);
                                Toast.makeText(getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        };

        Runnable r = new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        };
        Thread thread = new Thread(r);
        thread.start();

        return rootView;
    }
}
