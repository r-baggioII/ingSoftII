# **Informe Final – Proyecto Gimnasio “Sport”**

## **Índice**

[**1\. Introducción	2**](#1.-introducción)

[1.1 Objetivo del proyecto](#1.1-objetivo-del-proyecto-1.2-alcance-del-sistema-1.3-metodología-de-trabajo-\(rup\)-1.4-equipos-de-trabajo)  
 [1.2 Alcance del sistema](#1.1-objetivo-del-proyecto-1.2-alcance-del-sistema-1.3-metodología-de-trabajo-\(rup\)-1.4-equipos-de-trabajo)  
 [1.3 Metodología de trabajo (RUP)](#1.1-objetivo-del-proyecto-1.2-alcance-del-sistema-1.3-metodología-de-trabajo-\(rup\)-1.4-equipos-de-trabajo)  
 [1.4 Equipos de trabajo	2](#1.1-objetivo-del-proyecto-1.2-alcance-del-sistema-1.3-metodología-de-trabajo-\(rup\)-1.4-equipos-de-trabajo)

[**2\. Especificación del Sistema	2**](#2.-especificación-del-sistema)

[2.1 Descripción general del problema](#2.1-descripción-general-del-problema-2.2-requerimientos-funcionales-2.3-requerimientos-no-funcionales-2.4-actores-y-roles-del-sistema)  
 [2.2 Requerimientos funcionales](#2.1-descripción-general-del-problema-2.2-requerimientos-funcionales-2.3-requerimientos-no-funcionales-2.4-actores-y-roles-del-sistema)  
 [2.3 Requerimientos no funcionales](#2.1-descripción-general-del-problema-2.2-requerimientos-funcionales-2.3-requerimientos-no-funcionales-2.4-actores-y-roles-del-sistema)  
 [2.4 Actores y roles del sistema	3](#2.1-descripción-general-del-problema-2.2-requerimientos-funcionales-2.3-requerimientos-no-funcionales-2.4-actores-y-roles-del-sistema)

[**3\. Modelado y Diseño	3**](#3.-modelado-y-diseño)

[3.1 Escenarios de caso de uso](#3.1-escenarios-de-caso-de-uso-3.2-diagramas-uml)  
[3.2 Diagramas UML	3](#3.1-escenarios-de-caso-de-uso-3.2-diagramas-uml)

[3.2.1 Diagrama de casos de uso	3](#3.2.1-diagrama-de-casos-de-uso)

[3.2.2 Diagrama de clases de dominio	3](#3.2.2-diagrama-de-clases-de-dominio)

[3.2.3 Diagramas de secuencia del dominio	3](#3.2.3-diagramas-de-secuencia-del-dominio)

[3.2.4 Diagrama de clases de diseño	3](#3.2.4-diagrama-de-clases-de-diseño)

[3.2.5 Diagramas de secuencia de diseño	3](#3.2.5-diagramas-de-secuencia-de-diseño)

[3.2.6 Diagrama entidad-relación (Base de datos)](#3.2.6-diagrama-entidad-relación-\(base-de-datos\)-3.3-maquetado-de-la-interfaz-de-usuario-\(ui\))  
[3.3 Maquetado de la interfaz de usuario (UI)	3](#3.2.6-diagrama-entidad-relación-\(base-de-datos\)-3.3-maquetado-de-la-interfaz-de-usuario-\(ui\))

[**4\. Arquitectura del Sistema	3**](#4.-arquitectura-del-sistema)

[4.1 Arquitectura Cliente – Servidor](#4.1-arquitectura-cliente-–-servidor-4.2-capas-de-la-aplicación-4.3-tecnologías-utilizadas)  
 [4.2 Capas de la aplicación](#4.1-arquitectura-cliente-–-servidor-4.2-capas-de-la-aplicación-4.3-tecnologías-utilizadas)  
 [4.3 Tecnologías utilizadas	3](#4.1-arquitectura-cliente-–-servidor-4.2-capas-de-la-aplicación-4.3-tecnologías-utilizadas)

[**5\. Implementación de Patrones de Software	4**](#5.-implementación-de-patrones-de-software)

[5.1 Capas](#5.1-capas-5.2-experto-en-responsabilidad-5.3-creador-5.4-polimorfismo-5.5-alta-cohesión-/-bajo-acoplamiento-5.6-modelo-–-vista-–-controlador-\(mvc\)-5.7-inyección-de-dependencias-5.8-dto-\(para-reportes\)-5.9-dao-\(para-persistencia\)-\(explicar-dónde-y-cómo-se-usaron-con-diagramas-y-ejemplos-de-código\))  
 [5.2 Experto en Responsabilidad](#5.1-capas-5.2-experto-en-responsabilidad-5.3-creador-5.4-polimorfismo-5.5-alta-cohesión-/-bajo-acoplamiento-5.6-modelo-–-vista-–-controlador-\(mvc\)-5.7-inyección-de-dependencias-5.8-dto-\(para-reportes\)-5.9-dao-\(para-persistencia\)-\(explicar-dónde-y-cómo-se-usaron-con-diagramas-y-ejemplos-de-código\))  
 [5.3 Creador](#5.1-capas-5.2-experto-en-responsabilidad-5.3-creador-5.4-polimorfismo-5.5-alta-cohesión-/-bajo-acoplamiento-5.6-modelo-–-vista-–-controlador-\(mvc\)-5.7-inyección-de-dependencias-5.8-dto-\(para-reportes\)-5.9-dao-\(para-persistencia\)-\(explicar-dónde-y-cómo-se-usaron-con-diagramas-y-ejemplos-de-código\))  
 [5.4 Polimorfismo](#5.1-capas-5.2-experto-en-responsabilidad-5.3-creador-5.4-polimorfismo-5.5-alta-cohesión-/-bajo-acoplamiento-5.6-modelo-–-vista-–-controlador-\(mvc\)-5.7-inyección-de-dependencias-5.8-dto-\(para-reportes\)-5.9-dao-\(para-persistencia\)-\(explicar-dónde-y-cómo-se-usaron-con-diagramas-y-ejemplos-de-código\))  
 [5.5 Alta Cohesión / Bajo Acoplamiento](#5.1-capas-5.2-experto-en-responsabilidad-5.3-creador-5.4-polimorfismo-5.5-alta-cohesión-/-bajo-acoplamiento-5.6-modelo-–-vista-–-controlador-\(mvc\)-5.7-inyección-de-dependencias-5.8-dto-\(para-reportes\)-5.9-dao-\(para-persistencia\)-\(explicar-dónde-y-cómo-se-usaron-con-diagramas-y-ejemplos-de-código\))  
 [5.6 Modelo – Vista – Controlador (MVC)](#5.1-capas-5.2-experto-en-responsabilidad-5.3-creador-5.4-polimorfismo-5.5-alta-cohesión-/-bajo-acoplamiento-5.6-modelo-–-vista-–-controlador-\(mvc\)-5.7-inyección-de-dependencias-5.8-dto-\(para-reportes\)-5.9-dao-\(para-persistencia\)-\(explicar-dónde-y-cómo-se-usaron-con-diagramas-y-ejemplos-de-código\))  
 [5.7 Inyección de dependencias](#5.1-capas-5.2-experto-en-responsabilidad-5.3-creador-5.4-polimorfismo-5.5-alta-cohesión-/-bajo-acoplamiento-5.6-modelo-–-vista-–-controlador-\(mvc\)-5.7-inyección-de-dependencias-5.8-dto-\(para-reportes\)-5.9-dao-\(para-persistencia\)-\(explicar-dónde-y-cómo-se-usaron-con-diagramas-y-ejemplos-de-código\))  
 [5.8 DTO (para reportes)](#5.1-capas-5.2-experto-en-responsabilidad-5.3-creador-5.4-polimorfismo-5.5-alta-cohesión-/-bajo-acoplamiento-5.6-modelo-–-vista-–-controlador-\(mvc\)-5.7-inyección-de-dependencias-5.8-dto-\(para-reportes\)-5.9-dao-\(para-persistencia\)-\(explicar-dónde-y-cómo-se-usaron-con-diagramas-y-ejemplos-de-código\))  
 [5.9 DAO (para persistencia)](#5.1-capas-5.2-experto-en-responsabilidad-5.3-creador-5.4-polimorfismo-5.5-alta-cohesión-/-bajo-acoplamiento-5.6-modelo-–-vista-–-controlador-\(mvc\)-5.7-inyección-de-dependencias-5.8-dto-\(para-reportes\)-5.9-dao-\(para-persistencia\)-\(explicar-dónde-y-cómo-se-usaron-con-diagramas-y-ejemplos-de-código\))  
 [(explicar dónde y cómo se usaron con diagramas y ejemplos de código)	4](#5.1-capas-5.2-experto-en-responsabilidad-5.3-creador-5.4-polimorfismo-5.5-alta-cohesión-/-bajo-acoplamiento-5.6-modelo-–-vista-–-controlador-\(mvc\)-5.7-inyección-de-dependencias-5.8-dto-\(para-reportes\)-5.9-dao-\(para-persistencia\)-\(explicar-dónde-y-cómo-se-usaron-con-diagramas-y-ejemplos-de-código\))

[**6\. Iteraciones del Proceso RUP	4**](#6.-iteraciones-del-proceso-rup)

[6.1 Inicio](#6.1-inicio-6.2-elaboración-6.3-construcción-6.4-transición-\(detallar-avances-en-cada-etapa\))  
 [6.2 Elaboración](#6.1-inicio-6.2-elaboración-6.3-construcción-6.4-transición-\(detallar-avances-en-cada-etapa\))  
 [6.3 Construcción](#6.1-inicio-6.2-elaboración-6.3-construcción-6.4-transición-\(detallar-avances-en-cada-etapa\))  
 [6.4 Transición](#6.1-inicio-6.2-elaboración-6.3-construcción-6.4-transición-\(detallar-avances-en-cada-etapa\))  
 [(detallar avances en cada etapa)	4](#6.1-inicio-6.2-elaboración-6.3-construcción-6.4-transición-\(detallar-avances-en-cada-etapa\))

[**7\. Propuesta de Mejora	4**](#7.-propuesta-de-mejora)

[7.1 Análisis de software de gimnasios existentes](#7.1-análisis-de-software-de-gimnasios-existentes-7.2-nuevas-funcionalidades-sugeridas)  
 [7.2 Nuevas funcionalidades sugeridas	5](#7.1-análisis-de-software-de-gimnasios-existentes-7.2-nuevas-funcionalidades-sugeridas)

[**8\. Refactorización	5**](#8.-refactorización)

[8.1 Controladores](#8.1-controladores-8.2-dominio-de-la-aplicación-8.3-acceso-a-datos-\(con-propuestas-y-justificación\))  
 [8.2 Dominio de la aplicación](#8.1-controladores-8.2-dominio-de-la-aplicación-8.3-acceso-a-datos-\(con-propuestas-y-justificación\))  
 [8.3 Acceso a datos](#8.1-controladores-8.2-dominio-de-la-aplicación-8.3-acceso-a-datos-\(con-propuestas-y-justificación\))  
 [(con propuestas y justificación)	5](#8.1-controladores-8.2-dominio-de-la-aplicación-8.3-acceso-a-datos-\(con-propuestas-y-justificación\))

[**9\. Resultados y Pruebas	5**](#9.-resultados-y-pruebas)

[9.1 Funcionalidades implementadas](#9.1-funcionalidades-implementadas-9.2-cobertura-de-requerimientos-9.3-porcentaje-de-codificación-alcanzado-9.4-pruebas-realizadas-9.5-aplicación-funcionando)  
 [9.2 Cobertura de requerimientos](#9.1-funcionalidades-implementadas-9.2-cobertura-de-requerimientos-9.3-porcentaje-de-codificación-alcanzado-9.4-pruebas-realizadas-9.5-aplicación-funcionando)  
 [9.3 Porcentaje de codificación alcanzado](#9.1-funcionalidades-implementadas-9.2-cobertura-de-requerimientos-9.3-porcentaje-de-codificación-alcanzado-9.4-pruebas-realizadas-9.5-aplicación-funcionando)  
 [9.4 Pruebas realizadas](#9.1-funcionalidades-implementadas-9.2-cobertura-de-requerimientos-9.3-porcentaje-de-codificación-alcanzado-9.4-pruebas-realizadas-9.5-aplicación-funcionando)  
 [9.5 Aplicación funcionando	5](#9.1-funcionalidades-implementadas-9.2-cobertura-de-requerimientos-9.3-porcentaje-de-codificación-alcanzado-9.4-pruebas-realizadas-9.5-aplicación-funcionando)

[**10\. Conclusiones	5**](#10.-conclusiones)

[10.1 Logros alcanzados](#10.1-logros-alcanzados-10.2-limitaciones-10.3-posibles-mejoras-futuras)  
 [10.2 Limitaciones](#10.1-logros-alcanzados-10.2-limitaciones-10.3-posibles-mejoras-futuras)  
 [10.3 Posibles mejoras futuras	5](#10.1-logros-alcanzados-10.2-limitaciones-10.3-posibles-mejoras-futuras)

[**Bibliografía	5**](#bibliografía)

[**Anexos	5**](#anexos)

[● Capturas de pantalla de la aplicación	6](#capturas-de-pantalla-de-la-aplicación)

[● Enlace al repositorio Git	6](#enlace-al-repositorio-git)

[● Código relevante	6](#código-relevante)

# 1\. Introducción {#1.-introducción}

## 1.1 Objetivo del proyecto 1.2 Alcance del sistema 1.3 Metodología de trabajo (RUP) 1.4 Equipos de trabajo {#1.1-objetivo-del-proyecto-1.2-alcance-del-sistema-1.3-metodología-de-trabajo-(rup)-1.4-equipos-de-trabajo}

# 2\. Especificación del Sistema {#2.-especificación-del-sistema}

## 2.1 Descripción general del problema 2.2 Requerimientos funcionales 2.3 Requerimientos no funcionales 2.4 Actores y roles del sistema {#2.1-descripción-general-del-problema-2.2-requerimientos-funcionales-2.3-requerimientos-no-funcionales-2.4-actores-y-roles-del-sistema}

# 3\. Modelado y Diseño {#3.-modelado-y-diseño}

## 3.1 Escenarios de caso de uso 3.2 Diagramas UML {#3.1-escenarios-de-caso-de-uso-3.2-diagramas-uml}

### 3.2.1 Diagrama de casos de uso  {#3.2.1-diagrama-de-casos-de-uso}

### 3.2.2 Diagrama de clases de dominio  {#3.2.2-diagrama-de-clases-de-dominio}

### 3.2.3 Diagramas de secuencia del dominio  {#3.2.3-diagramas-de-secuencia-del-dominio}

### 3.2.4 Diagrama de clases de diseño  {#3.2.4-diagrama-de-clases-de-diseño}

### 3.2.5 Diagramas de secuencia de diseño  {#3.2.5-diagramas-de-secuencia-de-diseño}

### 3.2.6 Diagrama entidad-relación (Base de datos) 3.3 Maquetado de la interfaz de usuario (UI)  {#3.2.6-diagrama-entidad-relación-(base-de-datos)-3.3-maquetado-de-la-interfaz-de-usuario-(ui)}

# 4\. Arquitectura del Sistema {#4.-arquitectura-del-sistema}

## 4.1 Arquitectura Cliente – Servidor 4.2 Capas de la aplicación 4.3 Tecnologías utilizadas {#4.1-arquitectura-cliente-–-servidor-4.2-capas-de-la-aplicación-4.3-tecnologías-utilizadas}

* Java – Spring Boot

* Thymeleaf / HTML / CSS / Bootstrap

* Tomcat

* MySQL

* JDBC / JPA / Persistencia

* Git (repositorio de código)

# 5\. Implementación de Patrones de Software {#5.-implementación-de-patrones-de-software}

## 5.1 Capas 5.2 Experto en Responsabilidad 5.3 Creador 5.4 Polimorfismo 5.5 Alta Cohesión / Bajo Acoplamiento 5.6 Modelo – Vista – Controlador (MVC) 5.7 Inyección de dependencias 5.8 DTO (para reportes) 5.9 DAO (para persistencia) *(explicar dónde y cómo se usaron con diagramas y ejemplos de código)* {#5.1-capas-5.2-experto-en-responsabilidad-5.3-creador-5.4-polimorfismo-5.5-alta-cohesión-/-bajo-acoplamiento-5.6-modelo-–-vista-–-controlador-(mvc)-5.7-inyección-de-dependencias-5.8-dto-(para-reportes)-5.9-dao-(para-persistencia)-(explicar-dónde-y-cómo-se-usaron-con-diagramas-y-ejemplos-de-código)}

# 6\. Iteraciones del Proceso RUP {#6.-iteraciones-del-proceso-rup}

## 6.1 Inicio 6.2 Elaboración 6.3 Construcción 6.4 Transición *(detallar avances en cada etapa)* {#6.1-inicio-6.2-elaboración-6.3-construcción-6.4-transición-(detallar-avances-en-cada-etapa)}

# 7\. Propuesta de Mejora {#7.-propuesta-de-mejora}

## 7.1 Análisis de software de gimnasios existentes 7.2 Nuevas funcionalidades sugeridas {#7.1-análisis-de-software-de-gimnasios-existentes-7.2-nuevas-funcionalidades-sugeridas}

# 8\. Refactorización {#8.-refactorización}

## 8.1 Controladores 8.2 Dominio de la aplicación 8.3 Acceso a datos *(con propuestas y justificación)* {#8.1-controladores-8.2-dominio-de-la-aplicación-8.3-acceso-a-datos-(con-propuestas-y-justificación)}

# 9\. Resultados y Pruebas {#9.-resultados-y-pruebas}

## 9.1 Funcionalidades implementadas 9.2 Cobertura de requerimientos 9.3 Porcentaje de codificación alcanzado 9.4 Pruebas realizadas 9.5 Aplicación funcionando {#9.1-funcionalidades-implementadas-9.2-cobertura-de-requerimientos-9.3-porcentaje-de-codificación-alcanzado-9.4-pruebas-realizadas-9.5-aplicación-funcionando}

# 10\. Conclusiones {#10.-conclusiones}

## 10.1 Logros alcanzados 10.2 Limitaciones 10.3 Posibles mejoras futuras {#10.1-logros-alcanzados-10.2-limitaciones-10.3-posibles-mejoras-futuras}

# Bibliografía {#bibliografía}

# Anexos {#anexos}

* ## Capturas de pantalla de la aplicación  {#capturas-de-pantalla-de-la-aplicación}

* ## Enlace al repositorio Git  {#enlace-al-repositorio-git}

* ## Código relevante {#código-relevante}

