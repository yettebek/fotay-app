CREATE DATABASE IF NOT EXISTS fotay_db DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;

USE fotay_db;

CREATE TABLE usuarios (
    usu_id INT(3) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    usu_nombre varchar(40) NOT NULL,
    usu_contrasena varchar(50) NOT NULL
) ENGINE = InnoDB DEFAULT CHARSET = latin1;

ALTER TABLE usuarios
ADD UNIQUE INDEX(usu_id, usu_nombre, usu_contrasena);

INSERT INTO usuarios (usu_nombre, usu_contrasena)
VALUES ('Stephen', '12345678a'),
    ('Joseph', '12345678b'),
    ('Piopio', '11111111a');

CREATE TABLE fotos(
    foto_id INT(3) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    usu_id INT(3) NOT NULL,
    usu_nombre VARCHAR(40) NOT NULL,
    foto_fecha DATETIME NOT NULL,
    foto_coment VARCHAR(255) DEFAULT NULL,
    foto_ruta VARCHAR(255) DEFAULT NULL,
    foto_perfil VARCHAR(255) DEFAULT NULL
) ENGINE = INNODB DEFAULT CHARSET = latin1;

ALTER TABLE fotos
ADD CONSTRAINT FK_FotosUsuarios FOREIGN KEY(usu_id) REFERENCES usuarios(usu_id);

INSERT INTO fotos (usu_id, usu_nombre, foto_fecha, foto_coment, foto_ruta)
VALUES('1','John',NOW() + INTERVAL 2 HOUR,'pack_fotos','https://fotay.000webhostapp.com/images/fotay_31652463231.jpg'),
('1','John',NOW() + INTERVAL 2 HOUR,'pack_fotos','https://fotay.000webhostapp.com/images/fotay_51652463232.jpg'),
('1','John',NOW() + INTERVAL 2 HOUR,'pack_fotos','https://fotay.000webhostapp.com/images/fotay_51652463951.jpg'),
('1','John',NOW() + INTERVAL 2 HOUR,'pack_fotos','https://fotay.000webhostapp.com/images/fotay_71652464954.jpg'),
('1','John',NOW() + INTERVAL 2 HOUR,'pack_fotos','https://fotay.000webhostapp.com/images/fotay_81652465340.jpg'),
('1','John',NOW() + INTERVAL 2 HOUR,'pack_fotos','https://fotay.000webhostapp.com/images/fotay_31652463231.jpg'),
('1','John',NOW() + INTERVAL 2 HOUR,'pack_fotos','https://fotay.000webhostapp.com/images/fotay_51652463232.jpg'),
('1','John',NOW() + INTERVAL 2 HOUR,'pack_fotos','https://fotay.000webhostapp.com/images/fotay_51652463951.jpg'),
('1','John',NOW() + INTERVAL 2 HOUR,'pack_fotos','https://fotay.000webhostapp.com/images/fotay_71652464954.jpg'),
('1','John',NOW() + INTERVAL 2 HOUR,'pack_fotos','https://fotay.000webhostapp.com/images/fotay_81652465340.jpg'),
('1','John',NOW() + INTERVAL 2 HOUR,'pack_fotos','https://fotay.000webhostapp.com/images/fotay_111652470734.jpg'),
('1','John',NOW() + INTERVAL 2 HOUR,'pack_fotos','https://fotay.000webhostapp.com/images/fotay_111652470734.jpg');

CREATE TABLE comentarios(
    coment_id INT(3) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    foto_id INT(3) NOT NULL,
    usu_id INT(3) NOT NULL,
    usu_nombre VARCHAR(40) NOT NULL,
    txt_coment VARCHAR(255) NOT NULL
) ENGINE = INNODB DEFAULT CHARSET = latin1;

ALTER TABLE comentarios
ADD CONSTRAINT FK_ComentariosFoto FOREIGN KEY(foto_id) REFERENCES fotos(foto_id);
ALTER TABLE comentarios
ADD CONSTRAINT FK_ComentariosUsuario FOREIGN KEY(usu_id) REFERENCES usuarios(usu_id);

