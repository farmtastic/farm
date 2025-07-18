-- 1. Zone 데이터 추가
-- 이 INSERT 실행 후, 새로 생성된 zone의 ID는 1이 됩니다.
INSERT INTO zones (zone_name, description, created_at)
VALUES ('zone-A', '메인 재배 구역', NOW());

-- 2. Device 데이터 추가 (수위, 조도, PH 센서)
-- 아래 INSERT 문들은 위에서 생성된 zone_id = 1 을 참조합니다.
INSERT INTO devices (device_name, d_type, model_type, installed_at, zone_id)
VALUES ('water-level-sensor-1', 'SENSOR', 'WATER_LEVEL', NOW(), 1);

INSERT INTO devices (device_name, d_type, model_type, installed_at, zone_id)
VALUES ('light-sensor-1', 'SENSOR', 'LIGHT', NOW(), 1);

INSERT INTO devices (device_name, d_type, model_type, installed_at, zone_id)
VALUES ('ph-sensor-1', 'SENSOR', 'PH', NOW(), 1);

-- 3. 센서 로그 데이터 추가 (테스트용)

-- 현재로부터 10분 전 데이터
INSERT INTO sensor_logs (device_id, log_value, log_time)
VALUES (1, 15.5, NOW() - INTERVAL '10 minutes'); -- 수위
-- 현재로부터 1시간 전 데이터
INSERT INTO sensor_logs (device_id, log_value, log_time)
VALUES (2, 750.0, NOW() - INTERVAL '1 hour'); -- 조도
-- 현재로부터 2시간 30분 전 데이터
INSERT INTO sensor_logs (device_id, log_value, log_time)
VALUES (3, 6.8, NOW() - INTERVAL '2 hours 30 minutes'); -- PH

-- 현재로부터 5시간 전 데이터
INSERT INTO sensor_logs (device_id, log_value, log_time)
VALUES (1, 14.0, NOW() - INTERVAL '5 hours');
-- 현재로부터 12시간 전 데이터
INSERT INTO sensor_logs (device_id, log_value, log_time)
VALUES (2, 300.0, NOW() - INTERVAL '12 hours');
-- 현재로부터 23시간 전 데이터
INSERT INTO sensor_logs (device_id, log_value, log_time)
VALUES (3, 6.5, NOW() - INTERVAL '23 hours');

-- 현재로부터 25시간 전 데이터 (삭제 대상)
INSERT INTO sensor_logs (device_id, log_value, log_time)
VALUES (1, 10.2, NOW() - INTERVAL '25 hours');
-- 현재로부터 2일 전 데이터 (삭제 대상)
INSERT INTO sensor_logs (device_id, log_value, log_time)
VALUES (2, 100.0, NOW() - INTERVAL '2 day');
-- 현재로부터 3일 전 데이터 (삭제 대상)
INSERT INTO sensor_logs (device_id, log_value, log_time)
VALUES (3, 6.0, NOW() - INTERVAL '3 day');