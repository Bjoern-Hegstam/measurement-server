-- ADD NEW TABLES

CREATE TABLE instrumentation (
  id   VARCHAR(36) NOT NULL PRIMARY KEY,
  name VARCHAR     NOT NULL
);

CREATE TABLE sensor_registration (
  id                 VARCHAR(36) NOT NULL PRIMARY KEY,
  instrumentation_id VARCHAR(36) NOT NULL,
  sensor_id          VARCHAR(36) NOT NULL,
  valid_from         TIMESTAMP   NOT NULL,
  valid_to           TIMESTAMP
);

CREATE TABLE sensor (
  id   VARCHAR(36) NOT NULL PRIMARY KEY,
  name VARCHAR     NOT NULL
);

ALTER TABLE measurement
  ADD sensor_id VARCHAR(36);

ALTER TABLE measurement
  ADD instrumentation_id VARCHAR(36);

-- MIGRATE MEASUREMENT DATA
--- Create sensor, instrumentation and sensor_registration for each unique source, valid from first measurement, and associate with each measurement

INSERT INTO sensor (id, name)
SELECT
       md5(random() :: text || clock_timestamp() :: text) :: uuid,
       m.source
FROM measurement m
GROUP BY m.source;

INSERT INTO instrumentation (id, name)
SELECT
       md5(random() :: text || clock_timestamp() :: text) :: uuid,
       s.name
FROM sensor s;


INSERT INTO sensor_registration (id, instrumentation_id, sensor_id, valid_from)
SELECT
       md5(random() :: text || clock_timestamp() :: text) :: uuid,
       i.id,
       s.id,
       min(m.timestamp)
FROM instrumentation i
       INNER JOIN sensor s on i.name = s.name
       INNER JOIN measurement m on m.source = i.name
GROUP BY i.id, s.id;

UPDATE measurement m
SET instrumentation_id = (select id
                          from instrumentation
                          where name = m.source),
    sensor_id          = (select id
                          from sensor
                          where name = m.source);

-- ADD FOREIGN KEYS
ALTER TABLE sensor_registration
    ADD CONSTRAINT fk_sensor_registration_instrumentation FOREIGN KEY (instrumentation_id) REFERENCES instrumentation (id);

ALTER TABLE sensor_registration
  ADD CONSTRAINT fk_sensor_registration_sensor FOREIGN KEY (sensor_id) REFERENCES sensor (id);

ALTER TABLE measurement
  ADD CONSTRAINT fk_measurement_sensor FOREIGN KEY (sensor_id) REFERENCES sensor (id);

ALTER TABLE measurement
  ADD CONSTRAINT fk_measurement_instrumentation FOREIGN KEY (instrumentation_id) REFERENCES instrumentation (id);

ALTER TABLE measurement
    DROP COLUMN source;