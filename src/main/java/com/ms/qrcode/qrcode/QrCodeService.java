package com.ms.qrcode.qrcode;

import com.ms.qrcode.qrcode.dto.TipoQrCode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class QrCodeService {

    private static final String QUICKCHART_BASE = "https://quickchart.io/qr";

    public String gerarUrlQrCode(String baseUrl, Long eventoId, String eventoTema, TipoQrCode tipo) {
        String urlDestino = destino(baseUrl, eventoId, tipo);

        String encoded = URLEncoder.encode(urlDestino, StandardCharsets.UTF_8);
        return QUICKCHART_BASE + "?text=" + encoded + "&size=300&margin=2";
    }

    public byte[] gerarImagem(String baseUrl, Long eventoId, TipoQrCode tipo) throws WriterException, IOException {
        var matriz = new QRCodeWriter().encode(destino(baseUrl, eventoId, tipo), BarcodeFormat.QR_CODE,
                300, 300, Map.of(EncodeHintType.CHARACTER_SET, "UTF-8", EncodeHintType.MARGIN, 2));
        try (var output = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(matriz, "PNG", output);
            return output.toByteArray();
        }
    }

    private String destino(String baseUrl, Long eventoId, TipoQrCode tipo) {
        if (baseUrl == null || eventoId == null || eventoId <= 0 || tipo == null) {
            throw new IllegalArgumentException("Base URL, evento e tipo de QR Code são obrigatórios.");
        }
        URI uri = URI.create(baseUrl);
        if (!("https".equals(uri.getScheme()) || "http".equals(uri.getScheme()))
                || uri.getHost() == null || uri.getRawQuery() != null || uri.getRawFragment() != null
                || uri.getUserInfo() != null) {
            throw new IllegalArgumentException("Base URL deve ser HTTP(S), sem credenciais, query ou fragmento.");
        }
        String base = baseUrl.replaceAll("/+$", "");
        return base + "/eventos/" + eventoId + (tipo == TipoQrCode.CONFIRMACAO ? "/confirmar-presenca" : "");
    }

    public byte[] baixarQrCode(String qrCodeUrl) throws Exception {
        return new URL(qrCodeUrl).openStream().readAllBytes();
    }
}
