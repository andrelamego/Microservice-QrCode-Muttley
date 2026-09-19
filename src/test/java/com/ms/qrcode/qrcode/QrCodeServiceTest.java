package com.ms.qrcode.qrcode;

import com.ms.qrcode.qrcode.dto.TipoQrCode;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
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

    @ParameterizedTest @CsvSource({"INSCRICAO","CONFIRMACAO"})
    void geraPngValidoSemAcessarServicoExterno(TipoQrCode tipo) throws Exception {
        byte[] imagem=new QrCodeService().gerarImagem("https://example.invalid/",10L,tipo);
        assertThat(imagem).startsWith((byte)0x89,(byte)0x50,(byte)0x4e,(byte)0x47);
        assertThat(ImageIO.read(new ByteArrayInputStream(imagem))).isNotNull()
                .extracting(img -> img.getWidth(),img -> img.getHeight()).containsExactly(300,300);
        var bitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(ImageIO.read(new ByteArrayInputStream(imagem)))));
        String destino = new MultiFormatReader().decode(bitmap).getText();
        assertThat(destino).isEqualTo("https://example.invalid/eventos/10" +
                (tipo == TipoQrCode.CONFIRMACAO ? "/confirmar-presenca" : ""));
    }

    @ParameterizedTest @CsvSource({"javascript:alert(1)","https://usuario:senha@example.invalid","https://example.invalid?x=1"})
    void rejeitaBaseUrlInsegura(String baseUrl) {
        assertThatThrownBy(() -> new QrCodeService().gerarImagem(baseUrl,10L,TipoQrCode.INSCRICAO))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
