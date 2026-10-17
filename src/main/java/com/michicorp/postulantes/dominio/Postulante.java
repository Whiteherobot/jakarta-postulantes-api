package com.michicorp.postulantes.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;

/** Una postulación de una persona a un puesto. */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"cedula", "puesto"}))
public class Postulante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String cedula;

    @Column(nullable = false, length = 80)
    private String nombres;

    @Column(nullable = false, length = 120)
    private String correo;

    @Column(nullable = false, length = 40)
    private String puesto;

    private int experiencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private Estado estado;

    @Column(nullable = false)
    private LocalDate fechaPostulacion;

    protected Postulante() {
        // Para JPA.
    }

    /** Una postulación nueva empieza siempre como POSTULADO. */
    public static Postulante nueva(DatosPostulante datos, LocalDate fecha) {
        Postulante p = new Postulante();
        p.actualizar(datos);
        p.estado = Estado.POSTULADO;
        p.fechaPostulacion = fecha;
        return p;
    }

    /** Cambia los datos de contacto y el puesto; el estado y la fecha no se tocan. */
    public void actualizar(DatosPostulante datos) {
        cedula = datos.cedula();
        nombres = datos.nombres();
        correo = datos.correo();
        puesto = datos.puesto();
        experiencia = datos.experiencia();
    }

    /** Avanza al estado pedido, si la transición está permitida. */
    public void pasarA(Estado nuevo) {
        if (!estado.puedePasarA(nuevo)) {
            throw new IllegalStateException(
                    "No se puede pasar de " + estado.nombre() + " a " + nuevo.nombre() + ".");
        }
        estado = nuevo;
    }

    public Long getId() {
        return id;
    }

    /** Solo para los repositorios que no generan el id (los de prueba). */
    public void asignarId(Long id) {
        this.id = id;
    }

    public String getCedula() {
        return cedula;
    }

    public String getNombres() {
        return nombres;
    }

    public String getCorreo() {
        return correo;
    }

    public String getPuesto() {
        return puesto;
    }

    public int getExperiencia() {
        return experiencia;
    }

    public Estado getEstado() {
        return estado;
    }

    public LocalDate getFechaPostulacion() {
        return fechaPostulacion;
    }
}
