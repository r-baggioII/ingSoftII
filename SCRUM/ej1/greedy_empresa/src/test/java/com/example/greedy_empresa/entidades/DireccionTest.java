package com.example.greedy_empresa.entidades;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DireccionTest {

    @Test
    void testHasGeoPoint_WhenBothCoordinatesPresent() {
        // Given
        Direccion direccion = new Direccion();
        direccion.setLatitud("-32.88970575178735");
        direccion.setLongitud("-68.84457510855037");
        
        // When & Then
        assertTrue(direccion.hasGeoPoint());
    }

    @Test
    void testHasGeoPoint_WhenCoordinatesNull() {
        // Given
        Direccion direccion = new Direccion();
        
        // When & Then
        assertFalse(direccion.hasGeoPoint());
    }

    @Test
    void testHasGeoPoint_WhenCoordinatesEmpty() {
        // Given
        Direccion direccion = new Direccion();
        direccion.setLatitud("");
        direccion.setLongitud("");
        
        // When & Then
        assertFalse(direccion.hasGeoPoint());
    }

    @Test
    void testGetGoogleMapsUrl_WhenCoordinatesPresent() {
        // Given
        Direccion direccion = new Direccion();
        direccion.setLatitud("-32.88970575178735");
        direccion.setLongitud("-68.84457510855037");
        
        // When
        String url = direccion.getGoogleMapsUrl();
        
        // Then
        assertEquals("https://www.google.com/maps?q=-32.88970575178735,-68.84457510855037", url);
    }

    @Test
    void testGetGoogleMapsUrl_WhenNoCoordinates() {
        // Given
        Direccion direccion = new Direccion();
        
        // When
        String url = direccion.getGoogleMapsUrl();
        
        // Then
        assertNull(url);
    }
}