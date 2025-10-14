package com.ejemplo.biblioteca.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface PdfStorageService {

    String storePdf(MultipartFile file, String slugTitulo, Long libroId);

    Resource loadPdfAsResource(String fileName);

    void deletePdfIfExists(String fileName);
}
