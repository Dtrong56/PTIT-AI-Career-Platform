package com.ptit.ai_engine_service.messaging.event;

public final class RoutingKeys {

    private RoutingKeys() {
    }

    // Core -> AI Engine
    public static final String CV_PREPROCESSING_REQUEST = "cv.preprocessing.request";

    public static final String JD_PREPROCESSING_REQUEST = "jd.preprocessing.request";

    public static final String CV_ANALYSIS_REQUEST = "cv.analysis.request";

    // AI Engine -> Core
    public static final String CV_PREPROCESSING_COMPLETED = "cv.preprocessing.completed";

    public static final String JD_PREPROCESSING_COMPLETED = "jd.preprocessing.completed";

    public static final String CV_ANALYSIS_COMPLETED = "cv.analysis.completed";
}