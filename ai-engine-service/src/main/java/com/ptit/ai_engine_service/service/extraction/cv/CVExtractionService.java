package com.ptit.ai_engine_service.service.extraction.cv;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CVExtractionService {

    /**
     * Trích xuất văn bản từ file PDF CV.
     * 
     * @param fileData
     * @return
     * @throws IOException
     *                     1. Kiểm tra dữ liệu file PDF có null hoặc rỗng không, nếu
     *                     có thì ném ra IllegalArgumentException.
     *                     2. Sử dụng thư viện PDFBox để load file PDF từ dữ liệu
     *                     byte.
     *                     3. Kiểm tra xem file PDF có bị mã hóa không, nếu có thì
     *                     ném ra IllegalArgumentException.
     *                     4. Sử dụng PDFTextStripper để trích xuất văn bản từ file
     *                     PDF.
     *                     5. Kiểm tra xem văn bản trích xuất có null hoặc rỗng
     *                     không, nếu có thì ném ra IllegalStateException.
     *                     6. Loại bỏ các ký tự null, tab, khoảng trắng thừa và các
     *                     dòng trống thừa trong văn bản trích xuất.
     *                     7. Trả về văn bản đã được làm sạch.
     */
    public String extract(byte[] fileData) throws IOException {

        // Kiểm tra dữ liệu file PDF có null hoặc rỗng không, nếu có thì ném ra
        // IllegalArgumentException.
        if (fileData == null || fileData.length == 0) {
            throw new IllegalArgumentException(
                    "CV PDF data must not be empty.");
        }

        // Sử dụng thư viện PDFBox để load file PDF từ dữ liệu byte.
        try (PDDocument document = Loader.loadPDF(fileData)) {
            // Kiểm tra xem file PDF có bị mã hóa không, nếu có thì ném ra
            // IllegalArgumentException.
            if (document.isEncrypted()) {
                throw new IllegalArgumentException(
                        "Encrypted PDF files are not supported.");
            }

            // Sử dụng PDFTextStripper để trích xuất văn bản từ file PDF.
            PDFTextStripper stripper = new PDFTextStripper();

            String rawText = stripper.getText(document);

            // Kiểm tra xem văn bản trích xuất có null hoặc rỗng không, nếu có thì ném ra
            // IllegalStateException.
            if (rawText == null || rawText.isBlank()) {
                throw new IllegalStateException(
                        "No readable text found in CV PDF.");
            }

            // Loại bỏ các ký tự null, tab, khoảng trắng thừa và các dòng trống thừa trong
            // văn bản trích xuất.
            return rawText
                    .replace("\u0000", "")
                    .replaceAll("[\\t ]+", " ")
                    .replaceAll("\\n{3,}", "\n\n")
                    .trim();
        }
    }
}