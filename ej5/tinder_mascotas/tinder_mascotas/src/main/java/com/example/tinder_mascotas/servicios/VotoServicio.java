package com.example.tinder_mascotas.servicios;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.tinder_mascotas.repositorios.MascotaRepositorio;
import com.example.tinder_mascotas.repositorios.VotoRepositorio;
import com.example.tinder_mascotas.entidades.Voto;
import com.example.tinder_mascotas.entidades.Mascota;
import java.util.Date;
import java.util.Optional;


public class VotoServicio {

    @Autowired
    private VotoRepositorio votoRepositorio;

    @Autowired
    private MascotaRepositorio mascotaRepositorio;

    @Autowired
    private NotificacionServicio notificacionServicio;

    public void agregarVoto(String idUsuario, String idMascota1, String idMascota2) {

        Voto nuevoVoto = new Voto();
        nuevoVoto.setFecha(new Date());

        if (idMascota1.equals(idMascota2)){
            throw new IllegalArgumentException("No puedes votar por la misma mascota");
        }

        Optional<Mascota> respuesta = mascotaRepositorio.findById(idMascota1);

        if (respuesta.isPresent()) {
            Mascota mascota1 = respuesta.get();
            if (mascota1.getUsuario().getId().equals(idUsuario)) {
                nuevoVoto.setMascota1(mascota1);
            } else {
                throw new IllegalArgumentException("No tienes permiso para votar por esta mascota");
            }
        } else {
            throw new IllegalArgumentException("Mascota no encontrada");
        }

        Optional<Mascota> otraRespuesta = mascotaRepositorio.findById(idMascota2);

        Mascota mascota2 = null;
        if (otraRespuesta.isPresent()) {
            mascota2 = otraRespuesta.get();
            nuevoVoto.setMascota2(mascota2);
        } else {
            throw new IllegalArgumentException("Mascota no encontrada");
        }

        votoRepositorio.save(nuevoVoto);

        notificacionServicio.enviar("Tu mascota ha sido votada", "Tinder de mascotas", mascota2.getUsuario().getEmail());
    }

    public void responder(String idUsuario, String idVoto){

        Optional<Voto> respuesta = votoRepositorio.findById(idVoto);

        if (respuesta.isPresent()) {
            Voto voto = respuesta.get();
            voto.setFecha(new Date());

            if (voto.getMascota2().getUsuario().getId().equals(idUsuario)) {
                
                votoRepositorio.save(voto);

                notificacionServicio.enviar("Tu voto fue correspondido", "Tinder de mascotas", voto.getMascota1().getUsuario().getEmail());
            } else {
                throw new IllegalArgumentException("No tienes permiso para responder a este voto");
            }

        } else {
            throw new IllegalArgumentException("Voto no encontrado");
        }

    }

}
