package com.minimarket.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "facturas")
public class Factura {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int numero;

    @Column(nullable = false, length = 20)
    private String fecha;   // el diagrama usa String

    @Column(nullable = false)
    private int total;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleFactura> detalles = new ArrayList<>();

    public Factura() {}
    public Factura(int numero, String fecha, Cliente cliente) {
        this.numero = numero; this.fecha = fecha; this.cliente = cliente;
    }

    // helpers opcionales
    public void addDetalle(DetalleFactura d) {
        detalles.add(d); d.setFactura(this);
        this.total = detalles.stream().mapToInt(DetalleFactura::getSubtotal).sum();
    }

    public Long getId() { return id; }
    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public List<DetalleFactura> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleFactura> detalles) { this.detalles = detalles; }
}
