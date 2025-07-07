package com.farmtastic.farm.dto;

import com.farmtastic.farm.domain.enums.ModelType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

// C++ 하드웨어로부터 MQTT를 통해 센서 데이터를 수신하기 위한 DTO
@Getter
@NoArgsConstructor
public class LogReceiveDto {
    private Long deviceId;
    private ModelType modelType;
    private BigDecimal value;
}