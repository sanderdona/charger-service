package nl.dimensiontech.domotics.chargerservice.mapper;

import lombok.RequiredArgsConstructor;
import nl.dimensiontech.domotics.chargerservice.api.model.ChargeSessionPageDto;
import nl.dimensiontech.domotics.chargerservice.api.model.PageProofDto;
import nl.dimensiontech.domotics.chargerservice.domain.ChargeSession;
import nl.dimensiontech.domotics.chargerservice.domain.Proof;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PageMapper {

    private final ChargeSessionMapper chargeSessionMapper;
    private final ProofMapper proofMapper;

    public ChargeSessionPageDto toChargeSessionPageDto(Page<ChargeSession> page) {
        ChargeSessionPageDto chargeSessionPageDto = new ChargeSessionPageDto();
        chargeSessionPageDto.setContent(chargeSessionMapper.toDto(page.getContent()));
        return chargeSessionPageDto;
    }

    public PageProofDto toProofDto(Page<Proof> page) {
        PageProofDto pageProofDto = new PageProofDto();
        pageProofDto.setContent(proofMapper.toDto(page.getContent()));
        return pageProofDto;
    }
}
