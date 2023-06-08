package com.main.dhbworld.Dashboard.DataLoaders;

import android.content.Context;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseUser;
import com.main.dhbworld.Enums.InteractionState;
import com.main.dhbworld.Firebase.SignedInListener;
import com.main.dhbworld.Firebase.Utilities;
import com.main.dhbworld.R;

public class DataLoaderUserInt {
    Context context;
    ImageView image_canteen;
    ImageView image_coffee;
    ImageView image_printer;

    public DataLoaderUserInt(Context context, ImageView image_canteen, ImageView image_coffee, ImageView image_printer) {
        this.context = context;
        this.image_canteen = image_canteen;
        this.image_coffee = image_coffee;
        this.image_printer = image_printer;
    }

    public void load() {
        Utilities utilities = new Utilities(context);
        image_canteen.setColorFilter(ContextCompat.getColor(context, R.color.grey_light));
        image_coffee.setColorFilter(ContextCompat.getColor(context, R.color.grey_light));
        image_printer.setColorFilter(ContextCompat.getColor(context, R.color.grey_light));


        utilities.setSignedInListener(new SignedInListener() {
            @Override
            public void onSignedIn(FirebaseUser user) {
                utilities.setCurrentStatusListener((category, status) -> {
                    switch (category) {
                        case Utilities.CATEGORY_CAFETERIA:
                            InteractionState stateCanteen = InteractionState.parseId(status);
                            image_canteen.setColorFilter(context.getColor(stateCanteen.getColor()));
                            break;
                        case Utilities.CATEGORY_COFFEE:
                            InteractionState stateCoffee = InteractionState.parseId(status);
                            image_coffee.setColorFilter(context.getColor(stateCoffee.getColor()));
                            break;
                        case Utilities.CATEGORY_PRINTER:
                            InteractionState statePrinter = InteractionState.parseId(status);
                            image_printer.setColorFilter(context.getColor(statePrinter.getColor()));
                            break;
                    }
                });
                utilities.getCurrentStatus(Utilities.CATEGORY_CAFETERIA);
                utilities.getCurrentStatus(Utilities.CATEGORY_PRINTER);
                utilities.getCurrentStatus(Utilities.CATEGORY_COFFEE);
            }

            @Override
            public void onSignInError() {
            }
        });
        utilities.signIn();
    }
}
