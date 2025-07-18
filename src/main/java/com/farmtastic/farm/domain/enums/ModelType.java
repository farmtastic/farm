package com.farmtastic.farm.domain.enums;

public enum ModelType {
    TEMPERATURE,
    HUMIDITY,
    FAN,

    // 센서
    WATER_LEVEL, // 수위 센서 추가
    LIGHT,       // 조도 센서 추가
    PH,     // PH 센서 추가

    // 액추에이터 타입
    WATER_PUMP,
    LED
}
