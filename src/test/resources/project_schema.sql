DROP TABLE IF EXISTS material;
DROP TABLE IF EXISTS step;
DROP TABLE IF EXISTS project_category;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS project;


CREATE TABLE project(
	project_id INT AUTO_INCREMENT NOT NULL,
	project_name VARCHAR(128) NOT NULL,
	estimated_hours DECIMAL(7,2),
	actual_hours DECIMAL(7,2),
	difficulty INT,
	notes TEXT,
	PRIMARY KEY (project_id)
);


CREATE TABLE category(
	category_id INT AUTO_INCREMENT NOT NULL,
	category_name VARCHAR(128) NOT NULL ,
	PRIMARY KEY (category_id)
);


CREATE TABLE project_category(
	project_id INT NOT NULL,
	category_id INT NOT NULL,
	FOREIGN KEY (project_id) REFERENCES project (project_id) ON DELETE CASCADE,
	FOREIGN KEY (category_id) REFERENCES category (category_id) ON DELETE CASCADE,
	UNIQUE KEY (project_id, category_id)
);


CREATE TABLE step(
	step_id INT AUTO_INCREMENT NOT NULL,
	project_id INT NOT NULL,
	step_text TEXT NOT NULL,
	step_order INT NOT NULL,
	PRIMARY KEY (step_id),
	FOREIGN KEY (project_id) REFERENCES project (project_id) ON DELETE CASCADE
);

CREATE TABLE material(
	material_id INT AUTO_INCREMENT NOT NULL,
	project_id INT NOT NULL,
	material_name VARCHAR(128) NOT NULL,
	num_required INT,
	cost DECIMAL(7,2),
	PRIMARY KEY (material_id),
	FOREIGN KEY (project_id) REFERENCES project (project_id) ON DELETE CASCADE
);


	-- insert some data
	
INSERT INTO project (project_name, estimated_hours, actual_hours, difficulty, notes) VALUES ('Clean the Bathroom', 1.25,  1.4, 3, 'Clean sink with windex, clean toilet with lysol, wash floor with pine sol');
INSERT INTO material (project_id, material_name, num_required, cost) VALUES (1, 'windex', 1, 2.14);
INSERT INTO material (project_id, material_name, num_required, cost) VALUES (1, 'lysol', 1, 1.93);
INSERT INTO material (project_id, material_name, num_required, cost) VALUES (1, 'pine sol', 1, 0.99);
INSERT INTO material (project_id, material_name, num_required, cost) VALUES (1, 'paper towel', 2, 0.48);
INSERT INTO material (project_id, material_name, num_required, cost) VALUES (1, 'mop and bucket', 1, 10.49);
INSERT INTO material (project_id, material_name, num_required, cost) VALUES (1, 'toilet brush', 1, 5.99);
INSERT INTO step (project_id, step_text, step_order) VALUES (1, 'spray windex on mirror, the wipe with paper towel', 1);
INSERT INTO step (project_id, step_text, step_order) VALUES (1, 'add lysol to bowl, scrub with toilet brush, and wipe outside with paper towel', 2);
INSERT INTO step (project_id, step_text, step_order) VALUES (1, 'fill mop bucket with water and pine sol, and scrub the floor with the mop', 3);
INSERT INTO category (category_id, category_name) VALUES (1, 'Household Chores');
INSERT INTO	category (category_id, category_name) VALUES (2, 'jobs at work');
INSERT INTO	category (category_id, category_name) VALUES (3, 'Gardening');
INSERT INTO	project_category (project_id, category_id) VALUES (1, 1);
INSERT INTO project_category (project_id, category_id) VALUES (1, 2);

