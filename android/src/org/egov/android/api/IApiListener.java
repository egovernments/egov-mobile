package org.egov.android.api;

import org.egov.android.listener.Event;

public interface IApiListener {

    public void onResponse(Event<ApiResponse> event);
}
