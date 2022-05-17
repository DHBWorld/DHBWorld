package com.main.dhbworld.Dualis;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.main.dhbworld.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class DualisDocumentFragment extends Fragment implements DualisAPI.DocumentsLoadedListener, DualisAPI.DocumentsErrorListener {

    private final String arguments;
    private final List<HttpCookie> cookies;

    private static CircularProgressIndicator mainProgressIndicator;
    private static LinearLayout mainLayout;
    private RecyclerView documentsRecyclerView;
    private static DualisAPI dualisAPI;
    private static CookieHandler cookieHandler;

    private View mainView;

    public DualisDocumentFragment(String arguments, List<HttpCookie> cookies) {
        this.arguments = arguments;
        this.cookies = cookies;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainView = this.getView();

        mainProgressIndicator = mainView.findViewById(R.id.progress_main);
        mainLayout = mainView.findViewById(R.id.main_layout);
        documentsRecyclerView = mainView.findViewById(R.id.documents_list_view);
        documentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        documentsRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        dualisAPI = new DualisAPI();
        dualisAPI.setDocumentsLoadedListener(this);
        dualisAPI.setDocumentsErrorListener(this);

        CookieManager cookieManager = new CookieManager();
        try {
            cookieManager.getCookieStore().add(new URI("dualis.dhbw.de"), cookies.get(0));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        cookieHandler = CookieManager.getDefault();

        dualisAPI.makeDocumentsRequest(getContext(), arguments, cookieHandler);
    }

    static void makeDocumentsRequest(Context context, String arguments) {
        hideLayout();
        dualisAPI.makeDocumentsRequest(context, arguments, cookieHandler);
    }

    private static void showLayout() {
        mainLayout.setVisibility(View.VISIBLE);
        mainProgressIndicator.setVisibility(View.GONE);
    }

    private static void hideLayout() {
        mainLayout.setVisibility(View.INVISIBLE);
        mainProgressIndicator.setVisibility(View.VISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dualis_documents_view, container, false);
    }

    @Override
    public void onDocumentsLoaded(JSONObject jsonObject) {
        ArrayList<DualisDocument> dualisDocuments = new ArrayList<>();
        try {
            JSONArray documentsArray = jsonObject.getJSONArray("documents");
            for (int i=0; i<documentsArray.length(); i++) {
                JSONObject documentObject = documentsArray.getJSONObject(i);

                String name = documentObject.getString("name");
                String date = documentObject.getString("date");
                String url = documentObject.getString("url");

                dualisDocuments.add(new DualisDocument(name, date, url));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        DualisDocumentAdapter adapter = new DualisDocumentAdapter(getContext(), dualisDocuments);
        documentsRecyclerView.setAdapter(adapter);

        showLayout();

    }

    @Override
    public void onDocumentsError(VolleyError error) {
        Toast.makeText(getContext(), "Error: " + error.toString(), Toast.LENGTH_LONG).show();
    }
}
