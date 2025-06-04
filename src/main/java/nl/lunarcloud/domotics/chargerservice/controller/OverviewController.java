package nl.lunarcloud.domotics.chargerservice.controller;

import lombok.RequiredArgsConstructor;
import nl.lunarcloud.domotics.chargerservice.domain.ChargeSession;
import nl.lunarcloud.domotics.chargerservice.service.ChargeSessionService;
import nl.lunarcloud.domotics.chargerservice.service.EnergyMeterService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Controller
@RequiredArgsConstructor
public class OverviewController {

    private final ChargeSessionService chargeSessionService;
    private final EnergyMeterService energyMeterService;

    @GetMapping("/overview")
    public String overview(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startedAt").descending());
        Page<ChargeSession> sessionsPage = chargeSessionService.getSessions(pageable);

        Instant instant = Instant.ofEpochMilli(energyMeterService.getCurrentTimestamp());
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        model.addAttribute("energyMeterTimestamp", localDateTime);
        model.addAttribute("energyMeterReading", energyMeterService.getCurrentReading());
        model.addAttribute("sessionsPage", sessionsPage);
        return "overview";
    }
}
