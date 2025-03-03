package nl.lunarcloud.domotics.chargerservice.controller;

import lombok.RequiredArgsConstructor;
import nl.lunarcloud.domotics.chargerservice.api.ReportsApi;
import nl.lunarcloud.domotics.chargerservice.common.exception.RecordNotFoundException;
import nl.lunarcloud.domotics.chargerservice.report.ReportService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class ReportController implements ReportsApi {

    private final ReportService reportService;

    @Override
    public ResponseEntity<Resource> getReport(LocalDate startDate, LocalDate endDate) {
        File file = reportService.generateReport(startDate, endDate).orElseThrow(() -> new RecordNotFoundException("Vehicle not found"));
        FileSystemResource resource = new FileSystemResource(file);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        return ResponseEntity.ok().headers(headers).body(resource);

    }
}
