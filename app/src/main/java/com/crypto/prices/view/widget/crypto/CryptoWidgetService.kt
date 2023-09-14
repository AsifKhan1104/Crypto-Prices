package com.crypto.prices.view.widget.crypto

import android.content.Intent
import android.widget.RemoteViewsService

class CryptoWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return CryptoDataProvider(this, intent)
    }
}