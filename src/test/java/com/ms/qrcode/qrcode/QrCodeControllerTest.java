package com.ms.qrcode.qrcode;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class QrCodeControllerTest {
    @Test void geraQrCodePeloContratoHttpSemKafkaOuInternet() throws Exception {
        var mvc = MockMvcBuilders.standaloneSetup(new QrCodeController(new QrCodeService())).build();
        byte[] imagem = mvc.perform(post("/api/qrcode/gerar").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"eventoId":10,"baseUrl":"https://example.invalid","tema":"Evento","tipo":"CONFIRMACAO"}
                                """))
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andReturn().getResponse().getContentAsByteArray();
        assertThat(imagem).startsWith((byte) 0x89, (byte) 0x50, (byte) 0x4e, (byte) 0x47);
    }

    @Test void dadosObrigatoriosAusentesRetornam400() throws Exception {
        var mvc = MockMvcBuilders.standaloneSetup(new QrCodeController(new QrCodeService())).build();
        mvc.perform(post("/api/qrcode/gerar").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }
}
