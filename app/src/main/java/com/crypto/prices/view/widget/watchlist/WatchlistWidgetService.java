package com.crypto.prices.view.widget.watchlist;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class WatchlistWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WatchlistDataProvider(this, intent);
    }
}
