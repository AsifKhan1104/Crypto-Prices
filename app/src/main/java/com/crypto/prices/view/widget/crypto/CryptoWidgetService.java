package com.crypto.prices.view.widget.crypto;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class CryptoWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new CryptoDataProvider(this, intent);
    }
}
