CREATE TABLE "mission"
(
    "id"   serial       NOT NULL,
    "name" varchar(100) NOT NULL,
    PRIMARY KEY ("id")
);



CREATE TABLE "hardware_to_mission"
(
    "id"          bigserial NOT NULL,
    "hardware_id" bigint    NOT NULL,
    "mission_id"  integer   NOT NULL,
    PRIMARY KEY ("id")
);



CREATE TABLE "release"
(
    "id"      bigserial    NOT NULL,
    "name"    varchar(100) NOT NULL,
    "date"    date         NOT NULL,
    "status"  integer      NOT NULL,
    "mission" integer      NOT NULL,
    PRIMARY KEY ("id")
);



CREATE TABLE "firmware_type"
(
    "id"   serial       NOT NULL,
    "name" varchar(100) NOT NULL,
    PRIMARY KEY ("id")
);



CREATE TABLE "release_status"
(
    "id"   integer NOT NULL,
    "name" bigint  NOT NULL,
    PRIMARY KEY ("id")
);



CREATE TABLE "firmware"
(
    "id"   bigserial    NOT NULL,
    "name" varchar(100) NOT NULL,
    "type" integer      NOT NULL,
    PRIMARY KEY ("id")
);



CREATE TABLE "hardware"
(
    "id"   bigint       NOT NULL,
    "name" varchar(100) NOT NULL,
    PRIMARY KEY ("id")
);



CREATE TABLE "firmware_to_hardware"
(
    "id"          bigserial NOT NULL,
    "firmware_id" bigint    NOT NULL,
    "hardware_id" bigint    NOT NULL,
    PRIMARY KEY ("id")
);



CREATE TABLE "firmware_to_release"
(
    "id"          bigserial    NOT NULL,
    "firmware_id" bigint       NOT NULL,
    "release_id"  bigint       NOT NULL,
    "version"     varchar(100) NOT NULL,
    PRIMARY KEY ("id")
);


ALTER TABLE firmware_to_release
    ADD CONSTRAINT fk_firmware_id_firmware_to_release_firmware_id FOREIGN KEY (firmware_id) REFERENCES firmware (id);

ALTER TABLE firmware_to_hardware
    ADD CONSTRAINT fk_firmware_id_firmware_to_hardware_firmware_id FOREIGN KEY (firmware_id) REFERENCES firmware (id);

ALTER TABLE firmware
    ADD CONSTRAINT fk_firmware_type_id_firmware_type FOREIGN KEY (type) REFERENCES firmware_type (id);

ALTER TABLE hardware_to_mission
    ADD CONSTRAINT fk_hardware_id_hardware_to_mission_hardware_id FOREIGN KEY (hardware_id) REFERENCES hardware (id);

ALTER TABLE firmware_to_hardware
    ADD CONSTRAINT fk_hardware_id_firmware_to_hardware_hardware_id FOREIGN KEY (hardware_id) REFERENCES hardware (id);

ALTER TABLE hardware_to_mission
    ADD CONSTRAINT fk_mission_id_hardware_to_mission_mission_id FOREIGN KEY (mission_id) REFERENCES mission (id);

ALTER TABLE release
    ADD CONSTRAINT fk_mission_id_release_mission FOREIGN KEY (mission) REFERENCES mission (id);

ALTER TABLE firmware_to_release
    ADD CONSTRAINT fk_release_id_firmware_to_release_release_id FOREIGN KEY (release_id) REFERENCES release (id);

ALTER TABLE release
    ADD CONSTRAINT fk_release_status_id_release_status FOREIGN KEY (status) REFERENCES release_status (id);


