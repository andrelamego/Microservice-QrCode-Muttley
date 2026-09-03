package com.ms.qrcode.qrcode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.qrcode.qrcode.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.kafka.core.KafkaTemplate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class QrCodeConsumerTest {
    @ParameterizedTest @EnumSource(TipoQrCode.class)
    void sucessoRespondeComMesmaIdentidadeETipo(TipoQrCode tipo) throws Exception {
        var service=mock(QrCodeService.class); KafkaTemplate<String,QrCodeResponse> kafka=mock(KafkaTemplate.class);
        when(service.gerarUrlQrCode("https://example.invalid",10L,"Tema",tipo)).thenReturn("https://example.invalid/qr");
        new QrCodeConsumer(service,kafka).consumir(new ObjectMapper().writeValueAsString(new QrCodeRequest(10L,"https://example.invalid","Tema",tipo)));
        verify(kafka).send("qrcode.gerar.response","10-"+tipo,new QrCodeResponse(10L,"https://example.invalid/qr","SUCCESS",null,tipo));
    }
    @Test void falhaRespondeErrorSemUrl() throws Exception {
        var service=mock(QrCodeService.class); KafkaTemplate<String,QrCodeResponse> kafka=mock(KafkaTemplate.class);
        when(service.gerarUrlQrCode(anyString(),anyLong(),anyString(),any())).thenThrow(new IllegalStateException("falha simulada"));
        new QrCodeConsumer(service,kafka).consumir("{\"eventoId\":10,\"baseUrl\":\"https://example.invalid\",\"tema\":\"Tema\",\"tipo\":\"INSCRICAO\"}");
        verify(kafka).send("qrcode.gerar.response","10-INSCRICAO",new QrCodeResponse(10L,null,"ERROR","falha simulada",TipoQrCode.INSCRICAO));
    }
    @Test void jsonInvalidoNaoPublicaRespostaDeSucesso() {
        var service=mock(QrCodeService.class); KafkaTemplate<String,QrCodeResponse> kafka=mock(KafkaTemplate.class);
        assertThatThrownBy(() -> new QrCodeConsumer(service,kafka).consumir("{invalido"))
                .isInstanceOf(JsonProcessingException.class);
        verifyNoInteractions(service,kafka);
    }
}
