package com.minimarket.main;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.minimarket.model.Cliente;    // ← AJUSTA si tus entidades están en otro paquete
import com.minimarket.model.Domicilio; // ← AJUSTA si tus entidades están en otro paquete

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("SistemaVentasPU");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Domicilio dom = new Domicilio("Calle nueva", 123);
            em.persist(dom);

            Cliente c = new Cliente("Pepe", "Honguito", "123456789", dom);
            em.persist(c);

            em.getTransaction().commit();
            System.out.println("OK: Cliente id=" + c.getId());
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
}
