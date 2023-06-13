package com.main.dhbworld.Dualis.view.tabs.documents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.main.dhbworld.Dualis.parser.api.DualisAPI;
import com.main.dhbworld.Dualis.parser.htmlparser.documents.DualisDocument;
import com.main.dhbworld.R;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class DualisDocumentFragment extends Fragment implements DualisAPI.DocumentsListener {

    private final String arguments;
    private final List<HttpCookie> cookies;

    private static CircularProgressIndicator mainProgressIndicator;
    private static LinearLayout mainLayout;
    private RecyclerView documentsRecyclerView;
    private static DualisAPI dualisAPI;
    private static CookieHandler cookieHandler;

    private View mainView;

    public DualisDocumentFragment() {
        arguments = "";
        cookies = new ArrayList<>();
    }

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

        cookieHandler = CookieManager.getDefault();
        dualisAPI = new DualisAPI(getContext(), arguments, cookieHandler);

        if (cookies.size() == 0) {
            if (getActivity() != null) {
                Snackbar.make(getActivity().findViewById(android.R.id.content), getResources().getString(R.string.error_getting_kvv_data), BaseTransientBottomBar.LENGTH_SHORT).show();
            }
            return;
        }

        CookieManager cookieManager = new CookieManager();
        try {
            cookieManager.getCookieStore().add(new URI("dualis.dhbw.de"), cookies.get(0));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        dualisAPI.requestDocuments(this);
    }

    public void makeDocumentsRequest() {
        hideLayout();
        dualisAPI.requestDocuments(this);
    }

    private void showLayout() {
        mainLayout.setVisibility(View.VISIBLE);
        mainProgressIndicator.setVisibility(View.GONE);
    }

    private void hideLayout() {
        mainLayout.setVisibility(View.INVISIBLE);
        mainProgressIndicator.setVisibility(View.VISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dualis_documents_view, container, false);
    }

    @Override
    public void onDocumentsLoaded(ArrayList<DualisDocument> dualisDocuments) {
        DualisDocumentAdapter adapter = new DualisDocumentAdapter(getContext(), dualisDocuments);
        documentsRecyclerView.setAdapter(adapter);

        showLayout();
    }

    @Override
    public void onError(Exception error) {
        Snackbar.make(DualisDocumentFragment.this.getActivity().findViewById(android.R.id.content), getString(R.string.error_with_message, error.toString()), BaseTransientBottomBar.LENGTH_LONG).show();
    }
}
