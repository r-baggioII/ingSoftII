export function toPersonaPayload(persona) {
    const localidad = persona.domicilio.localidad;
    const localidadId = persona.domicilio.localidadId ?? localidad?.id;
    if (!localidadId) {
        throw new Error('Localidad no definida en la persona');
    }
    return {
        nombre: persona.nombre,
        apellido: persona.apellido,
        dni: persona.dni,
        domicilio: {
            calle: persona.domicilio.calle,
            numero: persona.domicilio.numero,
            localidadId
        }
    };
}
export function toAutorPayload(autor) {
    return {
        nombre: autor.nombre,
        apellido: autor.apellido,
        biografia: autor.biografia
    };
}
export function toLibroPayload(libro) {
    const autorId = libro.autorId ?? libro.autor.id;
    const personaId = libro.personaId ?? libro.persona.id;
    if (!autorId || !personaId) {
        throw new Error('Libro sin identificadores de autor o persona');
    }
    return {
        titulo: libro.titulo,
        fecha: libro.fecha,
        genero: libro.genero,
        paginas: libro.paginas,
        autorId,
        personaId
    };
}
