-- data.sql (PostgreSQL 호환)

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