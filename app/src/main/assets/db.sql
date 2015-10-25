CREATE TABLE `orga_nice_categories` (
    `_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `name_en` VARCHAR(50) NOT NULL DEFAULT "",
    `name_de` VARCHAR(50) NOT NULL DEFAULT "",
    `parent_category_id` INT(5) NOT NULL DEFAULT 0,
    `default_attribute_type` INT (3) NOT NULL DEFAULT -1,
    `icon` VARCHAR(50) DEFAULT "kleiderbuegel"
);

CREATE TABLE `orga_nice_items` (
    `_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `name` VARCHAR(50) NOT  NULL DEFAULT "",
    `image_file` VARCHAR(255),
    `category_id` INTEGER(5) NOT NULL,
    `is_wish` INT(1)  DEFAULT 0,
    `primary_color` INT(3),
    `rating` FLOAT (2),
    FOREIGN KEY(category_id) REFERENCES orga_nice_categories(_id) ON DELETE CASCADE
);

CREATE TABLE `orga_nice_attribute_types` (
     `_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
     `name_en` VARCHAR(50) NOT NULL DEFAULT "",
     `name_de` VARCHAR(50) NOT NULL DEFAULT "",
     `value_type` INT(3) NOT NULL DEFAULT 0,
     `is_unique` INT(1) DEFAULT 0
);

CREATE TABLE `orga_nice_item_attribute_types` (
    `_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `item_id` INT NOT NULL,
    `attribute_type_id` INT NOT NULL,
    `attribute_value` VARCHAR(255),
    FOREIGN KEY(`item_id`) REFERENCES orga_nice_items(_id) ON DELETE CASCADE,
    FOREIGN KEY(`attribute_type_id`) REFERENCES orga_nice_attribute_types(_id)
);


CREATE TABLE `orga_nice_categories_attribute_types` (
    `_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `category_id` INT NOT NULL,
    `attribute_type_id` INT NOT NULL,
    `attribute_value` VARCHAR(255)
);

CREATE TABLE `orga_nice_compares` (
    `_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `name` VARCHAR(50) NOT NULL DEFAULT "MyCompare",
    `item_ids` VARCHAR(50) NOT NULL DEFAULT "",
    `save_date` TIMESTAMP
);

CREATE TABLE `orga_nice_colors` (
    `_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `name_en` VARCHAR(50) NOT NULL DEFAULT "",
    `name_de` VARCHAR(50) NOT NULL DEFAULT "",
    `hex` VARCHAR(50) NOT NULL DEFAULT ""
);
