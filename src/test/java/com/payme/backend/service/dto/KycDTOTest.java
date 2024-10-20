package com.payme.backend.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.payme.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class KycDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(KycDTO.class);
        KycDTO kycDTO1 = new KycDTO();
        kycDTO1.setId(1L);
        KycDTO kycDTO2 = new KycDTO();
        assertThat(kycDTO1).isNotEqualTo(kycDTO2);
        kycDTO2.setId(kycDTO1.getId());
        assertThat(kycDTO1).isEqualTo(kycDTO2);
        kycDTO2.setId(2L);
        assertThat(kycDTO1).isNotEqualTo(kycDTO2);
        kycDTO1.setId(null);
        assertThat(kycDTO1).isNotEqualTo(kycDTO2);
    }
}
