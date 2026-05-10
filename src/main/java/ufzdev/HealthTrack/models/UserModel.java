package ufzdev.HealthTrack.models;

import java.util.List;

public class UserModel {
        // Campos Generales
        private String id;
        private String name;
        private String username;
        private String email;
        private String password;
        private String role;      // paciente, medico, administrador

        // Paciente
        private double imc;               //
        private String doctorAsignadoId;  //
        private String contactoFamiliar;  //

        // Medico
        private String cedulaProfesional;
        private String especialidad;
        private List<String> pacientesIds;

        // Administrador
        private String nivelAcceso;

        // Constructor vacio para firebase
        public UserModel() {}

        // Getters y Setters Generales
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        // Getters y Setters Paciente
        public double getImc() { return imc; }
        public void setImc(double imc) { this.imc = imc; }

        public String getDoctorAsignadoId() { return doctorAsignadoId; }
        public void setDoctorAsignadoId(String doctorAsignadoId) { this.doctorAsignadoId = doctorAsignadoId; }

        public String getContactoFamiliar() { return contactoFamiliar; }
        public void setContactoFamiliar(String contactoFamiliar) { this.contactoFamiliar = contactoFamiliar; }

        // Getters y Setters Medico
        public String getCedulaProfesional() { return cedulaProfesional; }
        public void setCedulaProfesional(String cedulaProfesional) { this.cedulaProfesional = cedulaProfesional; }

        public String getEspecialidad() { return especialidad; }
        public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

        public List<String> getPacientesIds() { return pacientesIds; }
        public void setPacientesIds(List<String> pacientesIds) { this.pacientesIds = pacientesIds; }

        // Getters y Setters Administrador
        public String getNivelAcceso() { return nivelAcceso; }
        public void setNivelAcceso(String nivelAcceso) { this.nivelAcceso = nivelAcceso; }
}