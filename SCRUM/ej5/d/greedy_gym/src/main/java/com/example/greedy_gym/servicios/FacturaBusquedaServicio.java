package com.example.greedy_gym.servicios;

import com.example.greedy_gym.entidades.EstadoFactura;
import com.example.greedy_gym.entidades.Factura;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class FacturaBusquedaServicio {

    @PersistenceContext
    private EntityManager em;

    public Page<Factura> buscar(EstadoFactura estado, Long numero, int page, int size, String sort) {
        Pageable pageable = buildPageable(page, size, sort);
        String base = "from Factura f where f.eliminado=false";
        String where = "";
        Map<String, Object> params = new HashMap<>();
        if (estado != null) { where += " and f.estado = :estado"; params.put("estado", estado); }
        if (numero != null) { where += " and f.numeroFactura = :numero"; params.put("numero", numero); }

        String order = pageable.getSort().isUnsorted() ? "" : toOrder(pageable.getSort());
        TypedQuery<Factura> q = em.createQuery("select f " + base + where + order, Factura.class);
        TypedQuery<Long> cq = em.createQuery("select count(f) " + base + where, Long.class);
        params.forEach((k,v) -> { q.setParameter(k, v); cq.setParameter(k, v); });
        q.setFirstResult((int) pageable.getOffset());
        q.setMaxResults(pageable.getPageSize());
        List<Factura> content = q.getResultList();
        long total = cq.getSingleResult();
        return new PageImpl<>(content, pageable, total);
    }

    public Page<Factura> buscarPorSocio(String idSocio, EstadoFactura estado, Long numero, int page, int size, String sort) {
        Pageable pageable = buildPageable(page, size, sort);
        String base = "from Factura f join f.detalles d join d.cuotaMensual c where f.eliminado=false and c.idSocio = :idSocio";
        String where = "";
        Map<String, Object> params = new HashMap<>();
        params.put("idSocio", idSocio);
        if (estado != null) { where += " and f.estado = :estado"; params.put("estado", estado); }
        if (numero != null) { where += " and f.numeroFactura = :numero"; params.put("numero", numero); }
        String order = pageable.getSort().isUnsorted() ? "" : toOrder(pageable.getSort());
        TypedQuery<Factura> q = em.createQuery("select distinct f " + base + where + order, Factura.class);
        TypedQuery<Long> cq = em.createQuery("select count(distinct f) " + base + where, Long.class);
        params.forEach((k,v) -> { q.setParameter(k, v); cq.setParameter(k, v); });
        q.setFirstResult((int) pageable.getOffset());
        q.setMaxResults(pageable.getPageSize());
        List<Factura> content = q.getResultList();
        long total = cq.getSingleResult();
        return new PageImpl<>(content, pageable, total);
    }

    private String toOrder(Sort sort) {
        StringBuilder sb = new StringBuilder(" order by ");
        boolean first = true;
        for (Sort.Order o : sort) {
            if (!first) sb.append(',');
            sb.append("f.").append(o.getProperty()).append(' ').append(o.isAscending()?"asc":"desc");
            first = false;
        }
        return sb.toString();
    }

    private Pageable buildPageable(int page, int size, String sort) {
        if (sort == null || sort.isBlank()) return PageRequest.of(page, size);
        String[] parts = sort.split(",");
        String property = parts[0].trim();
        Sort.Direction direction = parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim())
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(direction, property));
    }
}
