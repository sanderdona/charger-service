package nl.dimensiontech.domotics.chargerservice.mapper;

import nl.dimensiontech.domotics.chargerservice.api.model.ProofDto;
import nl.dimensiontech.domotics.chargerservice.domain.Proof;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ProofMapper {

    ProofDto toDto(Proof source);

    List<ProofDto> toDto(List<Proof> source);
}
