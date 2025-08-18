CREATE TABLE "missions"
(
    "id"   serial PRIMARY KEY,
    "name" varchar(100) UNIQUE NOT NULL
);



CREATE TABLE "hardware"
(
    "id"   bigserial PRIMARY KEY,
    "name" varchar(100) UNIQUE NOT NULL
);



CREATE TABLE "hardware_to_mission"
(
    "id"          bigserial PRIMARY KEY,
    "hardware_id" bigint  NOT NULL,
    "mission_id"  integer NOT NULL,
    CONSTRAINT hardware_to_mission_hardware_fk FOREIGN KEY (hardware_id) REFERENCES hardware (id),
    CONSTRAINT hardware_to_mission_missions_fk FOREIGN KEY (mission_id) REFERENCES missions (id)
);



CREATE TABLE "firmware_types"
(
    "id"   serial PRIMARY KEY,
    "name" varchar(100) UNIQUE NOT NULL
);



CREATE TABLE "firmware"
(
    "id"       bigserial PRIMARY KEY,
    "name"     varchar(100) NOT NULL,
    "type"     integer      NOT NULL,
    "oci_name" varchar(100) UNIQUE NOT NULL,
    CONSTRAINT firmware_firmware_types_fk FOREIGN KEY (type) REFERENCES firmware_types (id)
);



CREATE TABLE "firmware_to_hardware"
(
    "id"          bigserial PRIMARY KEY,
    "firmware_id" bigint NOT NULL,
    "hardware_id" bigint NOT NULL,
    CONSTRAINT firmware_to_hardware_firmware_fk FOREIGN KEY (firmware_id) REFERENCES firmware (id),
    CONSTRAINT firmware_to_hardware_hardware_fk FOREIGN KEY (hardware_id) REFERENCES hardware (id)
);



CREATE TABLE "release_statuses"
(
    "id"   serial PRIMARY KEY,
    "name" varchar(100) UNIQUE NOT NULL
);



CREATE TABLE "releases"
(
    "id"        bigserial PRIMARY KEY,
    "name"      varchar(100) NOT NULL,
    "date"      date         NOT NULL,
    "status"    integer      NOT NULL,
    "mission"   integer      NOT NULL,
    "oci_name"  varchar(100) NOT NULL,
    "reference" varchar(100) NOT NULL,
    "digest"    varchar(100),
    CONSTRAINT releases_release_statuses_fk FOREIGN KEY (status) REFERENCES release_statuses (id),
    CONSTRAINT releases_missions_fk FOREIGN KEY (mission) REFERENCES missions (id)
);



CREATE TABLE "firmware_to_release"
(
    "id"               bigserial PRIMARY KEY,
    "firmware_id"      bigint       NOT NULL,
    "release_id"       bigint       NOT NULL,
    "hardware_id"      bigint       NOT NULL,
    "firmware_version" varchar(100) NOT NULL,
    CONSTRAINT firmware_to_release_firmware_fk FOREIGN KEY (firmware_id) REFERENCES firmware (id),
    CONSTRAINT firmware_to_release_releases_fk FOREIGN KEY (release_id) REFERENCES releases (id),
    CONSTRAINT firmware_to_release_hardware_fk FOREIGN KEY (hardware_id) REFERENCES hardware (id)
);
