package nl.lunarcloud.domotics.chargerservice.mapper;

import nl.lunarcloud.domotics.chargerservice.api.model.PageableApi;
import nl.lunarcloud.domotics.chargerservice.api.model.PageableSortInnerApi;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PageableMapper {

    public PageRequest map(PageableApi pageableApi) {

        List<Sort.Order> sortOrders = pageableApi.getSort().stream()
                .filter(sortApi -> sortApi.getProperty() != null && sortApi.getDirection() != null)
                .map(sortApi -> new Sort.Order(map(sortApi.getDirection()), sortApi.getProperty()))
                .toList();

        return PageRequest.of(pageableApi.getPageNumber(), pageableApi.getPageSize(), Sort.by(sortOrders));
    }

    private Sort.Direction map(PageableSortInnerApi.DirectionEnum direction) {
        return switch (direction) {
            case ASC -> Sort.Direction.ASC;
            case DESC -> Sort.Direction.DESC;
        };
    }
}
