package com.ms.qrcode.qrcode;

import com.ms.qrcode.qrcode.dto.TipoQrCode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.net.*;
import java.nio.charset.StandardCharsets;
import static org.assertj.core.api.Assertions.*;

class QrCodeServiceTest {
    @ParameterizedTest @CsvSource({"INSCRICAO,/eventos/10","CONFIRMACAO,/eventos/10/confirmar-presenca"})
    void qrCodeApontaParaAcaoCorretaComUrlCodificada(TipoQrCode tipo,String caminho) {
        String url=new QrCodeService().gerarUrlQrCode("https://example.invalid",10L,"Tema com acentos",tipo);
        URI uri=URI.create(url);
        assertThat(uri.getScheme()).isEqualTo("https"); assertThat(uri.getHost()).isEqualTo("quickchart.io");
        String text=uri.getRawQuery().split("&")[0].substring("text=".length());
        assertThat(URLDecoder.decode(text,StandardCharsets.UTF_8)).isEqualTo("https://example.invalid"+caminho);
        assertThat(uri.getRawQuery()).contains("size=300","margin=2");
    }
}
