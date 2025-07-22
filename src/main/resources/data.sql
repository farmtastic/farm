-- Zone 데이터 추가
-- 이 INSERT 실행 후, 새로 생성된 zone의 ID는 1이 됩니다.
INSERT INTO zones (zone_name, description, created_at)
VALUES ('zone-A', '메인 재배 구역', NOW());

-- Device 데이터 추가 (수위, 조도, PH 센서)
-- 아래 INSERT 문들은 위에서 생성된 zone_id = 1 을 참조합니다.
-- 'WATER_LEVEL' 대신 'WATER_LEVEL_TOP'과 'WATER_LEVEL_BOTTOM'으로 명확하게 구분하여 2개의 디바이스를 추가합니다.
INSERT INTO devices (device_name, d_type, model_type, installed_at, zone_id)
VALUES ('water-level-top-sensor', 'SENSOR', 'WATER_LEVEL_TOP', NOW(), 1);

INSERT INTO devices (device_name, d_type, model_type, installed_at, zone_id)
VALUES ('water-level-bottom-sensor', 'SENSOR', 'WATER_LEVEL_BOTTOM', NOW(), 1);

INSERT INTO devices (device_name, d_type, model_type, installed_at, zone_id)
VALUES ('light-sensor-1', 'SENSOR', 'LIGHT', NOW(), 1);

INSERT INTO devices (device_name, d_type, model_type, installed_at, zone_id)
VALUES ('ph-sensor-1', 'SENSOR', 'PH', NOW(), 1);

-- 액추에이터 장비 (이후 device_id = 4, 5 으로 사용)
INSERT INTO devices (device_name, d_type, model_type, installed_at, zone_id)
VALUES ('water-pump-1', 'ACTUATOR', 'WATER_PUMP', NOW(), 1); -- device_id = 4 예상

INSERT INTO devices (device_name, d_type, model_type, installed_at, zone_id)
VALUES ('led-1', 'ACTUATOR', 'LED', NOW(), 1); -- device_id = 5 예상

-- AutomationRule 데이터 추가 (테스트용)

-- '수위 센서(device_id=1)'에 대한 [활성화된] 규칙
INSERT INTO automation_rules (rule_name, condition_op, threshold_value, action_command, is_active, sensor_id, actuator_id)
VALUES ('수위 경고', '>', 20.0, 'WATER_PUMP_ON', true, 1, 4);

-- '조도 센서(device_id=2)'에 대한 [비활성화된] 규칙
INSERT INTO automation_rules (rule_name, condition_op, threshold_value, action_command, is_active, sensor_id, actuator_id)
VALUES ('조명 자동 조절', '<', 500.0, 'LED_ON', false, 2, 5);

-- 센서 로그 데이터 추가 (테스트용)

-- 현재로부터 10분 전 데이터
INSERT INTO sensor_logs (device_id, log_value, log_time, threshold_value)
VALUES (1, 15.5, NOW() - INTERVAL '10 minutes', 20.0); -- 수위

-- 현재로부터 1시간 전 데이터
INSERT INTO sensor_logs (device_id, log_value, log_time, threshold_value)
VALUES (2, 750.0, NOW() - INTERVAL '1 hour', NULL); -- 조도

-- 현재로부터 2시간 30분 전 데이터
INSERT INTO sensor_logs (device_id, log_value, log_time, threshold_value)
VALUES (3, 6.8, NOW() - INTERVAL '2 hours 30 minutes', NULL); -- PH

-- 현재로부터 5시간 전 ~ 23시간 전 데이터
INSERT INTO sensor_logs (device_id, log_value, log_time, threshold_value) VALUES (1, 14.0, NOW() - INTERVAL '5 hours', 20.0);
INSERT INTO sensor_logs (device_id, log_value, log_time, threshold_value) VALUES (2, 300.0, NOW() - INTERVAL '12 hours', NULL);
INSERT INTO sensor_logs (device_id, log_value, log_time, threshold_value) VALUES (3, 6.5, NOW() - INTERVAL '23 hours', NULL);

-- 현재로부터 25시간 전 데이터 (삭제 대상)
INSERT INTO sensor_logs (device_id, log_value, log_time, threshold_value) VALUES (1, 10.2, NOW() - INTERVAL '25 hours', 20.0);
INSERT INTO sensor_logs (device_id, log_value, log_time, threshold_value) VALUES (2, 100.0, NOW() - INTERVAL '2 day', NULL);
INSERT INTO sensor_logs (device_id, log_value, log_time, threshold_value) VALUES (3, 6.0, NOW() - INTERVAL '3 day', NULL);