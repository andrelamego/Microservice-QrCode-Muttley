package com.ms.qrcode.qrcode;

import com.ms.qrcode.qrcode.dto.QrCodeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/qrcode")
@RequiredArgsConstructor
public class QrCodeController {

    private final QrCodeService qrCodeService;

    @PostMapping(value = "/gerar", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> gerar(@RequestBody QrCodeRequest request) throws Exception {
        try {
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG)
                    .body(qrCodeService.gerarImagem(request.baseUrl(), request.eventoId(), request.tipo()));
        } catch (IllegalArgumentException erro) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, erro.getMessage());
        }
    }

    @GetMapping("/baixar")
    public ResponseEntity<byte[]> baixar(@RequestParam String url) throws Exception {
        byte[] bytes = qrCodeService.baixarQrCode(url);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"qrcode.png\"")
                .body(bytes);
    }
}
