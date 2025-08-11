/* === Base de datos === */
IF DB_ID(N'ExpedienteClinicaDB') IS NULL
    CREATE DATABASE ExpedienteClinicaDB;
GO
USE ExpedienteClinicaDB;
GO

/* === Entidades base === */
IF OBJECT_ID(N'dbo.Persona','U') IS NULL
CREATE TABLE dbo.Persona(
                            id        INT IDENTITY(1,1) PRIMARY KEY,
                            nombre    NVARCHAR(120) NOT NULL,
                            telefono  NVARCHAR(40)  NULL,
                            sexo      NVARCHAR(10)  NULL,     -- "M", "F", "Otro"
                            domicilio NVARCHAR(200) NULL,
                            email     NVARCHAR(120) NOT NULL UNIQUE
);

IF OBJECT_ID(N'dbo.Paciente','U') IS NULL
CREATE TABLE dbo.Paciente(
                             id INT NOT NULL PRIMARY KEY
                                 CONSTRAINT FK_Paciente_Persona FOREIGN KEY REFERENCES dbo.Persona(id)
);

IF OBJECT_ID(N'dbo.Doctor','U') IS NULL
CREATE TABLE dbo.Doctor(
                           id            INT NOT NULL PRIMARY KEY
                               CONSTRAINT FK_Doctor_Persona FOREIGN KEY REFERENCES dbo.Persona(id),
                           especialidad  NVARCHAR(100) NOT NULL
);

IF OBJECT_ID(N'dbo.Enfermero','U') IS NULL
CREATE TABLE dbo.Enfermero(
                              id INT NOT NULL PRIMARY KEY
                                  CONSTRAINT FK_Enfermero_Persona FOREIGN KEY REFERENCES dbo.Persona(id)
);

/* === Documentos / movimientos === */
IF OBJECT_ID(N'dbo.Cita','U') IS NULL
CREATE TABLE dbo.Cita(
                         id           INT IDENTITY(1,1) PRIMARY KEY,
                         paciente_id  INT NOT NULL
                             CONSTRAINT FK_Cita_Paciente  FOREIGN KEY REFERENCES dbo.Paciente(id),
                         doctor_id    INT NOT NULL
                             CONSTRAINT FK_Cita_Doctor    FOREIGN KEY REFERENCES dbo.Doctor(id),
                         fechaHora    DATETIME2 NOT NULL
);

IF OBJECT_ID(N'dbo.HojaMedica','U') IS NULL
CREATE TABLE dbo.HojaMedica(
                               id              INT IDENTITY(1,1) PRIMARY KEY,
                               fechaHora       DATETIME2     NOT NULL,
                               motivoConsulta  NVARCHAR(200) NULL,
                               diagnosticos    NVARCHAR(MAX) NULL,
    tratamiento     NVARCHAR(MAX) NULL,
    doctor_id       INT NOT NULL
        CONSTRAINT FK_HM_Doctor    FOREIGN KEY REFERENCES dbo.Doctor(id),
    paciente_id     INT NOT NULL
        CONSTRAINT FK_HM_Paciente  FOREIGN KEY REFERENCES dbo.Paciente(id)
);

IF OBJECT_ID(N'dbo.HojaEnfermeria','U') IS NULL
CREATE TABLE dbo.HojaEnfermeria(
                                   id              INT IDENTITY(1,1) PRIMARY KEY,
                                   fechaHora       DATETIME2     NOT NULL,
                                   signosVitales   NVARCHAR(MAX) NULL,
    observaciones   NVARCHAR(MAX) NULL,
    enfermero_id    INT NOT NULL
        CONSTRAINT FK_HE_Enfermero FOREIGN KEY REFERENCES dbo.Enfermero(id),
    paciente_id     INT NOT NULL
        CONSTRAINT FK_HE_Paciente  FOREIGN KEY REFERENCES dbo.Paciente(id)
);

IF OBJECT_ID(N'dbo.Vacuna','U') IS NULL
CREATE TABLE dbo.Vacuna(
                           id               INT IDENTITY(1,1) PRIMARY KEY,
                           fecha            DATE NOT NULL,
                           vacuna           NVARCHAR(120) NOT NULL,
                           aplicado_por_id  INT NOT NULL
                               CONSTRAINT FK_Vacuna_Persona  FOREIGN KEY REFERENCES dbo.Persona(id),
                           paciente_id      INT NOT NULL
                               CONSTRAINT FK_Vacuna_Paciente FOREIGN KEY REFERENCES dbo.Paciente(id)
);

/* === Índices útiles === */
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_Cita_Paciente_Fecha' AND object_id = OBJECT_ID('dbo.Cita'))
CREATE INDEX IX_Cita_Paciente_Fecha ON dbo.Cita(paciente_id, fechaHora DESC);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_HM_Paciente_Fecha' AND object_id = OBJECT_ID('dbo.HojaMedica'))
CREATE INDEX IX_HM_Paciente_Fecha ON dbo.HojaMedica(paciente_id, fechaHora DESC);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_HE_Paciente_Fecha' AND object_id = OBJECT_ID('dbo.HojaEnfermeria'))
CREATE INDEX IX_HE_Paciente_Fecha ON dbo.HojaEnfermeria(paciente_id, fechaHora DESC);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_Vacuna_Paciente_Fecha' AND object_id = OBJECT_ID('dbo.Vacuna'))
CREATE INDEX IX_Vacuna_Paciente_Fecha ON dbo.Vacuna(paciente_id, fecha DESC);
GO

